package com.conv.HealthETrain.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.conv.HealthETrain.factory.JellyfinFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JellyfinUtilTest {

    @Test
    public void test() {
//        String streamingVideoUrl = JellyfinUtil.findStreamingVideoUrl("720p", "video", "jellyfin");
//        Console.log(streamingVideoUrl);

        byte[] bytes = FileUtil.readBytes("/home/john/Desktop/test.mp4");
        List<String> allMediaLibrary = JellyfinFactory
                .build("/home/john/env/server/jellyfin")
                .getAllMediaLibrary("");
//                .createMediaLibrary("video/test", "test", "homevideos");
//        Boolean mediaLibrary = JellyfinUtil.createMediaLibrary("/video/test", "test", "homevideos");
        Console.log(allMediaLibrary);
    }

}