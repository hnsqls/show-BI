package com.ls.bi.mq.demo.exchange.topic;

import com.rabbitmq.client.*;

/**
 * 消费者
 */
public class ReceiveLogsTopic {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        // 声明队列
        channel.queueDeclare("前端组",true,false,false,null);
        channel.queueDeclare("后端组",true,false,false,null);
        channel.queueDeclare("运维组",true,false,false,null);


        // 绑定队列 并且指定键
        channel.queueBind("前端组",EXCHANGE_NAME,"#.前端.#");
        channel.queueBind("后端组",EXCHANGE_NAME,"#.后端.#");
        channel.queueBind("运维组",EXCHANGE_NAME,"#.运维.#");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [前端组] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [后端组] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
         DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [运维组] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume("前端组", true, deliverCallback1, consumerTag -> { });
        channel.basicConsume("后端组", true, deliverCallback2, consumerTag -> { });
        channel.basicConsume("运维组", true, deliverCallback3, consumerTag -> { });
    }
}