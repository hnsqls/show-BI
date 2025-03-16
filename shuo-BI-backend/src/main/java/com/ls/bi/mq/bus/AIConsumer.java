package com.ls.bi.mq.bus;

import com.ls.bi.common.ErrorCode;
import com.ls.bi.exception.BusinessException;
import com.ls.bi.manager.AiManager;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.service.ChartService;
import com.ls.bi.utils.ExcelUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * AI 服务消费者
 */
@Component
@Slf4j
public class AIConsumer {

    private static final String EXCHANGE_NAME = MessageConst.EXCHANGE_NAME;
    private static final String QUEUE_NAME = MessageConst.Queue_NAME;

    @Resource
    private AiManager aiManager;

    @Resource
    private ChartService chartService;

    private Channel channel;

    @PostConstruct
    public void initChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        // 声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 绑定队列
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        log.info("Queue '{}' declared and bound to exchange '{}'", QUEUE_NAME, EXCHANGE_NAME);

        // 启动消费者
        startConsumer();
    }

    private void startConsumer() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String msg = new String(delivery.getBody(), "UTF-8");
            log.info(" [{}] Received message: {}", QUEUE_NAME, msg);

            Chart chart = chartService.getById(msg);

            String systemPrompt = "你是数据分析师，我会给你{csv数据}，以及{分析的要求}，请你生成{图形类型}ECharts的配置对象的json代码\n" +
                    "例如生成的图表数据\n" + "{\n" +
                    "```\n" +
                    "  \"xAxis\": {\n" +
                    "    \"type\": \"category\",\n" +
                    "    \"data\": [\"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\", \"Sun\"]\n" +
                    "  },\n" +
                    "  \"yAxis\": {\n" +
                    "    \"type\": \"value\"\n" +
                    "  },\n" +
                    "  \"series\": [\n" +
                    "    {\n" +
                    "      \"data\": [150, 230, 224, 218, 135, 147, 260],\n" +
                    "      \"type\": \"line\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "```\n" +
                    "1. 生成符合Echarts 能直接使用的图片代码\n" +
                    "2. 生成分析结论\n" +
                    "3. 在json串前以及都添加```\n" +
                    "4. 不要回复给我其他任何无关的信息包括你的结束语。";
            // 用户prompt
            String userPrompt = "根据csv数据:%s，分析要求:%s,生成一个%s类型的图表,";

            String csv = chart.getChartData();
            String chartType = chart.getChartType();
            String goal = chart.getGoal();

            String userPromptFormat = String.format(userPrompt, csv, chartType, goal);

            String result = null;
            // 处理消息
            try{
                 result = aiManager.doChat(systemPrompt, userPromptFormat);
            }catch (Exception e){

                // 返回信息，让其重新入队
                // 如果处理失败，拒绝消息并重新入队
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                throw  new BusinessException(ErrorCode.OPERATION_ERROR,"AI 服务繁忙，请稍后再试");
            }


            if (result == null){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"AI 服务繁忙，请稍后再试");
            }
            // 拆分代码， 和结果
            String[] split = result.split("```");
            // js 代码
            String genChart = split[1].trim();
            // 分析结果
            String genResult = split[2].trim();

            // 保存到数据库
            chart.setGenChart(genChart);
            chart.setGenResult(genResult);
            chartService.saveOrUpdate(chart);
            // 确认消息处理成功
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            log.info(" [{}] Processed and saved chart: {}", QUEUE_NAME, chart.getChartName());
        };

        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
    }
}
