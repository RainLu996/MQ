package com.lujun61.consumer.Utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtil {

    public static Connection getConnection() throws IOException, TimeoutException {
        //1.创建连接⼯⼚
        ConnectionFactory factory = new ConnectionFactory();

        //2.在⼯⼚对象中设置MQ的连接信息(ip, port, virtualhost, username, password)
        factory.setHost("192.168.153.130");
        factory.setPort(5672);
        factory.setVirtualHost("host1");
        factory.setUsername("rainlu");
        factory.setPassword("rainlu996");

        //3.通过⼯⼚对象获取与MQ的链接
        Connection connection = factory.newConnection();

        return connection;
    }

}
