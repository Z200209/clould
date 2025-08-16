package com.example.provider.service.sms;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.example.common.annotations.DataSource;
import com.example.common.config.mysql.DataSourceType;
import com.example.common.entity.Sms;
import com.example.common.entity.SmsTaskCrond;
import com.example.common.utils.BaseUtils;
import com.example.provider.mapper.sms.SmsMapper;
import com.example.provider.mapper.sms.SmsTaskCrondMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class SmsService {


    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Resource
    private SmsMapper smsMapper;

    @Resource
    private SmsTaskCrondMapper smsTaskCrondMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    String templateCode = "SMS_154950909";
    String templateName = "阿里云短信测试";

    /**
     * 创建阿里云短信客户端
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        return new Client(config);
    }

    /**
     * 方式1：同步发送短信
     */
    @DataSource(DataSourceType.MASTER)
    public boolean sendSms(String phone, String templateParam) {
        try {
            Client client = createClient();
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName("阿里云短信测试")
                    .setTemplateCode("SMS_154950909")
                    .setTemplateParam(templateParam);

            SendSmsResponse response = client.sendSms(request);
            String code = response.getBody().getCode();
            if (!"OK".equals(code)) {
                log.error("短信发送失败，响应码: {}, 消息: {}, 手机号: {}", code, response.getBody().getMessage(), phone);
            }

            // 记录发送结果
            Sms sms = new Sms();
            sms.setPhone(phone);
            sms.setContent(templateParam);
            sms.setTemplateCode(templateCode);
            sms.setTemplateParam(templateParam);
            sms.setStatus("OK".equals(code) ? 1 : 2);
            sms.setBizId(response.getBody().getBizId());
            sms.setIsDeleted(0);
            Integer currentTime = BaseUtils.currentSeconds();
            sms.setSendTime(currentTime);
            sms.setCreateTime(currentTime);
            sms.setUpdateTime(currentTime);
            smsMapper.insert(sms);
            return "OK".equals(code);
        } catch (java.net.SocketTimeoutException e) {
            log.error("短信发送连接超时，手机号: {}, 错误: {}", phone, e.getMessage());
            return false;
        } catch (java.net.ConnectException e) {
            log.error("短信发送连接失败，手机号: {}, 错误: {}", phone, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("短信发送异常，手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 多线程批量发送短信，使用StringBuilder收集结果
     */
    @DataSource(DataSourceType.MASTER)
    public String sendSmsMultiThread(List<String> phoneList, String templateParam) {
        StringBuilder result = new StringBuilder();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String phone : phoneList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                boolean success = sendSms(phone, templateParam);
                synchronized (result) {
                    result.append("手机号: ").append(phone)
                            .append(", 发送结果: ").append(success ? "成功" : "失败")
                            .append("\n");
                }
            }, executorService);
            futures.add(future);
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return result.toString();
    }

    /**
     * 根据手机号查询短信记录
     */
    @DataSource(DataSourceType.SLAVE)
    public List<Sms> getSmsRecordsByPhone(String phone) {
        return smsMapper.findByPhone(phone);
    }

    /**
     * 添加短信任务（按顺序发送）
     */
    @DataSource(DataSourceType.MASTER)
    public boolean addSmsTask(String phone, String templateParam) {
        try {
            SmsTaskCrond task = new SmsTaskCrond();
            task.setPhone(phone);
            task.setTemplateParam(templateParam);
            task.setStatus(0); // 初始状态
            Integer currentTime = BaseUtils.currentSeconds();
            task.setIsDeleted(0);
            task.setCreateTime(currentTime);
            task.setUpdateTime(currentTime);

            int result = smsTaskCrondMapper.insert(task);
            log.info("添加短信任务成功，手机号: {}", phone);
            return result > 0;
        } catch (Exception e) {
            log.error("添加短信任务失败，手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 执行待处理的短信任务（按创建时间顺序）
     */
    @DataSource(DataSourceType.MASTER)
    public void executePendingTasks() {
        try {
            List<SmsTaskCrond> pendingTasks = smsTaskCrondMapper.findPendingTasks(100);

            log.info("开始执行待处理短信任务，共 {} 个任务", pendingTasks.size());

            for (SmsTaskCrond task : pendingTasks) {
                try {
                    // 发送短信
                    boolean success = sendSms(task.getPhone(), task.getTemplateParam());

                    // 更新任务状态
                    Integer executeTime = BaseUtils.currentSeconds();
                    Integer status = success ? 1 : 2; // 1-完成 2-失败
                    smsTaskCrondMapper.updateStatus(task.getId(), status, executeTime, executeTime);

                    log.info("短信任务执行完成，任务ID: {}, 手机号: {}, 结果: {}",
                            task.getId(), task.getPhone(), success ? "成功" : "失败");
                } catch (Exception e) {
                    // 任务执行失败，标记为失败状态
                    Integer executeTime = BaseUtils.currentSeconds();
                    smsTaskCrondMapper.updateStatus(task.getId(), 2, executeTime, executeTime);
                    log.error("短信任务执行异常，任务ID: {}, 手机号: {}, 错误: {}",
                            task.getId(), task.getPhone(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("执行待处理短信任务异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据手机号查询短信任务
     */
    @DataSource(DataSourceType.SLAVE)
    public List<SmsTaskCrond> getSmsTasksByPhone(String phone) {
        return smsTaskCrondMapper.findByPhone(phone);
    }

    /**
     * 根据ID查询短信任务
     */
    @DataSource(DataSourceType.SLAVE)
    public SmsTaskCrond getSmsTaskById(java.math.BigInteger id) {
        return smsTaskCrondMapper.findById(id);
    }

    /**
     * 删除短信任务（逻辑删除）
     */
    @DataSource(DataSourceType.MASTER)
    public boolean deleteSmsTask(java.math.BigInteger id) {
        try {
            Integer currentTime = BaseUtils.currentSeconds();
            int result = smsTaskCrondMapper.delete(id, currentTime);
            return result > 0;
        } catch (Exception e) {
            log.error("删除短信任务失败，任务ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

}
