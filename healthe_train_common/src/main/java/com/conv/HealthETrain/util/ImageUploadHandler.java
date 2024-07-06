package com.conv.HealthETrain.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ImageUploadHandler {
    public static String uploadToGitHub(String token, String repo, String pathPrefix, String filePath, String commitMessage) {// 获取当前时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        // 生成文件路径
        String path = StrUtil.format("{}/{}.png", pathPrefix, timestamp);

        // GitHub API URL
        String url = StrUtil.format("https://api.github.com/repos/{}/contents/{}", repo, path);

        // 读取文件并进行 Base64 编码
        File file = FileUtil.file(filePath);
        byte[] fileContent = FileUtil.readBytes(file);
        String contentB64 = Base64Encoder.encode(fileContent);

        // 准备请求头和数据
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "token " + token);
        headers.put("Accept", "application/vnd.github.v3+json");

        Map<String, Object> data = new HashMap<>();
        data.put("message", commitMessage);
        data.put("content", contentB64);

        // 发送请求
        HttpResponse response = HttpRequest.put(url)
                .addHeaders(headers)
                .body(JSONUtil.toJsonStr(data))
                .execute();

        // 检查响应
        if (response.getStatus() == 201) {
            System.out.println("上传成功！");
            JSONObject jsonResponse = JSONUtil.parseObj(response.body());
            return jsonResponse.getByPath("content.download_url", String.class);
        } else {
            System.out.println("上传失败，状态码：" + response.getStatus());
            System.out.println(response.body());
            return null;
        }
    }

    public static void main(String[] args) {
        // 设置参数
        String token = ConfigUtil.getImgSaveToken();
        String repo = ConfigUtil.getImgSaveRepo();
        String pathPrefix = ConfigUtil.getImgSaveFolder();
        String filePath = "/home/john/Desktop/Snipaste_2024-07-06_09-29-03.png";
        String commitMessage = "上传图片到图床";
        // 上传图片
        String imageUrl = uploadToGitHub(token, repo, pathPrefix, filePath, commitMessage);

        if (imageUrl != null) {
            Console.log("图片链接：{}", imageUrl);
        }
    }
}
