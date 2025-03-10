package com.ls.bi.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data// 因为springboot 参数的注入是直接setter 方法的，所以要加@Data
public class AiConfig {

    private String apiKey;

    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";

    private ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);

    /**
     * AI 请求服务
     * @return
     */
    @Bean
    public ArkService AIService(){
         Dispatcher dispatcher = new Dispatcher();
        return ArkService
                .builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(apiKey).build();

    }

}
