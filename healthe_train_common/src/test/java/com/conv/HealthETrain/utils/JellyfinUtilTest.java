package com.conv.HealthETrain.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.conv.HealthETrain.factory.JellyfinFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JellyfinUtilTest {

    @Test
    public void test() {
//        String streamingVideoUrl = JellyfinUtil.findStreamingVideoUrl("720p", "video", "jellyfin");
//        Console.log(streamingVideoUrl);

        byte[] bytes = FileUtil.readBytes("/home/john/Desktop/test.mp4");
        Boolean mediaLibrary =  JellyfinFactory
                .build("/home/john/env/后台/jellyfin/")
                        .saveFile(bytes,
                                "test.mp4",
                                "test",
                                true);
//                .createMediaLibrary("video/test", "test", "homevideos");
//        Boolean mediaLibrary = JellyfinUtil.createMediaLibrary("/video/test", "test", "homevideos");
        Console.log(mediaLibrary);
    }

}