package com.donaldy.common;

import com.aliyun.oss.OSSClient;
import lombok.Getter;

public class OSSConst {

    public static final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY_SECRET = "";

    @Getter
    public enum OSSClientObject {
        PROD_PRIVATE(BucketName.PROD_PRIVATE),
        PROD_PUBLIC(BucketName.PROD_PUBLIC),
        DEV_PRIVATE(BucketName.DEV_PRIVATE),
        DEV_PUBLIC(BucketName.DEV_PUBLIC);

        private final OSSClient instance;
        private final String bucketName;

        OSSClientObject(BucketName bucketName) {
            this.instance = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            this.bucketName = bucketName.bucketName;
        }

        private enum BucketName {
            PROD_PRIVATE("prod_private"),
            PROD_PUBLIC("prod_public"),
            DEV_PRIVATE("dev_private"),
            DEV_PUBLIC("dev_public");

            private String bucketName;

            BucketName(String bucketName) {
                this.bucketName = bucketName;
            }
        }
    }

    @Getter
    public enum FileType {
        PRIVATE(0, "私有文件"),
        PUBLIC(1, "公有文件");

        private int code;
        private String value;

        FileType(int code, String value) {
            this.code = code;
            this.value = value;
        }

    }
}
