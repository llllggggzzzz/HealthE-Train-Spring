package com.conv.HealthETrain.utils;

import cn.hutool.core.lang.Console;
import org.junit.jupiter.api.Test;

class AIAnswerUtilTest {

    @Test
    public void test(){
        Integer score = AIAnswerUtil.getScore("你好吗", "我很好", "我好", "5");
        Console.log(score);
    }
}