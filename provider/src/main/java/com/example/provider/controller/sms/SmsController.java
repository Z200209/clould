package com.example.provider.controller.sms;

import com.example.common.entity.Sms;
import com.example.common.entity.SmsTaskCrond;
import com.example.provider.controller.domain.sms.SmsVO;
import com.example.provider.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
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
    public boolean sendSms(
            @RequestParam("phone") String phone,
            @RequestParam("templateParam") String templateParam) {

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (templateParam == null || templateParam.trim().isEmpty()) {
            throw new RuntimeException("模板参数不能为空");
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }


        String actualTemplateParam = templateParam;
        if (templateParam != null && templateParam.matches("^\\d{4,6}$")) {
            actualTemplateParam = "{\"code\":\"" + templateParam + "\"}";
        }

        try {
            boolean success = smsService.sendSms(phone, actualTemplateParam);
            if (success) {
                return true;
            } else {
                throw new RuntimeException("连接超时");
            }
        } catch (Exception e) {
            throw new RuntimeException("发送短信异常: {}", e);
        }
    }

    /**
     * 查询短信发送记录
     */
    @RequestMapping("/records")
    public List<SmsVO> getSmsRecords(
            @RequestParam("phone") String phone) {

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }

        try {
            List<Sms> smsList = smsService.getSmsRecordsByPhone(phone);
            List<SmsVO> smsVOList = smsList.stream().map(sms -> {
                SmsVO smsVO = new SmsVO();
                BeanUtils.copyProperties(sms, smsVO);
                return smsVO;
            }).collect(Collectors.toList());
            return smsVOList; // 成功
        } catch (Exception e) {
            throw new RuntimeException("查询短信记录异常: {}",e);
        }
    }


    /**
     * 添加短信任务
     */
    @RequestMapping( "/task/add")
    public boolean addSmsTask(
            @RequestParam("phone") String phone,
            @RequestParam("templateParam") String templateParam) {

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (templateParam == null || templateParam.trim().isEmpty()) {
            log.info("模板参数不能为空");
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }

        String actualTemplateParam = templateParam;
        if (templateParam != null && templateParam.matches("^\\d{4,6}$")) {
            actualTemplateParam = "{\"code\":\"" + templateParam + "\"}";
        }
        
        try {
            boolean success = smsService.addSmsTask(phone, actualTemplateParam);
            if (success) {
                return true;
            } else {
                throw new RuntimeException("短信添加失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("添加短信任务异常: {}",e);
        }
    }
    
    /**
     * 查询短信任务记录
     */
    @RequestMapping("/task/records")
    public List<SmsTaskCrond> getSmsTaskRecords(
            @RequestParam("phone") String phone) {
        
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        
        try {
            return smsService.getSmsTasksByPhone(phone); // 成功
        } catch (Exception e) {
            throw new RuntimeException("查询短信任务异常: {}", e);
        }
    }
    
    /**
     * 根据ID查询短信任务
     */
    @RequestMapping("/task")
    public SmsTaskCrond getSmsTaskById(
            @RequestParam(name = "id") BigInteger id) {

        // 参数验证
        if (id == null) {
            throw new RuntimeException("根据ID查询短信任务,ID为空");
        }
        
        try {
            SmsTaskCrond task = smsService.getSmsTaskById(id);
            if (task != null) {
                return task; // 成功
            } else {
                throw new RuntimeException("数据不存在"); //
            }
        } catch (Exception e) {
            throw new RuntimeException("查询短信任务详情异常: {}", e);
        }
    }
    
    /**
     * 删除短信任务
     */
    @RequestMapping("/task/delete")
    public boolean deleteSmsTask(@RequestParam(name = "id") BigInteger id) {

        // 参数验证
        if (id == null) {
            throw new RuntimeException("删除短信任务,缺少参数");
        }
        
        try {
            boolean success = smsService.deleteSmsTask(id);
            if (success) {
                return true;
            } else {
                throw new RuntimeException("删除短信任务失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除短信任务异常: {}",e);
        }
    }
    
    /**
     * 手动触发执行待处理任务
     */
    @RequestMapping("/task/execute")
    public boolean executePendingTasks() {
        try {
            smsService.executePendingTasks();
            return true; // 成功
        } catch (Exception e) {
            throw new RuntimeException("执行待处理任务异常: {}",e);
        }
    }
}