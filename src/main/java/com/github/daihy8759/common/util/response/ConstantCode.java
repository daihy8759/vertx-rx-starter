package com.github.daihy8759.common.util.response;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantCode {

    public final RetCode SUCCESS = new RetCode(0, "success");

    public final RetCode SERVER_ERROR = new RetCode(10000, "server error！");

    public final RetCode LOGIN_FAIL = new RetCode(22034, "login fail");
    public final RetCode TOKEN_NOT_FOUND = new RetCode(22035, "token not found！");
    public final RetCode TOKEN_REQUIRE_FAIL = new RetCode(22036, "app_id or secret not correct！");

}
