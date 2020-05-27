package com.donaldy.utils;

import com.donaldy.common.Const;
import com.donaldy.handler.RestfulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@ConditionalOnProperty(name = "storage.type", havingValue = "fs")
@Slf4j
@Component
public class FileSystemUtils implements StorageUtils {

    private static final List<String> IMAGE_SUFFIX_LIST = newArrayList("jpg", "png", "bmp", "jpeg", "gif");

    @Value("${fs.root.dir.path}")
    private String ROOT_PATH;

    @Value("${fs.public.bucket.name}")
    private String PUBLIC_BUCKET_NAME;

    @Value("${fs.private.bucket.name}")
    private String PRIVATE_BUCKET_NAME;

    @Value("${fs.domain.name}")
    private String DOMAIN_NAME;

    /**
     * 上传私有文件
     *
     * @param file     文件
     * @param fileDir  文件目录
     * @return         相对路径
     */
    @Override
    public String uploadObject(MultipartFile file, String fileDir) {

        return this.uploadFile(file, this.PRIVATE_BUCKET_NAME, fileDir);
    }

    /**
     * 上传公有文件
     *
     * @param file    文件
     * @param fileDir 目录
     * @return        相对路径
     */
    @Override
    public String uploadPublicObject(MultipartFile file, String fileDir) {

        return this.uploadFile(file, this.PUBLIC_BUCKET_NAME, fileDir);
    }

    /**
     * 上传文件至目录, 并返回相对路径
     * 返回文件格式：public/test/1547609644670015677.png
     *
     * 存储地址：/home/fileservice/public/test/1547609644670015677.png
     * @param file       文件
     * @param fileDir    文件目录
     * @param BucketName 桶名
     * @return           相对路径 test/1547609644670015677.png (不包含桶名)
     */
    private String uploadFile(MultipartFile file, String BucketName, String fileDir) {

        String fileSuffix = ContentTypeUtils.getFileSuffix(ObjectUtils.isEmpty(file.getOriginalFilename())? "" : file.getOriginalFilename(),
                file.getContentType());

        String fileName = NanoTimes.nanoTimestamp() + fileSuffix;

        String relativePath = fileDir + File.separator + fileName;

        String path = ROOT_PATH + File.separator + BucketName + File.separator + relativePath;

        File dest = new File(path);

        if (!dest.getParentFile().exists()) {

            dest.getParentFile().mkdirs();
        }

        try {

            file.transferTo(dest);

        } catch (IOException e) {

            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "存储文件异常");
        }

        return relativePath;
    }

    @Override
    public String uploadObject(MultipartFile file, String fileDir, Integer strategy) {
        return null;
    }


    @Override
    @Deprecated
    public String uploadPublicObject(MultipartFile file, String fileDir, Integer strategy) {
        return null;
    }

    /**
     * 预览私有文件
     *
     * @param response 响应
     * @param fileKey  文件路径
     * @param fileName 文件名
     */
    @Override
    public void previewOrDownload(HttpServletResponse response, String fileKey, String fileName) {

        String path = this.ROOT_PATH + File.separator + this.PRIVATE_BUCKET_NAME + File.separator + fileKey;

        File file = new File(path);

        this.previewFile(response, file, fileName);
    }

    /**
     * 预览公有文件
     *
     * @param response 响应
     * @param fileKey  文件路径
     * @param fileName 文件名
     */
    @Override
    public void previewOrDownloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        String path = this.ROOT_PATH + File.separator + this.PUBLIC_BUCKET_NAME + File.separator + fileKey;

        File file = new File(path);

        this.previewFile(response, file, fileName);
    }

    private void previewFile(HttpServletResponse response, File file, String fileName) {

        String fileSuffix = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

        fileName = Optional.ofNullable(fileName).orElse("文件");

        try (
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream()
        ) {
            if (!IMAGE_SUFFIX_LIST.contains(fileSuffix)) {
                response.setContentType("application/x-download");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            if ("pdf".equals(fileSuffix)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("下载指定文件时失败  filePath = " + file.getName() , e);
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        }

    }

    /**
     * 下载私有文件
     * @param response 响应
     * @param fileKey  路径
     * @param fileName 原文件名
     */
    @Override
    public void download(HttpServletResponse response, String fileKey, String fileName) {

        String path = this.ROOT_PATH + File.separator + this.PRIVATE_BUCKET_NAME + File.separator + fileKey;

        File file = new File(path);

        this.downloadFile(response, file, fileKey, fileName);
    }

    /**
     * 下载公有文件
     * @param response 响应
     * @param fileKey  路径
     * @param fileName 原文件名
     */
    @Override
    public void downloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        String path = this.ROOT_PATH + File.separator + this.PUBLIC_BUCKET_NAME + File.separator + fileKey;

        File file = new File(path);

        this.downloadFile(response, file, fileKey, fileName);
    }

    private void downloadFile(HttpServletResponse response, File file, String fileKey, String fileName) {

        String savedFileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        try(
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream()
        ) {

            String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/force-download");
            response.setHeader("downloadFileName", encodeFileName);
            response.setHeader("downloadFileRealName", savedFileName);
            response.addHeader("Content-Disposition", "attachment;fileName=" + encodeFileName);
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "文件异常");
        }
    }

    /**
     * 获取全路径
     *
     * @param path 相对路径
     * @return     全路径
     */
    @Override
    public String getPublicUrlByRelativePath(String path) {

        return this.DOMAIN_NAME + File.separator + this.PUBLIC_BUCKET_NAME + File.separator + path;
    }

}
