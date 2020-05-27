package com.donaldy.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.donaldy.common.Const;
import com.donaldy.common.OSSConst;
import com.donaldy.handler.RestfulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

@ConditionalOnProperty(name = "storage.type", havingValue = "oss")
@Slf4j
@Component
public class AliyunOSSUtils implements StorageUtils {

    private static final List<String> ImageSuffixList = newArrayList("jpg", "png", "bmp", "jpeg", "gif");

    /**
     * 上传到私有桶
     * @param file    文件
     * @param fileDir 目录
     * @return        相对路径
     */
    @Override
    public String uploadObject(MultipartFile file, String fileDir) {

        OSSConst.OSSClientObject ossClientObject =  OSSConst.OSSClientObject.PRIVATE_CLIENT;

        validateBucket(ossClientObject.getInstance(), ossClientObject.getBucketName());

        return uploadFile(ossClientObject, file, fileDir);
    }

    private void validateBucket(OSSClient ossClient, String _bucketName) {
        if(!ossClient.doesBucketExist(_bucketName)){

            log.error("找不到这个桶 ： {}", _bucketName);

            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "找不到这个桶");
        }
    }

    /**
     * 上传文件，并返回相对路径
     * 返回文件格式： head/test/1547609644670015677.png
     * @param ossClientObject 桶代理
     * @param file            文件
     * @param fileDir         文件夹
     * @return                head/test/1547609644670015677.png
     */
    private String uploadFile(OSSConst.OSSClientObject ossClientObject, MultipartFile file,
                              String fileDir) {

        String fileSuffix = ContentTypeUtils.getFileSuffix(ObjectUtils.isEmpty(file.getOriginalFilename()) ? "" : file.getOriginalFilename(),
                file.getContentType());

        String fileName = NanoTimes.nanoTimestamp() + fileSuffix;

        String relativePath = fileDir +  File.separator + fileName;

        try (
                InputStream inputStream = file.getInputStream()
        ){

            PutObjectResult result = ossClientObject.getInstance().putObject(ossClientObject.getBucketName(),
                    relativePath, inputStream);

            result.getETag();

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "传输异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("OSS异常");
        }

        return relativePath;
    }

    /**
     * 上传文件到公共桶, 返回路径
     *
     * @param file            文件
     * @param fileDir         文件目录
     * @return                相对地址
     */
    @Override
    public String uploadPublicObject(MultipartFile file, String fileDir) {

        OSSConst.OSSClientObject ossClientObject = OSSConst.OSSClientObject.PUBLIC_CLIENT;

        OSSClient ossClient = ossClientObject.getInstance();

        String bucketName = ossClientObject.getBucketName();

        validateBucket(ossClient, bucketName);

        String relativePath = uploadFile(ossClientObject, file, fileDir);

        // 校验公共桶
        ossClient.setObjectAcl(bucketName, relativePath, CannedAccessControlList.PublicRead);

        return relativePath;
    }

    /**
     * 上传文件到公共桶, 返回完整url
     *
     * @param bucketName      桶名
     * @param path            路径
     * @return                完整URL
     */
    public static String getCompleteUrl(String bucketName, String path) {

        return new StringBuilder("https://")
                .append(bucketName)
                .append(".")
                .append(OSSConst.ENDPOINT)
                .append(File.separator)
                .append(path).toString();
        //.append(URLEncoder.encode(path, "UTF-8").replaceAll("\\+", "%20")).toString();
    }

    /**
     * 上传私有文件（选择策略）
     *
     * 【oss 不需要】
     *
     * @param file     文件
     * @param fileDir  目录
     * @param strategy 策略
     * @return         路径
     */
    @Override
    public String uploadObject(MultipartFile file, String fileDir, Integer strategy) {

        return "";
    }

    /**
     * 上传公共文件（选择策略）
     *
     * 【oss 不需要】
     *
     * @param file     文件
     * @param fileDir  目录
     * @param strategy 策略
     * @return         路径
     */
    @Override
    public String uploadPublicObject(MultipartFile file, String fileDir, Integer strategy) {

        return "";
    }

    /**
     * 预览图片 PDF，下载其他文件
     * @param response        response
     * @param fileKey         fileName 文件名
     */
    @Override
    public void previewOrDownload(HttpServletResponse response, String fileKey, String fileName) {

        OSSConst.OSSClientObject ossClientObject = OSSConst.OSSClientObject.PRIVATE_CLIENT;

        this.previewFile(response, ossClientObject, fileKey, fileName);
    }

    @Override
    public void previewOrDownloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        OSSConst.OSSClientObject ossClientObject = OSSConst.OSSClientObject.PUBLIC_CLIENT;

        this.previewFile(response, ossClientObject, fileKey, fileName);
    }

    private void previewFile(HttpServletResponse response, OSSConst.OSSClientObject ossClientObject, String fileKey,
                             String fileName) {

        OSSClient ossClient = ossClientObject.getInstance();

        OSSObject ossObject = ossClient.getObject(ossClientObject.getBucketName(), fileKey);

        String fileSuffix = fileKey.substring(fileKey.lastIndexOf(".") + 1).toLowerCase();

        fileName = Optional.ofNullable(fileName).orElse("文件");

        try(
                InputStream inputStream = new DataInputStream(ossObject.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {
            if (!ImageSuffixList.contains(fileSuffix)) {
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
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("找不到对应文件");
        }

    }

    /**
     * 直接下载私有文件
     * @param response        response
     * @param fileKey         fileName: 文件名
     */
    @Override
    public void download(HttpServletResponse response, String fileKey, String fileName) {

        OSSConst.OSSClientObject ossClientObject = OSSConst.OSSClientObject.PRIVATE_CLIENT;

        this.downloadFile(response, ossClientObject, fileKey, fileName);
    }

    /**
     * 直接下载公共文件
     * @param response 响应
     * @param fileKey  路径
     * @param fileName 原文件名
     */
    @Override
    public void downloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        OSSConst.OSSClientObject ossClientObject = OSSConst.OSSClientObject.PUBLIC_CLIENT;

        this.downloadFile(response, ossClientObject, fileKey, fileName);
    }

    private void downloadFile(HttpServletResponse response, OSSConst.OSSClientObject ossClientObject, String fileKey,
                              String fileName) {

        OSSClient ossClient = ossClientObject.getInstance();

        OSSObject ossObject = ossClient.getObject(ossClientObject.getBucketName(), fileKey);

        fileName = Optional.ofNullable(fileName).orElse("下载文件");

        String savedFileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        try(
                InputStream inputStream = new DataInputStream(ossObject.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {

            String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/force-download");
            response.setHeader("downloadFileName", encodeFileName);
            response.setHeader("downloadFileRealName", savedFileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("找不到对应文件");
        }
    }

    @Override
    public String getPublicUrlByRelativePath(String path) {

        return new StringBuilder("https://")
                .append(OSSConst.OSSClientObject.PUBLIC_CLIENT.getBucketName())
                .append(".")
                .append(OSSConst.ENDPOINT)
                .append(File.separator)
                .append(path).toString();
    }
}
