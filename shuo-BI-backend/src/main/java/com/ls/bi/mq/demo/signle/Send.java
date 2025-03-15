package com.ls.bi.mq.demo.signle;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Send {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 连接mq的工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //如果有以下
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setPort(5672);

        try (Connection connection = factory.newConnection();
             // 通过连接 获取频道（具体操作Mq 的qpi）
             Channel channel = connection.createChannel()) {
            // 声明队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //构造消息
            String message = "Hello World!";
            // 选择队列发送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}