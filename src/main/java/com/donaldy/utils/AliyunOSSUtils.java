package com.donaldy.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.donaldy.common.OSSConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Calendar;

import java.util.UUID;


@Slf4j
public class AliyunOSSUtils {

    private static final String SUFFIX_STRING = "jpg png bmp jpeg pdf";

    /**
     * upload file
     * @param file           文件
     * @param fileDir        文件目录
     * @return               文件名
     * @throws IOException   IO异常
     */
    public static String upload(OSSConst.OSSClientObject ossClientObject, MultipartFile file,
                                String fileDir) throws IOException {

        createBucket(ossClientObject.getInstance(), ossClientObject.getBucketName());

        return uploadFile(ossClientObject, file, fileDir);
    }

    // TODO : 整改
    private static void createBucket(OSSClient ossClient, String _bucketName) {
        if(!ossClient.doesBucketExist(_bucketName)){
            CreateBucketRequest bucketRequest = new CreateBucketRequest(_bucketName);
            bucketRequest.setCannedACL(CannedAccessControlList.Private);
            ossClient.createBucket(bucketRequest);
            log.info("create oss bucket {}", _bucketName);
        }
    }

    private static String uploadFile(OSSConst.OSSClientObject ossClientObject, MultipartFile file,
                                     String fileDir) throws IOException {

        // format : project/b12f90037c164821b87ad7744a35e67b-1536045945002_test.txt
        String fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + "-" +
                Calendar.getInstance().getTimeInMillis();
        String fileURL = fileDir +  File.separator + fileName;

        try {
            PutObjectResult result = ossClientObject.getInstance().putObject(ossClientObject.getBucketName(),
                    fileURL, file.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IOException("上传异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("OSS异常");
        }

        return fileName;
    }

    /**
     * 上传文件到公共桶, 返回路径
     *
     * @param ossClientObject  ossClient对象
     * @param file            文件
     * @param fileDir         文件目录
     * @return                相对地址
     * @throws IOException    IO异常
     */
    public static String uploadPublicReturnPath(OSSConst.OSSClientObject ossClientObject, MultipartFile file,
                                      String fileDir) throws IOException {

        OSSClient ossClient = ossClientObject.getInstance();

        String bucketName = ossClientObject.getBucketName();

        createBucket(ossClient, bucketName);

        String path = uploadFile(ossClientObject, file, fileDir);

        // 校验公共桶
        ossClient.setObjectAcl(bucketName, fileDir + File.separator + path, CannedAccessControlList.PublicRead);

        return path;
    }

    /**
     * 上传文件到公共桶, 返回完整url
     *
     * @param bucketName      桶名
     * @param path            路径
     * @return                完整URL
     * @throws IOException    IO异常
     */
    public static String getCompleteUrl(String bucketName, String path) throws IOException {

        return new StringBuilder("https://")
                .append(bucketName)
                .append(".")
                .append(OSSConst.ENDPOINT)
                .append(File.separator)
                .append(path)
                .append(File.separator)
                .append(URLEncoder.encode(path, "UTF-8").replaceAll("\\+", "%20")).toString();
    }

    /**
     * 预览图片 PDF，下载其他文件
     * @param response        response
     * @param fileKey         fileName: UUID-时间戳_文件名
     * @throws IOException    IO异常
     */
    public static void previewOrDownload(OSSConst.OSSClientObject ossClientObject, HttpServletResponse response,
                                         String fileKey) throws IOException {
        OSSClient ossClient = ossClientObject.getInstance();

        OSSObject ossObject = ossClient.getObject(ossClientObject.getBucketName(), fileKey);

        String fileName = fileKey.substring(fileKey.lastIndexOf("_") + 1);

        String fileSuffix = fileKey.substring(fileKey.lastIndexOf(".") + 1).toLowerCase();

        try(
                InputStream inputStream = new DataInputStream(ossObject.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {
            if (!SUFFIX_STRING.contains(fileSuffix)) {
                response.setContentType("application/x-download");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IOException("下载文件异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("找不到对应文件");
        }
    }

    /**
     * download file
     * @param response        response
     * @param fileKey         fileName: UUID-时间戳_文件名
     * @throws IOException    IO异常
     */
    public static void download(OSSConst.OSSClientObject ossClientObject, HttpServletResponse response,
                                String fileKey) throws IOException {

        OSSClient ossClient = ossClientObject.getInstance();

        OSSObject ossObject = ossClient.getObject(ossClientObject.getBucketName(), fileKey);

        String fileName = fileKey.substring(fileKey.lastIndexOf("_") + 1);

        try(
                InputStream inputStream = new DataInputStream(ossObject.getObjectContent());
                OutputStream outputStream = response.getOutputStream()
        ) {

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IOException("下载文件异常");
        } catch (OSSException e) {
            log.error(e.getMessage());
            throw new OSSException("找不到对应文件");
        }
    }

    /**
     * get some OSS object
     * OSS 分页查询，只能拿到部分
     * @param _bucketName     桶名
     */
    public static void listObjects(OSSConst.OSSClientObject ossClientObject, String _bucketName) {
        OSSClient ossClient = ossClientObject.getInstance();

        ObjectListing listing = ossClient.listObjects(_bucketName);

        for (OSSObjectSummary ossObjectSummary : listing.getObjectSummaries()) {
            System.out.println(ossObjectSummary.getKey());
        }

    }
}
