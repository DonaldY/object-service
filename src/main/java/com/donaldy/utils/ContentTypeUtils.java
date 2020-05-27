package com.donaldy.utils;

import com.google.common.collect.ImmutableMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

public class ContentTypeUtils {

    private static Map<String, String> CONTENT_TYPE_SUFFIX_MAP =
            new ImmutableMap.Builder<String, String>()
                    .put("image/jpeg", ".jpeg")
                    .put("image/png", ".png")
                    .put("application/pdf", ".pdf")
                    .build();

    /**
     * 根据文件名与内容类型获取文件
     * @param originalFilename 原始文件名
     * @param contentType      文件内容类型
     * @return                 文件后缀(包含 .)
     */
    static String getFileSuffix(String originalFilename, String contentType) {

        if (!originalFilename.contains(".")) {

            String suffix = CONTENT_TYPE_SUFFIX_MAP.get(contentType.toLowerCase());

            return ObjectUtils.isEmpty(suffix) ? "" : suffix;
        }

        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        if (!StringUtils.isEmpty(fileSuffix) && fileSuffix.length() > 1) {

            return fileSuffix;
        }

        return CONTENT_TYPE_SUFFIX_MAP.get(contentType.toLowerCase());
    }

}