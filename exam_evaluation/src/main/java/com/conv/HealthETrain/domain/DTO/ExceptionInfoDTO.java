package com.conv.HealthETrain.domain.DTO;


import lombok.Data;

@Data
public class ExceptionInfoDTO {
    private String originPicPath;
    private String detectPicPath;
    private String time;
    private String info;
}
