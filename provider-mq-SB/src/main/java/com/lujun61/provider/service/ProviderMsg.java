package com.lujun61.provider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lujun61.provider.entity.Student;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProviderMsg {

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 发送普通字符串消息
     * @param msg 普通字符串消息
     */
    public void sendMsg(String msg) {

        //1. 发送消息到队列
        amqpTemplate.convertAndSend("queue1", msg);
        //2. 发送消息到交换机(订阅交换机)
        amqpTemplate.convertAndSend("ex1", "", msg);
        //3. 发送消息到交换机(路由交换机)
        amqpTemplate.convertAndSend("ex2", "a", msg);

    }

    public void sendObjMsg(Student student) throws JsonProcessingException {

        // 发送字符串消息：常用->先转为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStu = objectMapper.writeValueAsString(student);
        amqpTemplate.convertAndSend("", "queue1", jsonStu);

    }


}
