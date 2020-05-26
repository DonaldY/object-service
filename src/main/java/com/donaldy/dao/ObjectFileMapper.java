package com.donaldy.dao;

import com.donaldy.model.ObjectFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ObjectFileMapper {
    int deleteByPrimaryKey(Integer fileId);

    int insert(ObjectFile record);

    int insertSelective(ObjectFile record);

    ObjectFile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(ObjectFile record);

    int updateByPrimaryKey(ObjectFile record);

    Integer insertFile(@Param("userId") Integer userId,
                              @Param("clientId") String clientId,
                              @Param("name") String name,
                              @Param("path") String path,
                              @Param("type") Integer type);

    ObjectFile selectPrivateFile(@Param("fileId") Integer fileId,
                                 @Param("userId") Integer userId,
                                 @Param("clientId") String clientId,
                                 @Param("type") int type);

    /**
     * 查询根目录
     * @param foldName 文件夹名
     * @param type     文件类别
     * @return         根目录信息
     */
    /*FoldDto selectRootFoldByName(@Param("foldName") String foldName,
                                 @Param("type") Integer type,
                                 @Param("namespaceId") Integer namespaceId);*/


    /**
     * 根据命名空间ID查找所有目录
     * @param namespaceId  命名空间ID
     * @param type         目录类别
     * @return             所有目录
     */
    /*List<FoldDto> selectAllFoldsByNamespaceId(@Param("namespaceId") Integer namespaceId, @Param("type") int type);*/
}