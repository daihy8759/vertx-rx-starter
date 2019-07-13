package com.github.daihy8759.common.util.response;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantCode {

    public final RetCode SUCCESS = new RetCode(0, "success");

    public final RetCode LOGIN_FAIL = new RetCode(202034, "login fail");
    public static RetCode TOKEN_NOT_FOUND = new RetCode(202035, "token not found！");
    public static RetCode TOKEN_REQUIRE_FAIL = new RetCode(202036, "app_id or secret not correct！");

}
