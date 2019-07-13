package com.github.daihy8759.common.util.response;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApiResponse {

    private int code;

    private Boolean success;

    private String message;

    private Object data;

    public static ApiResponse success() {
        return new ApiResponse().setSuccess(true);
    }

    public static ApiResponse fail() {
        return new ApiResponse().setSuccess(false);
    }

    public ApiResponse setRetCode(RetCode retCode) {
        this.code = retCode.getCode();
        this.message = retCode.getMsg();
        return this;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code", code);
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        jsonObject.put("data", data);
        return jsonObject.toString();
    }
}
