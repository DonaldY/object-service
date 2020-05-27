package com.donaldy.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.donaldy.common.Const;
import com.donaldy.common.S3Const;
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

@ConditionalOnProperty(name = "storage.type", havingValue = "ceph")
@Slf4j
@Component
public class CephUtils implements StorageUtils {

    private static final List<String> ImageSuffixList = newArrayList("jpg", "png", "bmp", "jpeg", "gif");

    /*public static void main(String[] args) {

        String clientRegion = "*** Client region ***";
        String bucketName = "qqq";
        String fileName = "*** Path to file to upload ***";
        Region region = Region.CN_Beijing;

        try {
            AWSCredentials credentials = new BasicAWSCredentials("qqq", "qqq");

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    "http://localhost:8080",""))
                    .build();

            S3Object s3Object = s3Client.getObject("ppp", "pom.xml");

            InputStream inputStream = new DataInputStream(s3Object.getObjectContent());

            File file = new File("hahah.xml");

            OutputStream outputStream = new FileOutputStream(file);

            IOUtils.copy(inputStream, outputStream);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 上传到私有
     * @param file    文件
     * @param fileDir 目录
     * @return        相对路径
     */
    @Override
    public String uploadObject(MultipartFile file, String fileDir) {

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PRIVATE_CLIENT;

        return uploadFile(s3ClientObject, file, fileDir);
    }

    /**
     * 上传文件，并返回相对路径
     * 返回文件格式： head/test/1547609644670015677.png
     * @param s3ClientObject        桶代理
     * @param file            文件
     * @param fileDir         文件夹
     * @return                head/test/1547609644670015677.png
     */
    private String uploadFile(S3Const.S3ClientObject s3ClientObject, MultipartFile file, String fileDir) {

        String fileSuffix = ContentTypeUtils.getFileSuffix(ObjectUtils.isEmpty(file.getOriginalFilename()) ? "" : file.getOriginalFilename(),
                file.getContentType());

        String fileName = NanoTimes.nanoTimestamp() + fileSuffix;

        String relativePath = fileDir +  File.separator + fileName;

        try {

            AmazonS3 s3Client = s3ClientObject.getInstance();

            /**
             * TODO objectMetadata
             * 没有必要设置 objectMetadata, SDK 仍然会做一遍
             */
            s3Client.putObject(s3ClientObject.getBucketName(), fileName,file.getInputStream(), new ObjectMetadata());

        } catch (IOException e) {
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "传输异常");
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

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PUBLIC_CLIENT;

        return uploadFile(s3ClientObject, file, fileDir);
    }

    @Deprecated
    @Override
    public String uploadObject(MultipartFile file, String fileDir, Integer strategy) {
        return "";
    }

    @Deprecated
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

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PRIVATE_CLIENT;

        this.previewFile(response, s3ClientObject, fileKey, fileName);
    }

    private void previewFile(HttpServletResponse response, S3Const.S3ClientObject s3ClientObject, String fileKey, String fileName) {

        String fileSuffix = fileKey.substring(fileKey.lastIndexOf(".") + 1).toLowerCase();

        fileName = Optional.ofNullable(fileName).orElse("文件");

        AmazonS3 s3Client = s3ClientObject.getInstance();

        try (
                S3Object s3Object = s3Client.getObject(s3ClientObject.getBucketName(), fileKey);
                InputStream inputStream = new DataInputStream(s3Object.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {
            if ("pdf".equals(fileSuffix)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            if (!ImageSuffixList.contains(fileSuffix)) {
                response.setContentType("application/x-download");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "找不到对应文件");
        }

    }

    @Override
    public void previewOrDownloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PUBLIC_CLIENT;

        this.previewFile(response, s3ClientObject, fileKey, fileName);
    }

    /**
     * 直接下载私有文件
     * @param response        response
     * @param fileKey         fileName: 文件名
     */
    @Override
    public void download(HttpServletResponse response, String fileKey, String fileName) {

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PRIVATE_CLIENT;

        this.downloadFile(response, s3ClientObject, fileKey, fileName);
    }

    private void downloadFile(HttpServletResponse response, S3Const.S3ClientObject s3ClientObject, String fileKey, String fileName) {

        fileName = Optional.ofNullable(fileName).orElse("下载文件");

        String savedFileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        AmazonS3 s3Client = s3ClientObject.getInstance();

        try (
                S3Object s3Object = s3Client.getObject(s3ClientObject.getBucketName(), fileKey);
                InputStream inputStream = new DataInputStream(s3Object.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {

            String encodeFileName = URLEncoder.encode(fileName, "UTF-8");

            response.setContentType("application/force-download");
            response.setHeader("downloadFileRealName", savedFileName);
            response.setHeader("downloadFileName", encodeFileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    "下载文件异常");
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw new RestfulException(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "找不到对应文件");
        }

    }

    @Override
    public void downloadPublicObject(HttpServletResponse response, String fileKey, String fileName) {

        S3Const.S3ClientObject s3ClientObject = S3Const.S3ClientObject.PUBLIC_CLIENT;

        this.downloadFile(response, s3ClientObject, fileKey, fileName);
    }

    @Override
    public String getPublicUrlByRelativePath(String path) {

        // bucket-name.s3-website.region.amazonaws.com

        return new StringBuilder("https://")
                .append(S3Const.S3ClientObject.PUBLIC_CLIENT.getBucketName())
                .append(".")
                .append(S3Const.ENDPOINT)
                .append(File.separator)
                .append(path).toString();
    }
}
