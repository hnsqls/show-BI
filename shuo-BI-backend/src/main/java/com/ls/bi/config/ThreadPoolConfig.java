package com.ls.bi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {



    // 定义线程池 Bean
    @Bean
    public ExecutorService myexecutorService() {


        // 核心线程
        int corePoolSize = 2;

        //  最大线程数
        int maximumPoolSize = 5;

        // 空闲线程存活时间
        long keepAliveTime = 60;
        // 工作队列
        BlockingQueue<Runnable> workQueue =  new LinkedBlockingQueue<Runnable>(6);


        //创建
        return new ThreadPoolExecutor(
                corePoolSize,               // 核心线程数
                maximumPoolSize,                // 最大线程数
                keepAliveTime,         // 空闲线程存活时间
                TimeUnit.SECONDS,      // 时间单位
                workQueue,           // 工作队列
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );



    }
}
