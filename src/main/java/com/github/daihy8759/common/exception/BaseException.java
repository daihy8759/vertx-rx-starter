package com.github.daihy8759.common.exception;

import com.github.daihy8759.common.util.response.RetCode;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private RetCode retCode;

    public BaseException(RetCode retCode) {
        super(retCode.getMsg());
        this.retCode = retCode;
    }

    public BaseException(int code, String msg) {
        super(msg);
        this.retCode = new RetCode(code, msg);
    }

    public RetCode getRetCode() {
        return retCode;
    }
}
