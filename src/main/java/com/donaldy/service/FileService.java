package com.donaldy.service;

import com.donaldy.model.ObjectFile;
import com.donaldy.vo.ObjectFileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService {

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     */
    void downloadFileById(HttpServletResponse response, Integer fileId);

    /**
     * 根据加密文件ID下载文件
     * @param response   返回响应
     * @param fileId     加密文件ID
     * @param objectFile 文件信息
     */
    void downloadFileByEncryptedId(ObjectFile objectFile, String fileId, HttpServletResponse response);

    /**
     * 根据加密文件ID预览文件
     * @param response   返回响应
     * @param fileId     文件ID
     */
    void previewFile(Integer fileId, HttpServletResponse response);

    /**
     * 默认上传文件
     * @param userId   用户ID
     * @param clientId 应用ID
     * @param file     文件
     * @return         加密文件ID
     */
    String uploadFile(Integer userId, String clientId, MultipartFile file);

    /**
     * 上传公共文件
     * @param userId   用户ID
     * @param fileDir  存储相对路径（clientId 或者 相对路径）
     * @param file     文件
     * @return         文件信息
     */
    ObjectFileVo uploadPublicFile(Integer userId, String fileDir, MultipartFile file);


    /**
     * 根据加密ID预览文件
     * @param objectFile  对象文件
     * @param fileId      加密ID
     * @param response    返回
     */
    void previewFileByEncryptedId(ObjectFile objectFile, String fileId, HttpServletResponse response);


    /**
     * 重写文件
     * @param fileId 文件ID
     * @param file   文件
     */
    void overwriteFile(Integer fileId, MultipartFile file);
}
