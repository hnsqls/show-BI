package com.ls.bi.mq.demo.exchange.direct;

import com.rabbitmq.client.*;

/**
 * 消费者
 */
public class ReceiveLogsDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 声明队列
        channel.queueDeclare("li",true,false,false,null);
        channel.queueDeclare("shuo",true,false,false,null);


        // 绑定队列 并且指定键
        channel.queueBind("li",EXCHANGE_NAME,"li");
        channel.queueBind("shuo",EXCHANGE_NAME,"shuo");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback lideliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [li] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback shuoliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [shuo] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume("li", true, lideliverCallback, consumerTag -> {});
        channel.basicConsume("shuo", true, shuoliverCallback, consumerTag -> {});
    }
}