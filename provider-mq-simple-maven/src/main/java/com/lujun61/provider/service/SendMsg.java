package com.lujun61.provider.service;

import com.lujun61.provider.Utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 消息提供者向消息队列发送消息
public class SendMsg {

    public static void main(String[] args) throws Exception {
        sendSimpleMsg();
    }

    private static void sendSimpleMsg() throws Exception {
        String msg = "Hello LuJun!";
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        /*       在普通Maven工程中使用Java代码定义队列       */

        /**               定义队列(使⽤Java代码在MQ中新建⼀个队列)
         * 参数1：定义的队列名称
         * 参数2：队列中的数据是否持久化（如果选择了持久化）
         * 参数3: 是否排外（当前队列是否为当前连接私有）
         * 参数4：⾃动删除（当此队列的连接数为0时，此队列会销毁（⽆论队列中是否还有数据））
         * 参数5：设置当前队列的参数
         */
        //  channel.queueDeclare("queue7", false, false, false, null);


        /*       定义⼀个“订阅交换机”      */
        //   channel.exchangeDeclare("ex3", BuiltinExchangeType.FANOUT);
        /*       定义⼀个“路由交换机”      */
        //   channel.exchangeDeclare("ex4", BuiltinExchangeType.DIRECT);


        /**                    绑定队列
         * 参数1：队列名称
         * 参数2：⽬标交换机
         * 参数3：如果绑定订阅交换机参数为"",如果绑定路由交换机则表示设置队列的key
         */
        //   channel.queueBind("queue7","ex4","key1");
        //   channel.queueBind("queue8","ex4","key2");

        /**
         * ·参数1：交换机名称，如果直接发送信息到队列，则交换机名称为""
         * ·参数2：⽬标队列名称。若交换机名称不为空，则此处作为交换机转发消息的key
         * ·参数3：设置当前这条消息的属性（设置过期时间 10）
         * ·参数4：消息的内容
         */
        /* 直接从队列queue1中获取消息 */
        channel.basicPublish("", "queue2", null, msg.getBytes());

        /* 使用【路由模式】连接交换机（所以第二个参数需要指定key值） */
        //channel.basicPublish("ex1", "a", null, msg.getBytes());

        /* 使用【订阅模式】连接交换机（所以第二个参数没有key值） */
        //channel.basicPublish("ex2", "", null, msg.getBytes());

        System.out.println("发送：" + msg);

        /* 关闭连接：表示可以将消息从客户端提交至MQ服务器 */
        channel.close();
        connection.close();
    }

    /**
     * 在Maven项目中使用RabbitMQ·客户端·事务
     */
    private static void sendTXMsg(String msg) throws IOException, TimeoutException {

        Connection connection = ConnectionUtil.getConnection(); //connection 表示与 host1的连接
        Channel channel = connection.createChannel();
        channel.txSelect(); //开启事务
        try {
            channel.basicPublish("ex4", "k1", null, msg.getBytes());
            channel.txCommit(); //提交事务
        } catch (Exception e) {
            channel.txRollback(); //事务回滚
        } finally {
            channel.close();
            connection.close();
        }

    }

    /**
     * 在普通Maven项目中使用消息确认机制
     * <p>
     * 声明：
     * 以下列举出的三种消息确认方式全为伪代码示例。不代表最终可运行结果！！！
     * <p>
     * 凡消息确认，都应该在发送消息之前开启。避免消息发送过快，未达到监听效果
     */
    private static void sendConfirmMsg(String msg) throws IOException, TimeoutException, InterruptedException {

        Connection connection = ConnectionUtil.getConnection(); //connection 表示与 host1的连接
        Channel channel = connection.createChannel();

        /*                            普通confirm⽅式                            */
        //1.发送消息之前开启消息确认
        channel.confirmSelect();
        channel.basicPublish("ex1", "a", null, msg.getBytes());

        //2.接收消息确认
        boolean b = channel.waitForConfirms();
        System.out.println("发送：" + (b ? "成功" : "失败"));

        // 3、关闭连接
        channel.close();
        connection.close();


        /*                        批量confirm⽅式                            */
        //1.发送消息之前开启消息确认
        channel.confirmSelect();

        //2.批量发送消息
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("ex1", "a", null, msg.getBytes());
        }

        //3.接收批量消息确认：发送的所有消息中，如果有⼀条是失败的，则所有消息发送直接失败，抛出IO异常
        boolean b2 = channel.waitForConfirms();

        // 4、关闭连接
        channel.close();
        connection.close();



        /*                         异步confirm⽅式                        */
        //发送消息之前开启消息确认
        channel.confirmSelect();
        //批量发送消息
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("ex1", "a", null, msg.getBytes());
        }
        //假如发送消息需要10s，waitForConfirms会进⼊阻塞状态
        //boolean b3 = channel.waitForConfirms();

        //使⽤监听器使用异步消息confirm
        channel.addConfirmListener(new ConfirmListener() {
            //参数1： long l 返回消息的表示
            //参数2： boolean b 是否为批量confirm
            public void handleAck(long l, boolean b) throws IOException {
                System.out.println("~~~~~消息成功发送到交换机");


                // 关闭连接：由于是异步监听，需要在方法内关闭channel连接。避免在方法之外关闭，导致此处消息监听失效。
                //channel.close();
                //connection.close();
            }

            public void handleNack(long l, boolean b) throws IOException {
                System.out.println("~~~~~消息发送到交换机失败");
            }
        });
    }

    /*                   return监听                       */
    private static void sendReturnMsg(String msg) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection(); //connection 表示与 host1的连接
        Channel channel = connection.createChannel();

        /* return机制：监听交换机是否将消息分发到队列：成功不返回值；失败才会通知。*/
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int i, String s, String s1, String s2,
                                     AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                //如果交换机分发消息到队列失败，则会执⾏此⽅法（⽤来处理交换机分发消息到队列失败的情况）
                System.out.println("*****" + i); //标识
                System.out.println("*****" + s);
                System.out.println("*****" + s1); //交换机名
                System.out.println("*****" + s2); //交换机对应的队列的key
                System.out.println("*****" + new String(bytes)); //发送的消息
            }
        });

        //发送消息
        //channel.basicPublish("ex2", "c", null, msg.getBytes());
        channel.basicPublish("ex2", "c", true, null, msg.getBytes());


        // 4、关闭连接
        channel.close();
        connection.close();
    }
}