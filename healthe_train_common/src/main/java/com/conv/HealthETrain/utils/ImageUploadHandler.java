package com.conv.HealthETrain.utils;

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
    public static String uploadFileToGitHub(String token, String repo, String pathPrefix, String filePath, String commitMessage) {// 获取当前时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        // 生成文件路径
        String path = StrUtil.format("{}/{}.png", pathPrefix, timestamp);

        // GitHub API URL
        String url = StrUtil.format("https://api.github.com/repos/{}/contents/{}", repo, path);

        // 读取文件并进行 Base64 编码
        File file = FileUtil.file(filePath);
        byte[] fileContent = FileUtil.readBytes(file);
        String contentB64 = Base64Encoder.encode(fileContent);
//        System.out.println(contentB64);
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

    public static String uploadBase64ToGitHub(String token, String repo, String pathPrefix, String base64EncoderFile, String commitMessage) {// 获取当前时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        // 生成文件路径
        String path = StrUtil.format("{}/{}.png", pathPrefix, timestamp);

        // GitHub API URL
        String url = StrUtil.format("https://api.github.com/repos/{}/contents/{}", repo, path);

        // 准备请求头和数据
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "token " + token);
        headers.put("Accept", "application/vnd.github.v3+json");

        Map<String, Object> data = new HashMap<>();
        data.put("message", commitMessage);
        data.put("content", base64EncoderFile);

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

    public static void test(String base64File){
        // 设置参数
        String token = ConfigUtil.getImgSaveToken();
        String repo = ConfigUtil.getImgSaveRepo();
        String pathPrefix = ConfigUtil.getImgSaveFolder();
        String commitMessage = "上传图片到图床";
        String imageUrl = uploadBase64ToGitHub(token, repo, pathPrefix, base64File, commitMessage);
        System.out.println(imageUrl);
    }

    public static void main(String[] args) {
//        test("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");
        // 设置参数
        String token = ConfigUtil.getImgSaveToken();
        String repo = ConfigUtil.getImgSaveRepo();
        String pathPrefix = ConfigUtil.getImgSaveFolder();
        String filePath = "/home/john/Desktop/Snipaste_2024-07-06_09-29-03.png";
        String commitMessage = "上传图片到图床";
        // 上传图片
        String imageUrl = uploadFileToGitHub(token, repo, pathPrefix, filePath, commitMessage);

        if (imageUrl != null) {
            Console.log("图片链接：{}", imageUrl);
        }
    }
}
