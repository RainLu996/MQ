package com.lujun61.provider.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class MyConfirmListener implements RabbitTemplate.ConfirmCallback {
    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    // 声明周期函数，在当前对象被Spring创建后，加载此声明周期函数，给RabbitTemplate对象赋值
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        //参数b 表示消息确认结果
        //参数s 表示发送的消息
        if (b) {
            System.out.println("消息发送到交换机成功！");
        } else {
            System.out.println("消息发送到交换机失败！");
            amqpTemplate.convertAndSend("ex4", "", s);
        }
    }
}
