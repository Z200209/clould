package com.example.consumer.feign;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="provider", contextId = "smsServiceFeign", path = "/console/sms")
public interface SmsServiceFeign {
    @RequestMapping("/send")
    Response sendSms(@VerifiedUser User loginUser,
                     @RequestParam(name = "phone") String phone,
                     @RequestParam(name = "templateParam") String templateParam);

    @RequestMapping("/records")
    Response getSmsRecords(@VerifiedUser User loginUser,
                           @RequestParam("phone") String phone);

    @RequestMapping("/task/add")
    Response addSmsTask(@VerifiedUser User loginUser,
                       @RequestParam("phone") String phone,
                       @RequestParam("templateParam") String templateParam);

    @RequestMapping("/task/records")
    Response getSmsTaskRecords(@VerifiedUser User loginUser,
                              @RequestParam("phone") String phone);

    @RequestMapping("/task/{id}")
    Response getSmsTaskById(@VerifiedUser User loginUser,
                           @PathVariable("id") java.math.BigInteger id);

    @RequestMapping("/task/delete/{id}")
    Response deleteSmsTask(@VerifiedUser User loginUser,
                         @PathVariable("id") java.math.BigInteger id);

    @RequestMapping("/task/execute")
    Response executePendingTasks(@VerifiedUser User loginUser);

}
