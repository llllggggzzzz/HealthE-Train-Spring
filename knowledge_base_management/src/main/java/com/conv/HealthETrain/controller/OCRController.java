package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.OCRResultDTO;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.utils.AccessTokenUtil;
import com.conv.HealthETrain.utils.FileUtil;
import com.conv.HealthETrain.utils.HttpUtil;
import com.conv.HealthETrain.utils.Base64Util;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/ocr")
@Slf4j
public class OCRController {
    /**
    * @Description: 获取ocr结果
    * @Param: 文件路径
    * @return: 获取文字数组结果
    * @Author: flora
    * @Date: 2024/7/12
    */
    @PostMapping("")
    public ApiResponse<OCRResultDTO> getOCRResult(@RequestBody byte[] fileArrayBuffer){
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            // 本地文件路径
//            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(fileArrayBuffer);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AccessTokenUtil.getAccessToken();

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            log.info("result"+ result);
            // 解析 JSON 响应
            ObjectMapper objectMapper = new ObjectMapper();
            OCRResultDTO ocrResultDTO = objectMapper.readValue(result, OCRResultDTO.class);

            return ApiResponse.success(ocrResultDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
