package com.donaldy.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestfulException extends RuntimeException {
    private Integer code;
    private String msg;

    public RestfulException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}