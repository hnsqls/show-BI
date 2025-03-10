package com.ls.bi;


import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AITest {
    // 请确保您已将 API Key 存储在环境变量 ARK_API_KEY 中
// 初始化Ark客户端，从环境变量中读取您的API Key
        // 从环境变量中获取您的 API Key。此为默认方式，您可根据需要进行修改
//        static String apiKey = System.getenv("ARK_API_KEY");
        static String apiKey = "28950e59-8ce0-4216-8771-e1f88693e908";


        // 此为默认路径，您可根据业务所在地域进行配置
        static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
        static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        static Dispatcher dispatcher = new Dispatcher();
        static ArkService service = ArkService.builder().dispatcher(dispatcher).connectionPool(connectionPool).baseUrl(baseUrl).apiKey(apiKey).build();

        public static void main(String[] args) {
            System.out.println("\n----- standard request -----");
            // 构造消息
            final List<ChatMessage> messages = new ArrayList<>();
            final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是人工智能助手.").build();
            final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
            messages.add(systemMessage);
            messages.add(userMessage);


            // 封装请求
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
                    .model("doubao-pro-32k-character-241215")
                    .messages(messages)
                    .build();

            // 发起请求获取结果
            service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));

//            System.out.println("\n----- streaming request -----");
//            final List<ChatMessage> streamMessages = new ArrayList<>();
//            final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是人工智能助手.").build();
//            final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
//            streamMessages.add(streamSystemMessage);
//            streamMessages.add(streamUserMessage);
//
//            ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
//                    // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
//                    .model("doubao-pro-32k-character-241215")
//                    .messages(messages)
//                    .build();
//
//            service.streamChatCompletion(streamChatCompletionRequest)
//                    .doOnError(Throwable::printStackTrace)
//                    .blockingForEach(
//                            choice -> {
//                                if (choice.getChoices().size() > 0) {
//                                    System.out.print(choice.getChoices().get(0).getMessage().getContent());
//                                }
//                            }
//                    );

            service.shutdownExecutor();
        }

}

