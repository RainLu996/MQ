package com.lujun61.provider.controller;

import com.lujun61.provider.service.ProviderMsg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ProviderController {

    @Resource
    private ProviderMsg providerMsg;

    // http://localhost:9002/provider/send?msg=HelloWorld
    @GetMapping("/provider/send")
    public String send(String msg) {

        providerMsg.sendMsg(msg);

        return "success";

    }

}
