package com.donaldy.controller;

import com.donaldy.common.ServerResponse;
import com.donaldy.model.ObjectFile;
import com.donaldy.service.FileService;
import com.donaldy.vo.ObjectFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class FileController {

    @Autowired
    private FileService fileService;



    /**
     * 根据文件ID下载
     * @param fileId   文件ID
     * @param response 返回响应
     */
    @GetMapping("/file/download")
    public void download(@RequestParam Integer fileId, HttpServletResponse response) {

        this.fileService.downloadFileById(response, fileId);
    }


    /**
     * 预览 or 下载文件
     * @param fileId       文件ID
     * @param response     response
     */
    @GetMapping("/file/preview")
    public void previewOrDownload(@RequestParam Integer fileId,
                                  HttpServletResponse response) {

        this.fileService.previewFile(fileId, response);
    }

    /**
     * OSS【私有文件】 预览 or 下载文件
     * @param fileId       文件ID
     * @param response     response
     */
    @GetMapping("/download/preview")
    public void previewOrDownload(@RequestAttribute Integer userId,
                                  @RequestAttribute String clientId,
                                  @RequestParam String fileId,
                                  HttpServletResponse response) {

        ObjectFile objectFile = ObjectFile.newBuilder().userId(userId).clientId(clientId).build();

        this.fileService.previewFileByEncryptedId(objectFile, fileId, response);
    }

    /**
     * OSS 下载私有文件
     * @param fileId       文件ID
     * @param response     response
     * @return             返回体
     */
    @GetMapping("/download/direct")
    public ServerResponse directDownload(@RequestAttribute Integer userId,
                                         @RequestAttribute String clientId,
                                         @RequestParam String fileId,
                                         HttpServletResponse response) {

        ObjectFile objectFile = ObjectFile.newBuilder().userId(userId).clientId(clientId).build();

        this.fileService.downloadFileByEncryptedId(objectFile, fileId, response);

        return ServerResponse.createBySuccessMessage("下载成功");
    }

    /**
     * 上传私有桶
     *
     * 全应用可访问
     * @param file        上传的文件
     * @return            文件ID
     */
    @PostMapping("/upload/private")
    public ServerResponse uploadToPrivateBucket(@RequestAttribute Integer userId,
                                                @RequestAttribute String clientId,
                                                @RequestParam MultipartFile file) {

        if (file.isEmpty()) {
            return ServerResponse.createByErrorMessage("文件为空");
        }

        String encryptedId = this.fileService.uploadFile(userId, clientId, file);

        return ServerResponse.createBySuccess(encryptedId);
    }

    /**
     * 上传公有桶
     * @param file        上传的文件
     * @return            文件ID
     */
    @PostMapping("/upload/public")
    public ServerResponse uploadToPublicBucket(@RequestAttribute Integer userId,
                                               @RequestAttribute String clientId,
                                               @RequestParam MultipartFile file) {

        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            return ServerResponse.createByErrorMessage("文件为空");
        }

        ObjectFileVo objectFileVo = this.fileService.uploadPublicFile(userId, clientId, file);

        return ServerResponse.createBySuccess(objectFileVo);
    }
}
