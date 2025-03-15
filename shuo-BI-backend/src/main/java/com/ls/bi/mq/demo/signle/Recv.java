package com.ls.bi.mq.demo.signle;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class Recv {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 连接mq 的信息
        factory.setHost("localhost");
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setPort(5672);

        // 创建连接
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        //  通过通道 声明连接的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 阻塞线程，获取消息， 当获取到消息制动执行回调函数deliverCallback
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}