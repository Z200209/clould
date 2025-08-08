package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.consumer.feign.SmsServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/console/sms")
public class SmsController {
    @Resource
    private SmsServiceFeign smsServiceFeign;

    @RequestMapping("/send")
    public Object sendSms(@VerifiedUser User loginUser,
                         @RequestParam("phone") String phone,
                         @RequestParam("templateParam") String templateParam) {
        return smsServiceFeign.sendSms(loginUser, phone, templateParam);
    }


    @RequestMapping("/records")
    public Object getSmsRecords(@VerifiedUser User loginUser,
                              @RequestParam("phone") String phone) {
        return smsServiceFeign.getSmsRecords(loginUser, phone);
    }

    @RequestMapping("/task/add")
    public Object addSmsTask(@VerifiedUser User loginUser,
                             @RequestParam("phone") String phone,
                             @RequestParam("templateParam") String templateParam){
        return smsServiceFeign.addSmsTask(loginUser, phone, templateParam);
    }

    @RequestMapping("/task/records")
    public Object getSmsTaskRecords(@VerifiedUser User loginUser,
                                 @RequestParam("phone") String phone) {
        return smsServiceFeign.getSmsTaskRecords(loginUser, phone);
    }

    @RequestMapping("/task/{id}")
    public Object getSmsTaskById(@VerifiedUser User loginUser,
                               @PathVariable("id") java.math.BigInteger id) {
        return smsServiceFeign.getSmsTaskById(loginUser, id);
    }

    @RequestMapping("/task/delete/{id}")
    public Object deleteSmsTask(@VerifiedUser User loginUser,
                                @PathVariable("id") java.math.BigInteger id){
        return smsServiceFeign.deleteSmsTask(loginUser, id);
    }

    @RequestMapping("/task/execute")
    public Object executePendingTasks(@VerifiedUser User loginUser) {
        return smsServiceFeign.executePendingTasks(loginUser);
    }

}
