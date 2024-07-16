package com.conv.HealthETrain.utils;

import cn.hutool.json.JSONObject;
import com.conv.HealthETrain.response.ApiResponse;
import okhttp3.*;

import java.io.*;
import java.net.URLEncoder;

public class AccessTokenUtil {
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    public static String getAccessToken(){
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=iz9lfy04v1OpwvzaquUSGjEW&client_secret=o6J4LhLXMPxy5p7Qj7hRwvpQ16D3pa4c&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            ResponseBody jsonBody = response.body();
            String stringBody = jsonBody.string();
            JSONObject jsonObject = new JSONObject(stringBody);
            String accessToken = jsonObject.getStr("access_token");
            return accessToken;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes("/Users/flora/Pictures/wqy.jpg");
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AccessTokenUtil.getAccessToken();

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
