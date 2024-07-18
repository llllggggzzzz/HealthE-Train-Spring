package com.conv.HealthETrain.utils;


import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AIAnswerUtil {
    private static final String aiAddress = "127.0.0.1";
    private static final Integer aiPort = 5000;

    public static Double getScore(String question,
                                   String realAnswer,
                                   String userAnswer,
                                   String sumScore) {
        // 获取分数
        String url = UrlBuilder.create()
                .setScheme("http")
                .setHost(aiAddress).setPort(aiPort)
                .addPath("/answer")
                .addQuery("question", question)
                .addQuery("real_answer", realAnswer)
                .addQuery("user_answer", userAnswer)
                .addQuery("sum_score", sumScore)
                .build();
        HttpResponse execute = HttpUtil.createGet(url).execute();
        JSONObject object = JSONUtil.parseObj(execute.body());
        return object.getDouble("data");
    }
}
