package com.ls.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    public void dochattest(){
        String string = aiManager.doChat("","你是哪个模型","deepseek-v3-241226");
        System.out.println("string = " + string);

    }




}