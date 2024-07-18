package com.conv.HealthETrain.cotroller;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.global.avformat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;


@RestController
@AllArgsConstructor
@RequestMapping("/file")
@Slf4j
public class FileController {
    private static final String fileBaseAddress = "/home/john/Desktop/file";
    private static final String fileMD5BaseAddress = "/home/john/Desktop/md5";

    @GetMapping("/{md5}")
    public ApiResponse<List<String>> checkFileByMd5(@PathVariable("md5") String md5) {
        // 根据md5查询文件分块
        if(StrUtil.isBlank(md5)) {
            return ApiResponse.error(ResponseCode.NOT_FOUND, "未查询到md5", new ArrayList<>());
        }
        String fileFolder = fileMD5BaseAddress + "/" + md5;
        if(!FileUtil.isDirectory(fileFolder)) {
            return ApiResponse.error(ResponseCode.NOT_FOUND, "未查询到md5", new ArrayList<>());
        }
        List<String> fileNames = FileUtil.listFileNames(fileFolder);
        log.info("读取到文件名列表: {}", fileNames);
        return ApiResponse.success(fileNames);
    }

    @PostMapping("/chunk")
    public ApiResponse<Integer> uploadFile(@RequestParam("md5") String md5,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("chunkNumber") Integer chunkNumber) throws IOException {
        // 将文件保存
        String fileSaveAddress = fileMD5BaseAddress + "/" + md5;
        if(!FileUtil.exist(fileSaveAddress) || !FileUtil.isDirectory(fileSaveAddress)) {
            FileUtil.mkdir(fileSaveAddress);
        }
        if(FileUtil.isDirectory(fileSaveAddress)) {
            // 保存文件
            String savePath = fileSaveAddress + "/" + chunkNumber;
            File saveFile = FileUtil.writeBytes(file.getBytes(), savePath);
            if(saveFile != null) {
                // 写入成功
                return  ApiResponse.success(chunkNumber);
            }
        }
        return  ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY, "文件片段存储失败", chunkNumber);

    }

    @PostMapping("/{md5}")
    public ApiResponse<String> uploadFileSucceed(@PathVariable("md5") String md5,
                                                 @RequestBody JSONObject jsonObject) throws IOException, InterruptedException {
        if(!FileUtil.exist(fileBaseAddress)) {
            FileUtil.mkdir(fileBaseAddress);
        }
        // 将文件保存在fileName下
        String fileName = jsonObject.getStr("fileName");
        String savePath = fileBaseAddress + "/" + fileName;
        String md5Path = fileMD5BaseAddress + "/" + md5;

        if(FileUtil.exist(savePath) && FileUtil.isFile(savePath)) {
            // 验证md5是否相同, 如果不相同则覆盖
            byte[] bytes = FileUtil.readBytes(savePath);
            String exitFileMD5 = DigestUtils.md5Hex(bytes);
            if(StrUtil.equals(exitFileMD5, md5)) {
                // 直接返回给前端路径
                return ApiResponse.success(savePath);
            } else {
                // 删除此文件
                FileUtil.del(savePath);
            }
        }

        FileUtil.newFile(savePath);
        if(FileUtil.exist(md5Path) && FileUtil.isDirectory(md5Path)) {
            // 读取所有md5片段, 写入到savePath组成最后的文件
            mergeFiles(md5Path, savePath);
            return ApiResponse.success(savePath);
        }
        return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY);
    }

    public static void mergeFiles(String md5FolderPath, String savePath) throws IOException {
        // 获取md5文件夹下的所有文件列表
        File[] chunkFiles = FileUtil.ls(md5FolderPath);

        // 按文件名排序，确保顺序合并
        Arrays.sort(chunkFiles, Comparator.comparing(File::getName));

        try (FileOutputStream outputStream = new FileOutputStream(savePath)) {
            for (File chunkFile : chunkFiles) {
                // 读取分块文件的内容并写入最终文件
                FileUtil.writeToStream(chunkFile, outputStream);
                // 删除已经合并的分块文件
//                FileUtil.del(chunkFile);
            }
        }
    }

    /**
     * 获取视频时长
     * @param videoPath
     * @return
     */
    @GetMapping("/video/duration")
    public ApiResponse<Object> getVideoDuration(@RequestParam("videoPath") String videoPath) {
        // 获取视频时长
        long duration = getVideoDurationUtil(videoPath);

        return ApiResponse.success(duration);
    }

    private long getVideoDurationUtil(String videoPath) {
        AVFormatContext pFormatContext = avformat.avformat_alloc_context();

        if (avformat.avformat_open_input(pFormatContext, videoPath, null, null) != 0) {
            throw new RuntimeException("Couldn't open video file: " + videoPath);
        }

        if (avformat.avformat_find_stream_info(pFormatContext, (AVDictionary) null) < 0) {
            throw new RuntimeException("Couldn't retrieve stream info from video file: " + videoPath);
        }

        long duration = pFormatContext.duration() / (1000 * 1000); // 转换为秒

        avformat.avformat_close_input(pFormatContext);

        return duration;
    }

}
