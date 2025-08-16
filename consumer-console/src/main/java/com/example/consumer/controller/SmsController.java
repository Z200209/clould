package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.Sms;
import com.example.common.entity.SmsTaskCrond;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.controller.domain.sms.SmsVO;
import com.example.consumer.feign.SmsServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/console/sms")
public class SmsController {
    @Resource
    private SmsServiceFeign smsServiceFeign;

    @RequestMapping("/send")
    public Response sendSms(@VerifiedUser User loginUser,
                            @RequestParam("phone") String phone,
                            @RequestParam("templateParam") String templateParam) {
        if (loginUser == null){
            return new Response(1002);
        }
        try {
            boolean result = smsServiceFeign.sendSms(phone, templateParam);
            return result ? new Response(1001) : new Response(4004);
        }catch (Exception e){
            log.error("发送短信失败：{}", e.getMessage());
            return new Response(4004, "发送短信失败");
        }
    }


    @RequestMapping("/records")
    public Response getSmsRecords(@VerifiedUser User loginUser,
                              @RequestParam("phone") String phone) {
        if (loginUser == null){
            return new Response(1002);
        }
        try {
            List<Sms> smsList = smsServiceFeign.getSmsRecords(phone);
            List<SmsVO> smsRecords = new ArrayList<>();
            SmsVO smsVO = null;
            for (Sms sms : smsList) {
                smsVO = new SmsVO()
                        .setId(sms.getId())
                        .setPhone(sms.getPhone())
                        .setContent(sms.getContent())
                        .setTemplateCode(sms.getTemplateCode())
                        .setTemplateParam(sms.getTemplateParam())
                        .setStatus(sms.getStatus())
                        .setBizId(sms.getBizId())
                        .setCreateTime(sms.getCreateTime())
                        .setUpdateTime(sms.getUpdateTime())
                        .setSendTime(sms.getSendTime());
            }
            smsRecords.add(smsVO);
            return new Response(1001, smsRecords);

        } catch (Exception e) {
           log.error("获取短信记录失败", e);
           return new Response(4004, "获取短信记录失败: " + e.getMessage());
        }
    }

    @RequestMapping("/task/add")
    public Response addSmsTask(@VerifiedUser User loginUser,
                             @RequestParam("phone") String phone,
                             @RequestParam("templateParam") String templateParam){
        if (loginUser == null) {
            return new Response(1002); // 没有登录
        }
        try {
            boolean result = smsServiceFeign.addSmsTask(phone, templateParam);
            return result ? new Response(1001) : new Response(4004, "添加短信任务失败");
        }catch (Exception e){
            log.error("添加短信任务异常: {}", e.getMessage(), e);
            return new Response(4004);
        }
    }

    @RequestMapping("/task/records")
    public Response getSmsTaskRecords(
            @VerifiedUser User loginUser,
            @RequestParam("phone") String phone) {
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信任务");
            return new Response(1002); // 没有登录
        }
        try {
            List<SmsTaskCrond> taskRecordList = smsServiceFeign.getSmsTaskRecords(phone);
            return !taskRecordList.isEmpty() ? new Response(1001, taskRecordList) : new Response(4004);
        }catch (Exception e){
            log.error("用户 {} 查询短信任务失败: {}", loginUser.getId(), e.getMessage(), e);
            return new Response(5001, "查询短信任务失败: " + e.getMessage());
        }
    }

    @RequestMapping("/task")
    public Response getSmsTaskById(
            @VerifiedUser User loginUser,
            @RequestParam(name = "id") BigInteger id) {
        if (loginUser == null) {
            return new Response(1002); // 没有登录
        }
        try {
            SmsTaskCrond task = smsServiceFeign.getSmsTaskById( id);
            return task != null ? new Response(1001, task) : new Response(4006);
        } catch (Exception e) {
            log.error("查询短信任务详情异常", e);
            return new Response(5000);
        }
    }

    @RequestMapping("/task/delete")
    public Response deleteSmsTask(
            @VerifiedUser User loginUser,
            @RequestParam(name = "id") BigInteger id){
        if (loginUser == null){
            return new Response(1002);
        }
        try {
            boolean result = smsServiceFeign.deleteSmsTask(id);
            return result ? new Response(1001) : new Response(4004);
        }catch (Exception e){
            log.error("删除短信任务异常: {}", e.getMessage(), e);
            return new Response(4004);
        }

    }

    @RequestMapping("/task/execute")
    public Response executePendingTasks(@VerifiedUser User loginUser) {
        if (loginUser == null){
            log.warn("未登录用户尝试执行待处理任务");
            return new Response(1002);
        }
        try {
            boolean result = smsServiceFeign.executePendingTasks();
            return result ? new Response(1001, "任务执行完成") : new Response(4004);
        } catch (Exception e) {
            log.error("执行待处理任务异常: {}", e.getMessage(), e);
            return new Response(4004);
        }
    }

}
