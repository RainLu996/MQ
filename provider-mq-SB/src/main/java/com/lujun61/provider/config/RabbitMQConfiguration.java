package com.lujun61.provider.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    //声明队列
    @Bean
    public Queue queue9(){
        Queue queue9 = new Queue("queue9");
        //设置队列属性
        return queue9;
    }
    @Bean
    public Queue queue10(){
        Queue queue10 = new Queue("queue10");
        //设置队列属性
        return queue10;
    }
    //声明订阅模式交换机
    @Bean
    public FanoutExchange ex5(){
        return new FanoutExchange("ex5");
    }
    //声明路由模式交换机
    @Bean
    public DirectExchange ex6(){
        return new DirectExchange("ex6");
    }
    //绑定队列
    @Bean
    public Binding bindingQueue9(Queue queue9, DirectExchange ex6){
        return BindingBuilder.bind(queue9).to(ex6).with("k1");
    }
    @Bean
    public Binding bindingQueue10(Queue queue10, DirectExchange ex6){
        return BindingBuilder.bind(queue10).to(ex6).with("k2");
    }

}
