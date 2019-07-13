package com.github.daihy8759.common.util.response;

import lombok.Data;

@Data
public class RetCode {

    private Integer code;
    private String msg;

    public RetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
