package com.conv.HealthETrain.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;



@Slf4j
public class JellyfinUtil {
    private JellyfinUtil() {}
    private JellyfinUtil(String jellyfinBaseAddress) {
        if(StrUtil.endWith(jellyfinBaseAddress, "/")) {
            jellyfinBaseAddress = jellyfinBaseAddress.substring(0, jellyfinBaseAddress.length()-1);
        }
        this.jellyfinBaseAddress = jellyfinBaseAddress;
    }
    private String jellyfinBaseAddress = "";
    private static final String JellyfinBaseAddress = "127.0.0.1";
    private static final Integer JellyfinPort = 8096;

    /**
     * @description 查询jellyfin寻找串流地址,未找到则返回空
     * @param videoName 视频名称
     * @return 返回串流地址
     */
    public  String findStreamingVideoUrl(String videoName, String mediaLibrary, String userName) {
        if(StrUtil.isBlankOrUndefined(userName)) {
            log.warn("未检测到设置用户名, 使用默认用户");
            userName = "root";
        }
        String jellyfinApiKey = ConfigUtil.getJellyfinApiKey();
        // 获取API_Key
        String address = JellyfinBaseAddress + ":" + JellyfinPort;
        String api_key = "?api_key=" + jellyfinApiKey;
        String refreshUrl = address + "/Library/Refresh" + api_key;
        // 刷新所有媒体库
        HttpResponse execute = HttpRequest.post(refreshUrl).execute();
//        Console.log(execute);
        // 查询userId
        String findUserIdUrl = address + "/Users" + api_key;
        String userIdResponseBody = HttpRequest.get(findUserIdUrl).execute().body();
        JSONArray userJsonArray = JSONUtil.parseArray(userIdResponseBody);
        if(userJsonArray.isEmpty()) {
            log.error("刷新媒体库请求失败");
            return "";
        }
        log.info("刷新媒体库成功");
        // 查询所有媒体库信息,
        String userId = "";
//        Console.log(userJsonArray);
        for (int i = 0; i < userJsonArray.size(); i++) {
            JSONObject jsonObject = userJsonArray.getJSONObject(i);
            String objecUserName = jsonObject.getStr("Name");
            if(StrUtil.equals(objecUserName, userName)) {
                userId = jsonObject.getStr("Id");
                break;
            }
        }
        // 未查询到用户id, 用户不存在
        if(StrUtil.isBlank(userId)) {
            log.error("未查询到用户id, 用户不存在");
            return "";
        }

        log.info("成功查询到用户ID: {}", userId);

        // 访问所有媒体库
        String mediaLibraryId = "";

        String mediaLibraryUrl = address + StrUtil.format("/Users/{}/Views", userId) + api_key;
        String mediaLibraryResponse = HttpRequest.get(mediaLibraryUrl).execute().body();
        JSONObject mediaLibraryJson = JSONUtil.parseObj(mediaLibraryResponse);
        if(!mediaLibraryJson.isEmpty()) {
//            Console.log(mediaLibraryJson);
            JSONArray items = mediaLibraryJson.getJSONArray("Items");
            if(items.isEmpty()) {
                log.error("查询媒体库列表失败");
                return "";
            }
            // 便利所有items
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if(StrUtil.equals(item.getStr("Name"), mediaLibrary)) {
                    mediaLibraryId = item.getStr("Id");
                    break;
                }
            }
        }

        log.info("查询媒体库成功-ID: {}", mediaLibraryId);

        String videoId = "";


        String getMediaLibraryDetailUrl = address +
                StrUtil.format("/Users/{}/Items", userId) +
                api_key +
                "&ParentId=" +
                mediaLibraryId;
        String mediaLibraryDetailBody = HttpRequest.get(getMediaLibraryDetailUrl).execute().body();
        JSONObject mediaLibraryDetailObject = JSONUtil.parseObj(mediaLibraryDetailBody);
        if(!mediaLibraryDetailObject.isEmpty()) {
            JSONArray items = mediaLibraryDetailObject.getJSONArray("Items");
            if(items.isEmpty()) {
                log.error("查询媒体库详细信息失败");
                return "";
            }
            // 遍历items, 寻找对应视频名称
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if(!item.isEmpty() && StrUtil.equals(item.getStr("Name"), videoName) && !item.getBool("IsFolder")) {
                    // 获取对应的id
                    videoId = item.getStr("Id");
                    break;
                }
            }
        }


        if(!videoId.isBlank()) {
            //拼接返回
            log.info("获取视频ID成功: {}", videoId);
            String url = URLUtil.normalize(address +
                    StrUtil.format("/Items/{}/Download", videoId) +
                    api_key);
            log.info("得到串流URL: {}", url);
            return url;
        }
        log.error("获取视频ID失败");
        return "";
    }

    /**
     * @description 传入挂载的相对路径,即可创建媒体库
     * @param path 挂载的文件夹相对位置: /video -> jellyfinBaseAddress + /video
     * @param libraryName 创建的媒体库的名称
     * @param collectionType 创建的媒体库的类型
     * @return 是否创建成功
     */
    public Boolean createMediaLibrary(String path, String libraryName, String collectionType) {
        if(jellyfinBaseAddress.isBlank()) {
            log.error("基础路径初始化异常-PATH: {}", jellyfinBaseAddress);
            throw new GlobalException("基础路径初始化异常", ExceptionCode.JELLYFIN_INIT_ERROR);
        }


        if(!StrUtil.startWith(path, "/")) {
            path = "/" + path;
        }
        if((!path.startsWith("/video") ) && (!path.startsWith("/image"))) {
            log.error("MediaLibrary库创建失败,不支持的挂载路径: {}", path);
            throw new GlobalException("MediaLibrary库创建失败,不支持的挂载路径", ExceptionCode.FILE_PATH_ERROR);
        }

        String realPath = jellyfinBaseAddress + path;
        if(!FileUtil.exist(realPath)) {
            File newDir = FileUtil.mkdir(realPath);
            if(!newDir.isDirectory()) {
                System.out.println(newDir.getAbsolutePath());
                log.error("文件夹创建失败");
                throw new GlobalException("文件夹创建失败", ExceptionCode.JELLYFIN_INIT_ERROR);
            } else {
                log.info("文件夹创建成功-PATH: {}", realPath);
            }
        } else {
            log.warn("文件夹已存在且可能不为空,不再创建文件夹");
        }

        if(!collectionType.isBlank()) {
            collectionType = "homevideos";
        }
        // 获取API_Key
        String jellyfinApiKey = ConfigUtil.getJellyfinApiKey();
        String jsonString = ConfigUtil.getJellyfinCreateJson();
        JSONObject creationJsonObject = JSONUtil.parseObj(jsonString);
        JSONObject libraryOptions = creationJsonObject.getJSONObject("LibraryOptions");
        JSONObject addPath = new JSONObject();
        addPath.set("Path", path);
        JSONArray pathArray = new JSONArray();
        pathArray.add(addPath);
        JSONObject newLibraryOptions = libraryOptions.set("PathInfos", pathArray);
        JSONObject requestBody = new JSONObject();
        requestBody.set("LibraryOptions", newLibraryOptions);
        String postURL = UrlBuilder.create()
                .setScheme("http")
                .setHost(JellyfinBaseAddress)
                .setPort(JellyfinPort)
                .addPath("/Library").addPath("VirtualFolders")
                .addQuery("collectionType", collectionType)
                .addQuery("refreshLibrary", true)
                .addQuery("name", libraryName)
                .build();
        log.info("构建建立请求URL: {}", postURL);

        HttpResponse response = HttpRequest.post(postURL)
                .header("Content-Type", "application/json")
                .header("Authorization", "MediaBrowser Token=" + jellyfinApiKey)
                .body(requestBody.toString()).execute();

        return response.getStatus() < 300;
    }

    /**
     * @description 将文件保存到媒体库中
     * @param sourcePath 源路径
     * @param libraryName 选择的媒体存储库名称
     * @param copyMode 复制模式: 当打开时文件会复制到媒体库中保存,
     *                 当关闭时文件会移动到媒体库中保存
     * @param isOverride 覆盖模式: 当打开时若媒体库中有相同命名文件则会覆盖保存
     *                   当关闭时若媒体库中有相同命名文件则不进行保存
     * @return 返回文件保存是否成功
     */
    public Boolean saveFile(String sourcePath, String libraryName, Boolean copyMode, Boolean isOverride) {
        if(!FileUtil.exist(sourcePath) || !FileUtil.isFile(sourcePath)) {
            log.error("文件不存在或者传入了文件夹路径");
            return false;
        }


        // 根据库名称查询真实地址
        String jellyfinApiKey = ConfigUtil.getJellyfinApiKey();
        String getMediaLibraryUrl = UrlBuilder.create()
                .setScheme("http")
                .setHost(JellyfinBaseAddress)
                .setPort(JellyfinPort)
                .addPath("/Library").addPath("VirtualFolders")
                .addQuery("api_key", jellyfinApiKey)
                .build();
        String body = HttpRequest.get(getMediaLibraryUrl).execute().body();
        JSONArray array = JSONUtil.parseArray(body);
        if(array.isEmpty()) {
            log.error("未创建任何媒体库");
            return false;
        }

        String path = "";
        for (int i = 0; i < array.size(); i++) {
            JSONObject medieLibraryInfo = array.getJSONObject(i);
            if(StrUtil.equals(medieLibraryInfo.getStr("Name"), libraryName)) {
                JSONArray locations = medieLibraryInfo.getJSONArray("Locations");
                if(locations.isEmpty()) {
                    log.error("未查询到媒体库设置地址");
                    return false;
                }
                String findPath = locations.getStr(0);
                if(!StrUtil.isBlank(findPath)) {
                    path = findPath;
                    log.info("查询到媒体库路径: {}", path);
                    break;
                }
            }
        }

        String realDirPath = jellyfinBaseAddress + path;
        if(!FileUtil.exist(realDirPath) || !FileUtil.isDirectory(realDirPath)) {
            log.warn("目标文件夹不存在或为文件,创建新的文件夹");
            FileUtil.mkdir(realDirPath);
        }


        String targetFilePath = realDirPath + "/" + FileUtil.getName(sourcePath);
        if(FileUtil.exist(targetFilePath)) {
            if(isOverride) {
                log.warn("文件已存在,将覆盖文件");
                if(copyMode) {
                    FileUtil.copy(sourcePath, targetFilePath, true);
                    log.info("文件复制成功: {} -> {}", sourcePath, targetFilePath);
                }
                else {
                    FileUtil.move(new File(sourcePath), new File(targetFilePath), true);
                    log.info("文件移动成功: {} -> {}", sourcePath, targetFilePath);
                }
                String refreshURL = UrlBuilder.create()
                        .setScheme("http")
                        .setHost(JellyfinBaseAddress)
                        .setPort(JellyfinPort)
                        .addPath("/Library").addPath("Refresh")
                        .addQuery("api_key", jellyfinApiKey)
                        .build();
                HttpResponse response = HttpRequest.post(refreshURL).execute();
                if(response.getStatus() < 300) {
                    log.info("媒体库状态刷新成功");
                } else {
                    log.error("媒体库状态刷新失败");
                }
                return true;
            } else {
                log.warn("文件已存在,移动失败");
                return false;
            }
        } else {
            if(copyMode) {
                FileUtil.copy(sourcePath, targetFilePath, true);
                log.info("文件复制成功: {} -> {}", sourcePath, targetFilePath);
            }
            else {
                FileUtil.move(new File(sourcePath), new File(targetFilePath), true);
                log.info("文件移动成功: {} -> {}", sourcePath, targetFilePath);
            }
            String refreshURL = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(JellyfinBaseAddress)
                    .setPort(JellyfinPort)
                    .addPath("/Library").addPath("Refresh")
                    .addQuery("api_key", jellyfinApiKey)
                    .build();
            HttpResponse response = HttpRequest.post(refreshURL).execute();
            if(response.getStatus() < 300) {
                log.info("媒体库状态刷新成功");
            } else {
                log.error("媒体库状态刷新失败");
            }
        }
        return false;
    }

    /**
     * @description 将文件字节流保存到媒体中
     * @param sourceBytes 源字节数组
     * @param fileName 文件名称: example.mp4
     * @param libraryName 选择的媒体存储库名称
     * @param isOverride 覆盖模式: 当打开时若媒体库中有相同命名文件则会覆盖保存
     *                   当关闭时若媒体库中有相同命名文件则不进行保存
     * @return 返回文件是否保存成功
     */
    public Boolean saveFile(byte[] sourceBytes, String fileName, String libraryName, Boolean isOverride) {

        String tempFilePath = UniqueIdGenerator.generateUniqueId(DateTime.now().toString(), DateTime.now().toString()) + "/" + fileName;
        if(!FileUtil.exist(tempFilePath)) {
            FileUtil.newFile(tempFilePath);
        }
        File file = FileUtil.writeBytes(sourceBytes, tempFilePath);
        Boolean saveSucceed = false;
        if(file.exists()) {
            String sourcePath = file.getPath();
            log.info("创建临时文件路径: {}", sourcePath);
            saveSucceed = saveFile(sourcePath, libraryName, false, isOverride);
        }
        if(FileUtil.exist(tempFilePath)) {
            FileUtil.del(tempFilePath);
        }
        return saveSucceed;
    }

}
