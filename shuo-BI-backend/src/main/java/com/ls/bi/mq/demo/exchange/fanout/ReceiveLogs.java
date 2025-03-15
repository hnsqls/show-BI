package com.ls.bi.mq.demo.exchange.fanout;

import com.rabbitmq.client.*;

/**
 * 消费者
 */
public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        // 声明队列 并且获取到队列的名称
//        String queueName = channel.queueDeclare().getQueue();
        String lsQueue = channel.queueDeclare("ls_queue", true, false, false, null).getQueue();
        String hnsqlsQueue = channel.queueDeclare("hnsqls_queue", true, false, false, null).getQueue();

        // 绑定队列
        channel.queueBind(lsQueue, EXCHANGE_NAME, "");
        channel.queueBind(hnsqlsQueue, EXCHANGE_NAME, "");
//        channel.queueBind(queueName1, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println( " [ls] Received '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [hnsqls_queue] Received '" + message + "'");
        };
        channel.basicConsume(lsQueue, true, deliverCallback, consumerTag -> { });
        channel.basicConsume(hnsqlsQueue, true, deliverCallback2, consumerTag -> { });


    }
}