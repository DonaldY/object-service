package com.donaldy.model;

import lombok.Data;

import java.util.Date;

@Data
public class ObjectFile {
    private Integer fileId;

    private Integer userId;

    private String clientId;

    private String fileName;

    private Long fileSize;

    private String path;

    private Integer type;

    private String url;

    private Integer dirId;

    private Integer isDeleted;

    private Date createdAt;

    private Date updatedAt;

    public ObjectFile() {

    }

    private ObjectFile(Builder builder) {
        setFileId(builder.fileId);
        setUserId(builder.userId);
        setClientId(builder.clientId);
        setFileName(builder.fileName);
        setFileSize(builder.fileSize);
        setPath(builder.path);
        setType(builder.type);
        setUrl(builder.url);
        setDirId(builder.dirId);
        setIsDeleted(builder.isDeleted);
        setCreatedAt(builder.createdAt);
        setUpdatedAt(builder.updatedAt);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer fileId;
        private Integer userId;
        private String clientId;
        private String fileName;
        private Long fileSize;
        private String path;
        private Integer type;
        private String url;
        private Integer dirId;
        private Integer isDeleted;
        private Date createdAt;
        private Date updatedAt;

        private Builder() {
        }

        public Builder fileId(Integer val) {
            fileId = val;
            return this;
        }

        public Builder userId(Integer val) {
            userId = val;
            return this;
        }

        public Builder clientId(String val) {
            clientId = val;
            return this;
        }

        public Builder fileName(String val) {
            fileName = val;
            return this;
        }

        public Builder fileSize(Long val) {
            fileSize = val;
            return this;
        }

        public Builder path(String val) {
            path = val;
            return this;
        }

        public Builder type(Integer val) {
            type = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder dirId(Integer val) {
            dirId = val;
            return this;
        }

        public Builder isDeleted(Integer val) {
            isDeleted = val;
            return this;
        }

        public Builder createdAt(Date val) {
            createdAt = val;
            return this;
        }

        public Builder updatedAt(Date val) {
            updatedAt = val;
            return this;
        }

        public ObjectFile build() {
            return new ObjectFile(this);
        }
    }
}