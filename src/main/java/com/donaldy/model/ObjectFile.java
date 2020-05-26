package com.donaldy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectFile {
    private Integer fileId;

    private Integer userId;

    private String clientId;

    private String fileName;

    private String path;

    private Byte accessType;

    private Date createdAt;

    private Date updatedAt;
}