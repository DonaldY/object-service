package com.donaldy.common;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "storage.type", havingValue = "ceph")
@Component
public class S3Const {

    public static String ENDPOINT;
    private static String INTERNAL_ENDPOINT;

    private static String ACCESS_KEY_ID;
    private static String ACCESS_KEY_SECRET;

    private static String PRIVATE_BUCKET_NAME;
    private static String PUBLIC_BUCKET_NAME;

    @Value("${ceph.endpoint}")
    public void setEndPoint(String endPoint) {
        ENDPOINT = endPoint;
    }

    @Value("${ceph.internal.endpoint}")
    public void setInternalEndpoint(String endpoint) { INTERNAL_ENDPOINT = endpoint; }

    @Value("${ceph.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
    }

    @Value("${ceph.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        ACCESS_KEY_SECRET = accessKeySecret;
    }

    @Value("${ceph.private.bucketName}")
    public void setPrivateBucketName(String privateBucketName) {
        PRIVATE_BUCKET_NAME = privateBucketName;
    }

    @Value("${ceph.public.bucketName}")
    public void setPublicBucketName(String publicBucketName) {
        PUBLIC_BUCKET_NAME = publicBucketName;
    }

    @Getter
    public enum S3ClientObject {
        PRIVATE_CLIENT(BucketName.PRIVATE_BUCKET),
        PUBLIC_CLIENT(BucketName.PUBLIC_BUCKET);

        private AmazonS3 instance;
        private String bucketName;

        S3ClientObject(BucketName bucketName) {
            AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

            this.instance = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    INTERNAL_ENDPOINT,""))
                    .build();
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
