package com.ls.bi.config;

import com.ls.bi.common.ErrorCode;
import com.ls.bi.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ThreadPoolConfigTest {

    @Resource
    private ExecutorService myexecutorService;

    @Test
    void executorService() throws InterruptedException {

        float cnt = 0.5f;
        while (true){

            System.out.println(cnt +"秒");
            myexecutorService.submit((Callable<Object>) () -> {

                Thread thread = Thread.currentThread();
                System.out.println(thread.getName());
                System.out.println(myexecutorService);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请求压力过大，请稍后再试");
                }

                return thread.getName() +"ls";
            });

            Thread.sleep(500);
            cnt=cnt+0.5f;
        }

    }
}