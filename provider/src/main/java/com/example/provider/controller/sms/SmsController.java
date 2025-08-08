package com.example.provider.controller.sms;


import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.Sms;
import com.example.common.entity.SmsTaskCrond;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.provider.controller.domain.sms.SmsVO;
import com.example.provider.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/console/sms")
public class SmsController {
    
    @Autowired
    private SmsService smsService;

    /**
     * 发送单条短信（同步）
     */
    @RequestMapping("/send")
    public Response sendSms(@VerifiedUser User loginUser,
                            @RequestParam("phone") String phone,
                            @RequestParam("templateParam") String templateParam) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试发送短信");
            return new Response(1002); // 没有登录
        }

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }
        if (templateParam == null || templateParam.trim().isEmpty()) {
            log.info("模板参数不能为空");
            return new Response(4005); // 请求参数错误
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            log.info("手机号格式不正确");
            return new Response(4005); // 请求参数错误
        }


        String actualTemplateParam = templateParam;
        if (templateParam != null && templateParam.matches("^\\d{4,6}$")) {
            actualTemplateParam = "{\"code\":\"" + templateParam + "\"}";
        }

        log.info("用户 {} 发送短信，手机号: {}, 模板参数: {}", loginUser.getId(), phone, actualTemplateParam);

        try {
            boolean success = smsService.sendSms(phone, actualTemplateParam);
            if (success) {
                return new Response(1001); // 成功
            } else {
                return new Response(4004); // 链接超时
            }
        } catch (Exception e) {
            log.error("发送短信异常: {}", e.getMessage(), e);
            return new Response(4004); // 链接超时
        }
    }

    /**
     * 查询短信发送记录
     */
    @RequestMapping("/records")
    public Response getSmsRecords(@VerifiedUser User loginUser,
                                 @RequestParam("phone") String phone) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信记录");
            return new Response(1002); // 没有登录
        }

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            log.info("手机号格式不正确");
            return new Response(4005); // 请求参数错误
        }

        log.info("用户 {} 查询短信记录，手机号: {}", loginUser.getId(), phone);

        try {
            List<Sms> smsList = smsService.getSmsRecordsByPhone(phone);
            List<SmsVO> smsVOList = smsList.stream().map(sms -> {
                SmsVO smsVO = new SmsVO();
                BeanUtils.copyProperties(sms, smsVO);
                return smsVO;
            }).collect(Collectors.toList());

            return new Response(1001, smsVOList); // 成功
        } catch (Exception e) {
            log.error("查询短信记录异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }


    /**
     * 添加短信任务
     */
    @RequestMapping( "/task/add")
    public Response addSmsTask(@VerifiedUser User loginUser,
                              @RequestParam("phone") String phone,
                              @RequestParam("templateParam") String templateParam) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试添加短信任务");
            return new Response(1002); // 没有登录
        }

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }
        if (templateParam == null || templateParam.trim().isEmpty()) {
            log.info("模板参数不能为空");
            return new Response(4005); // 请求参数错误
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            log.info("手机号格式不正确");
            return new Response(4005); // 请求参数错误
        }

        String actualTemplateParam = templateParam;
        if (templateParam != null && templateParam.matches("^\\d{4,6}$")) {
            actualTemplateParam = "{\"code\":\"" + templateParam + "\"}";
        }
        
        log.info("用户 {} 添加短信任务，手机号: {}, 模板参数: {}", 
                loginUser.getId(), phone, actualTemplateParam);
        
        try {
            boolean success = smsService.addSmsTask(phone, actualTemplateParam);
            if (success) {
                return new Response(1001); // 成功
            } else {
                return new Response(4004); // 操作失败
            }
        } catch (Exception e) {
            log.error("添加短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 查询短信任务记录
     */
    @RequestMapping("/task/records")
    public Response getSmsTaskRecords(@VerifiedUser User loginUser,
                                     @RequestParam("phone") String phone) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信任务");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }
        
        try {
            List<SmsTaskCrond> taskList = smsService.getSmsTasksByPhone(phone);
            return new Response(1001, taskList); // 成功
        } catch (Exception e) {
            log.error("查询短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 根据ID查询短信任务
     */
    @RequestMapping("/task/{id}")
    public Response getSmsTaskById(@VerifiedUser User loginUser,
                                  @PathVariable("id") java.math.BigInteger id) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信任务详情");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (id == null) {
            return new Response(4005); // 请求参数错误
        }
        
        try {
            SmsTaskCrond task = smsService.getSmsTaskById(id);
            if (task != null) {
                return new Response(1001, task); // 成功
            } else {
                return new Response(4006); // 数据不存在
            }
        } catch (Exception e) {
            log.error("查询短信任务详情异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 删除短信任务
     */
    @RequestMapping("/task/delete/{id}")
    public Response deleteSmsTask(@VerifiedUser User loginUser,
                                 @PathVariable("id") java.math.BigInteger id) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除短信任务");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (id == null) {
            return new Response(4005); // 请求参数错误
        }
        
        log.info("用户 {} 删除短信任务，任务ID: {}", loginUser.getId(), id);
        
        try {
            boolean success = smsService.deleteSmsTask(id);
            if (success) {
                return new Response(1001); // 成功
            } else {
                return new Response(4006); // 数据不存在
            }
        } catch (Exception e) {
            log.error("删除短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 手动触发执行待处理任务
     */
    @RequestMapping("/task/execute")
    public Response executePendingTasks(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试执行待处理任务");
            return new Response(1002); // 没有登录
        }
        
        log.info("用户 {} 手动触发执行待处理短信任务", loginUser.getId());
        
        try {
            smsService.executePendingTasks();
            return new Response(1001, "任务执行完成"); // 成功
        } catch (Exception e) {
            log.error("执行待处理任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
}