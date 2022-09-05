package com.lujun61.consumer.service;

import com.lujun61.consumer.Utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveMsg2 {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        /**
         * 定义消费消息的方式
         */
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                //body就是从队列中获取的数据
                String msg = new String(body);
                System.out.println("Consumer2接收：" + msg);
            }
        };

        /**
         * 参数1：获取消息的目标队列
         * 参数2：给出应答
         * 参数3：消费模式
         */
        channel.basicConsume("queue1", true, consumer);
    }

}
