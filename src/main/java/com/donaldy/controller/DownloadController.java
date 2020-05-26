package com.donaldy.controller;


import com.donaldy.common.OSSConst;
import com.donaldy.common.ServerResponse;
import com.donaldy.service.ObjectFileService;
import com.donaldy.utils.AliyunOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@RestController
@RequestMapping(value = "/download")
public class DownloadController {

    @Autowired
    private ObjectFileService objectFileService;

    /**
     * 预览 or 下载文件
     * @param userId       用户ID
     * @param clientId     项目ID
     * @param fileId       文件ID
     * @param response     response
     * @return             返回体
     * @throws IOException 异常
     */
    @GetMapping(value = "/preview")
    public ServerResponse previewOrDownload(@RequestAttribute Integer userId,
                                            @RequestAttribute String clientId,
                                            @RequestParam Integer fileId,
                                            HttpServletResponse response) throws IOException {

        String fileName = this.objectFileService.getPrivateFile(userId, clientId, fileId);

        if (StringUtils.isEmpty(fileName)) {
            return ServerResponse.createByErrorMessage("找不到这个文件");
        }

        AliyunOSSUtils.previewOrDownload(OSSConst.OSSClientObject.PROD_PRIVATE, response, fileName);

        return ServerResponse.createBySuccessMessage("下载成功");
    }

    /**
     * 下载私有文件
     * @param userId       用户ID
     * @param clientId     项目ID
     * @param fileId       文件ID
     * @param response     response
     * @return             返回体
     * @throws IOException 异常
     */
    @GetMapping(value = "/direct")
    public ServerResponse directDownload(@RequestAttribute Integer userId,
                                         @RequestAttribute String clientId,
                                         @RequestParam Integer fileId,
                                         HttpServletResponse response) throws IOException {

        String fileName = this.objectFileService.getPrivateFile(userId, clientId, fileId);

        if (StringUtils.isEmpty(fileName)) {
            return ServerResponse.createByErrorMessage("找不到这个文件");
        }

        //ObjectFile objectFile = ObjectFile.newBuilder().fileName(file.getOriginalFilename()).userId(userId)
        //        .path(path).build();

        AliyunOSSUtils.download(OSSConst.OSSClientObject.PROD_PRIVATE, response, fileName);

        return ServerResponse.createBySuccessMessage("下载成功");
    }
}
