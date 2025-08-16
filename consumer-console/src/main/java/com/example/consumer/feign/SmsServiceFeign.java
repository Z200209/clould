package com.example.consumer.feign;

import com.example.common.entity.Sms;
import com.example.common.entity.SmsTaskCrond;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;

@FeignClient(name = "provider", contextId = "smsServiceFeign", path = "/sms")
public interface SmsServiceFeign {
    @RequestMapping("/send")
    boolean sendSms(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "templateParam") String templateParam);

    @RequestMapping("/records")
    List<Sms> getSmsRecords(
            @RequestParam("phone") String phone);

    @RequestMapping("/task/add")
    boolean addSmsTask(
            @RequestParam("phone") String phone,
            @RequestParam("templateParam") String templateParam);

    @RequestMapping("/task/records")
    List<SmsTaskCrond> getSmsTaskRecords(
            @RequestParam("phone") String phone);

    @RequestMapping("/task")
    SmsTaskCrond getSmsTaskById(
            @RequestParam(name = "id") BigInteger id);

    @RequestMapping("/task/delete")
    boolean deleteSmsTask(
            @RequestParam(name = "id") BigInteger id);

    @RequestMapping("/task/execute")
    boolean executePendingTasks();

}
