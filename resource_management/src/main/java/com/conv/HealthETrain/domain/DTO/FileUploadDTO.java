package com.conv.HealthETrain.domain.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@EqualsAndHashCode
public class FileUploadDTO {
    private String md5;
    private MultipartFile file;
    private Integer chunkNumber;
}
