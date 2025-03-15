package com.ls.bi.mq.demo.Muti;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消费者， 多个消费者
 */
public class Worker {

  private static final String TASK_QUEUE_NAME = "task_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    // 连接是单实例的
    final Connection connection = factory.newConnection();


    // 通过通道，声明队列信息
//    channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");



    for(int i = 0 ; i < 2; i ++){
//        通过通道，声明队列信息
        // 通道是多实例的，但是不通的通过可以操作相同的队列
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        // 设置通道一次处理的消息
        channel.basicQos(1);
        // 回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), "UTF-8");

            try {
                System.out.println(" [x] Received '" + message + "'");
                Thread.sleep(8000); // 模拟耗时
            } catch (InterruptedException e) {
                // 如果处理失败，拒绝消息并重新入队
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                e.printStackTrace();
            } finally {
                System.out.println(" [x] done '" + message + "'");
                // 消息确认
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        // 监听消息
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });

    }


  }

}