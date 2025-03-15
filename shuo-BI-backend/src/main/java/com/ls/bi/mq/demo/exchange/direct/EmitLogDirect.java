package com.ls.bi.mq.demo.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

/**
 * 生产者
 */
public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机，名称，类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入消息和路由键");
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

