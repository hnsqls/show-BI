package com.ls.bi.mq.demo.exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

/**
 * 生产者
 */
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()) {
                String[] argvs = scanner.nextLine().split(" ");
                if (argvs.length < 1) {
                    continue;
                }
                String message = argvs[0];
                String routerkey = argvs[1];
                // 发送消息给交换机
                channel.basicPublish(EXCHANGE_NAME, routerkey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + routerkey + "':'" + message + "'");
            }




        }
    }


}