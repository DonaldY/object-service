package com.donaldy.common;

import lombok.Getter;

public class Const {

    public enum HttpStatusCode {

        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        @Getter
        private String value;
        @Getter
        private Integer code;

        HttpStatusCode(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public static HttpStatusCode codeOf(Integer code) {
            for (HttpStatusCode statusEnum : values()) {
                if (code.intValue() == statusEnum.getCode().intValue()) {
                    return statusEnum;
                }
            }
            throw new RuntimeException("Can't find http_status_code by code");
        }
    }

}
