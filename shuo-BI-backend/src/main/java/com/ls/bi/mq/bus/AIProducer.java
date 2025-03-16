package com.ls.bi.mq.bus;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * AI 消息生产者
 */
@Component
@Slf4j
public class AIProducer {

    private static final String EXCHANGE_NAME = MessageConst.EXCHANGE_NAME;

    private Channel channel;

    @PostConstruct
    public void initChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
    }

    public void send(String message) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        log.info("Sent message: {}", message);
    }
}