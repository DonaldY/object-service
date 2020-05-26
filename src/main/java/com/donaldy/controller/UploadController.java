package com.donaldy.controller;

import com.donaldy.common.OSSConst;
import com.donaldy.common.ServerResponse;
import com.donaldy.service.ObjectFileService;
import com.donaldy.utils.AliyunOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/upload")
public class UploadController {

    @Autowired
    private ObjectFileService objectFileService;

    /**
     * 生产环境 ： 上传私有桶
     * @param file        上传的文件
     * @param clientId    项目
     * @param userId      用户ID
     * @return            文件ID
     * @throws IOException IO
     */
    @PostMapping(value = "/private")
    public ServerResponse uploadPrivateBucket(@RequestAttribute Integer userId,
                                              @RequestAttribute String clientId,
                                              @RequestParam MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ServerResponse.createByErrorMessage("文件为空");
        }

        String fileName = AliyunOSSUtils.upload(OSSConst.OSSClientObject.PROD_PRIVATE, file, clientId);

        Integer fileId = this.objectFileService.addPrivateFile(userId, clientId,
                file.getOriginalFilename(), fileName);

        return ServerResponse.createBySuccess(fileId);
    }

    /**
     * 生产环境 ：上传公有桶
     * @param file        上传的文件
     * @param clientId    项目
     * @return            文件ID
     * @throws IOException IO
     */
    @PostMapping(value = "/public")
    public ServerResponse uploadPublicBucket(@RequestAttribute Integer userId,
                                             @RequestAttribute String clientId,
                                             @RequestParam MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ServerResponse.createByErrorMessage("文件为空");
        }

        String path = AliyunOSSUtils.uploadPublicReturnPath(OSSConst.OSSClientObject.PROD_PRIVATE, file, clientId);

        this.objectFileService.addPublicFile(userId, clientId, file.getOriginalFilename(), path);

        return ServerResponse.createBySuccess(AliyunOSSUtils.getCompleteUrl(OSSConst.OSSClientObject.PROD_PRIVATE.getBucketName(),
                path));
    }
}
