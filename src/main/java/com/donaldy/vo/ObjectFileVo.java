package com.donaldy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class ObjectFileVo implements Serializable {

    private Integer fileId;
    private String  fileName;
    private Long    fileSize;
    private String  path;
    private Integer dirId;
    private String  url;
    private Date    createdAt;
    private Date    updatedAt;

    public ObjectFileVo() {}

}
