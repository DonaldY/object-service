package com.donaldy.common;

import com.aliyun.oss.OSSClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "storage.type", havingValue = "oss")
@Component
public class OSSConst {

    public  static String ENDPOINT;
    private static String ACCESS_KEY_ID;
    private static String ACCESS_KEY_SECRET;

    private static String PRIVATE_BUCKET_NAME;
    private static String PUBLIC_BUCKET_NAME;

    @Value("${aliyun.oss.endpoint}")
    public void setEndPoint(String endPoint) {
        ENDPOINT = endPoint;
    }

    @Value("${aliyun.oss.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
    }

    @Value("${aliyun.oss.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        ACCESS_KEY_SECRET = accessKeySecret;
    }

    @Value("${aliyun.oss.private.bucketName}")
    public void setPrivateBucketName(String privateBucketName) {
        PRIVATE_BUCKET_NAME = privateBucketName;
    }

    @Value("${aliyun.oss.public.bucketName}")
    public void setPublicBucketName(String publicBucketName) {
        PUBLIC_BUCKET_NAME = publicBucketName;
    }

    @Getter
    public enum OSSClientObject {
        PRIVATE_CLIENT(BucketName.PRIVATE_BUCKET),
        PUBLIC_CLIENT(BucketName.PUBLIC_BUCKET);

        private OSSClient instance;
        private String bucketName;

        OSSClientObject(BucketName bucketName) {
            this.instance = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            this.bucketName = bucketName.bucketName;
        }

        @Getter
        private enum BucketName {
            PRIVATE_BUCKET(PRIVATE_BUCKET_NAME),
            PUBLIC_BUCKET(PUBLIC_BUCKET_NAME);

            private String bucketName;

            BucketName(String bucketName) {
                this.bucketName = bucketName;
            }
        }
    }
}
