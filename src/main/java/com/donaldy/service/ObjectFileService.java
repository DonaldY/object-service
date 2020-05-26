package com.donaldy.service;

public interface ObjectFileService {

    /**
     * 添加私有文件记录
     * @param userId     用户ID
     * @param clientId   项目ID
     * @param name       原始文件名
     * @param path       上传的路径
     * @return           主键ID
     */
    Integer addPrivateFile(Integer userId, String clientId, String name, String path);

    /**
     * 异步添加公共文件
     * @param userId     用户ID
     * @param clientId   客户端ID
     */
    void addPublicFile(Integer userId, String clientId, String name, String path);

    /**
     * 获取私有文件名
     * @param userId   用户ID
     * @param clientId 项目ID
     * @param fileId   文件ID
     * @return         文件名
     */
    String getPrivateFile(Integer userId, String clientId, Integer fileId);

}
