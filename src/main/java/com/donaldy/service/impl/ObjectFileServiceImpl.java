package com.donaldy.service.impl;

import com.donaldy.common.Const;
import com.donaldy.common.OSSConst;
import com.donaldy.dao.ObjectFileMapper;
import com.donaldy.model.ObjectFile;
import com.donaldy.service.ObjectFileService;
import com.donaldy.utils.Assert;
import com.donaldy.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ObjectFileServiceImpl implements ObjectFileService {

    @Autowired
    private ObjectFileMapper objectFileMapper;


    @Override
    public Integer addPrivateFile(Integer userId, String clientId, String name, String path) {

        Integer fileId = this.objectFileMapper.insertFile(userId, clientId, name,
                path, OSSConst.FileType.PUBLIC.getCode());

        Assert.isFalse(NumberUtils.isEmpty(fileId), Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "生成记录失败");

        return fileId;
    }

    @Override
    public void addPublicFile(Integer userId, String clientId, String name, String path) {

        Integer flag = this.objectFileMapper.insertFile(userId, clientId, name,
                path, OSSConst.FileType.PUBLIC.getCode());

        // TODO: 错误重试
    }


    @Override
    public String getPrivateFile(Integer userId, String clientId, Integer fileId) {

        ObjectFile objectFile = this.objectFileMapper.selectPrivateFile(fileId, userId, clientId,
                OSSConst.FileType.PRIVATE.getCode());

        Assert.isFalse(ObjectUtils.isEmpty(objectFile), Const.HttpStatusCode.FORBIDDEN.getCode(), "找不到该文件");

        return objectFile.getPath();
    }

    /**
     * 处理目录并返回目录
     *
     * 根目录 /
     *
     * 1. 生成树
     * 2. 插入与更新
     *
     * @param fileDto 上传信息
     */
    /*private FoldDto executeFoldReturnFoldId(FileDto fileDto) {

        List<String> dirs = Arrays.asList(fileDto.getDir().split("/"));

        dirs = dirs.stream().filter(dir -> !StringUtils.isEmpty(dir)).collect(Collectors.toList());

        List<FoldDto> foldTreeList = this.objectFileDao.selectAllFoldsByNamespaceId(fileDto.getNamespaceId(),
                Const.FileType.FOLD.getCode());

        Map<Integer, List<FoldDto>> foldTreeMap = foldTreeList.stream()
                .collect(Collectors.groupingBy(FoldDto::getDirId));

        int dirIdPointer = 0;
        int exitDirNum = 0;

        for (String dir : dirs) {

            List<FoldDto> nowFolds = foldTreeMap.get(dirIdPointer);

            if (CollectionUtils.isEmpty(nowFolds)) {

                break;
            }

            for (FoldDto foldDto : nowFolds) {

                if (dir.equals(foldDto.getFileName())) {

                    dirIdPointer = foldDto.getFileId();

                    exitDirNum ++;
                    break;
                }
            }
        }

        List<String> notExistDirs = dirs.subList(exitDirNum, dirs.size());

        if (notExistDirs.isEmpty()) {

            return FoldDto.newBuilder().fileId(dirIdPointer).build();
        }

        List<FoldDto> folds = notExistDirs.stream().map(dir -> FoldDto.newBuilder().type(Const.FileType.FOLD.getCode())
                .namespaceId(fileDto.getNamespaceId()).fileName(dir)
                .userId(fileDto.getUserId()).clientId(fileDto.getClientId()).build())
                .collect(Collectors.toList());

        int flag = this.objectFileDao.insertFoldsBatch(folds);

        if (exitDirNum > 0) {
            updateParentDirId(folds, dirIdPointer);
        } else {
            updateParentDirId(folds, 0);
        }

        return folds.get(folds.size() - 1);
    }

    private void updateParentDirId(List<FoldDto> folds, int dirId) {

        for (int i = 0; i < folds.size(); ++i) {

            FoldDto foldDto = folds.get(i);

            if (0 == i) {

                foldDto.setDirId(dirId);
                continue;
            }

            FoldDto preFold = folds.get(i - 1);
            foldDto.setDirId(preFold.getFileId());
        }

        int flag = this.objectFileDao.updateFoldsDirIdBatch(folds);
    }*/


}
