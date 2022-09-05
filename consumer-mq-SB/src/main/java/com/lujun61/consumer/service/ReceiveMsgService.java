package com.lujun61.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lujun61.consumer.entity.Student;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = {"queue1", "queue2"})
public class ReceiveMsgService {

    @RabbitHandler
    public void receiveMsg(String msg) {
        System.out.println("接收MSG：" + msg);
    }

    @RabbitHandler
    public void receiveObjMsg(String msg) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Student student = objectMapper.readValue(msg, Student.class);

        System.out.println("String---" + student);
    }

}
