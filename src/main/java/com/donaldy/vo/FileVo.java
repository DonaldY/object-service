package com.donaldy.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FileVo {

    private Integer fileId;

    private Integer userId;

    private String  fileName;

    private String  fileSize;

    private String  path;

    private Integer dirId;

    private Date    createdAt;

    private Date    updatedAt;
}
