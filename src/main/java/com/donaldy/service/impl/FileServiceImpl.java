package com.donaldy.service.impl;

import com.donaldy.common.Const;
import com.donaldy.dao.ObjectFileMapper;
import com.donaldy.model.ObjectFile;
import com.donaldy.service.FileService;
import com.donaldy.utils.*;
import com.donaldy.vo.ObjectFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private StorageUtils storageUtils;

    @Autowired
    private ObjectFileMapper objectFileDao;

    @Override
    public void downloadFileById(HttpServletResponse response, Integer fileId) {

    }

    @Override
    public void downloadFileByEncryptedId(ObjectFile objectFile, String fileId, HttpServletResponse response) {

        Assert.isFalse(StringUtils.isEmpty(fileId), Const.HttpStatusCode.BAD_REQUEST.getCode(), "文件参数错误");

        ObjectFile file = null;

        Assert.isFalse(ObjectUtils.isEmpty(file), Const.HttpStatusCode.NOT_FOUND.getCode(), "貌似找不到这个路径");

        this.storageUtils.download(response, file.getPath(), file.getFileName());
    }

    @Override
    public void previewFile(Integer fileId, HttpServletResponse response) {

        Assert.isFalse(NumberUtils.isEmpty(fileId), Const.HttpStatusCode.BAD_REQUEST.getCode(), "文件参数错误");

        ObjectFile file = null;

        Assert.isFalse(ObjectUtils.isEmpty(file), Const.HttpStatusCode.NOT_FOUND.getCode(), "貌似找不到这个路径");

        previewFileByType(response, file);
    }

    /**
     * 根据文件类型上传文件
     *
     * TODO : 文件类型可能改变，但文件存储目录改变，不能按照这个进行存储
     * @param response   响应
     * @param file       文件
     */
    private void previewFileByType(HttpServletResponse response, ObjectFile file) {

        if (Const.FileType.PUBLIC.getCode() == file.getType()) {

            this.storageUtils.previewOrDownloadPublicObject(response, file.getPath(), file.getFileName());

            return;
        }

        this.storageUtils.previewOrDownload(response, file.getPath(), file.getFileName());
    }

    @Override
    public String uploadFile(Integer userId, String clientId, MultipartFile file) {

        String relativePath = this.storageUtils.uploadObject(file, clientId);

        ObjectFile objectFile = ObjectFile.newBuilder().
                userId(userId).clientId(clientId).fileName(file.getOriginalFilename())
                .fileSize(file.getSize()).path(relativePath).build();

        createFile(objectFile);

        return HashidsUtils.encode(objectFile.getFileId());
    }

    private void createFile(ObjectFile objectFile) {

        int flag = this.objectFileDao.insertSelective(objectFile);

        Assert.isFalse(NumberUtils.isEmpty(flag), Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                "数据库操作异常");
    }

    /**
     * 重写文件
     * 1. 校验文件
     * 2. 上传文件
     * 3. 更新目录
     * @param fileId 文件ID
     * @param file   文件
     */
    @Override
    public void overwriteFile(Integer fileId, MultipartFile file) {

        Assert.isFalse(StringUtils.isEmpty(fileId), Const.HttpStatusCode.BAD_REQUEST.getCode(), "文件参数错误");

        ObjectFile objectFile = null;

        Assert.isFalse(ObjectUtils.isEmpty(objectFile), Const.HttpStatusCode.NOT_FOUND.getCode(), "貌似找不到这个文件");

        String path = uploadFileByType(file, objectFile);

        updateFilePath(fileId, path);
    }

    private void updateFilePath(Integer fileId, String path) {

        int flag = 0;

        Assert.isFalse(NumberUtils.isEmpty(flag), Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "更新失败");
    }

    /**
     * 根据文件类型上传文件
     *
     * TODO : 文件类型可能改变，但文件存储目录改变，不能按照这个进行存储
     * @param file       文件
     * @param objectFile 对象文件信息
     * @return           上传后的相对路径
     */
    private String uploadFileByType(MultipartFile file, ObjectFile objectFile) {

        if (Const.FileType.PUBLIC.getCode() == objectFile.getType()) {

            return this.storageUtils.uploadPublicObject(file, objectFile.getClientId());
        }

        return this.storageUtils.uploadObject(file, objectFile.getClientId());
    }

    @Override
    public ObjectFileVo uploadPublicFile(Integer userId, String clientId, MultipartFile file) {

        String path = this.storageUtils.uploadPublicObject(file, clientId);

        ObjectFile objectFile = ObjectFile.newBuilder().
                userId(userId).clientId(clientId).fileName(file.getOriginalFilename())
                .fileSize(file.getSize()).type(Const.FileType.PUBLIC.getCode())
                .path(path).build();

        createFile(objectFile);

        String url = this.storageUtils.getPublicUrlByRelativePath(path);

        return ObjectFileVo.builder().fileId(objectFile.getFileId()).fileName(objectFile.getFileName())
                .url(url).createdAt(new Date()).updatedAt(new Date()).fileSize(file.getSize()).build();
    }

    private ObjectFile getRenameObjectFile(ObjectFile objectFile, String newFileName) {

        objectFile.setFileName(newFileName);
        objectFile.setUpdatedAt(new Date());

        return objectFile;
    }

    @Override
    public void previewFileByEncryptedId(ObjectFile objectFile, String fileId, HttpServletResponse response) {

        Assert.isFalse(StringUtils.isEmpty(fileId), Const.HttpStatusCode.BAD_REQUEST.getCode(), "文件参数错误");

        ObjectFile file = null;

        Assert.isFalse(ObjectUtils.isEmpty(file), Const.HttpStatusCode.NOT_FOUND.getCode(), "貌似找不到这个文件");

        this.storageUtils.previewOrDownload(response, file.getPath(), file.getFileName());
    }
}
