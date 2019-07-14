package com.github.daihy8759.common.util.response;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class ApiMessageCodec implements MessageCodec<ApiResponse, ApiResponse> {

    @Override
    public void encodeToWire(Buffer buffer, ApiResponse apiResponse) {
        JsonObject jsonToEncode = new JsonObject();
        jsonToEncode.put("code", apiResponse.getCode());
        jsonToEncode.put("success", apiResponse.getSuccess());
        jsonToEncode.put("message", apiResponse.getMessage());
        jsonToEncode.put("data", apiResponse.getData());
        String jsonToStr = jsonToEncode.encode();
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public ApiResponse decodeFromWire(int position, Buffer buffer) {
        int _pos = position;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos += 4, _pos + length);
        JsonObject contentJson = new JsonObject(jsonStr);
        int code = contentJson.getInteger("code");
        boolean success = contentJson.getBoolean("success");
        String message = contentJson.getString("message");
        Object data = contentJson.getValue("data");
        return new ApiResponse().setCode(code).setSuccess(success).setMessage(message)
                .setData(data);
    }

    @Override
    public ApiResponse transform(ApiResponse apiResponse) {
        return apiResponse;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
