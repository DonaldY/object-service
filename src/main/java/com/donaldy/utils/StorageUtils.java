package com.donaldy.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface StorageUtils {

    String uploadObject(MultipartFile file, String fileDir);

    String uploadPublicObject(MultipartFile file, String fileDir);

    String uploadObject(MultipartFile file, String fileDir, Integer strategy);

    String uploadPublicObject(MultipartFile file, String fileDir, Integer strategy);

    /**
     * 预览或下载私有文件
     *
     * @param response 响应
     * @param fileKey  文件路径
     * @param fileName 文件名
     */
    void previewOrDownload(HttpServletResponse response, String fileKey, String fileName);

    /**
     * 预览或下载公有文件
     *
     * @param response 响应
     * @param fileKey  文件路径
     * @param fileName 文件名
     */
    void previewOrDownloadPublicObject(HttpServletResponse response, String fileKey, String fileName);

    /**
     * 下载私有文件
     * @param response 响应
     * @param fileKey  路径
     * @param fileName 原文件名
     */
    void download(HttpServletResponse response, String fileKey, String fileName);

    /**
     * 下载公有文件
     * @param response 响应
     * @param fileKey  路径
     * @param fileName 原文件名
     */
    void downloadPublicObject(HttpServletResponse response, String fileKey, String fileName);

    /**
     * 获取公有地址
     * @param path 相对路径
     * @return     公有URL
     */
    String getPublicUrlByRelativePath(String path);
}
