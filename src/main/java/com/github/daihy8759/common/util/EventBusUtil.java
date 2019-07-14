package com.github.daihy8759.common.util;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import com.github.daihy8759.common.exception.BaseException;
import com.github.daihy8759.common.util.response.ApiResponse;
import com.github.daihy8759.common.util.response.ConstantCode;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Log4j2
@UtilityClass
public class EventBusUtil {

    public void catchException(Throwable e, Message<JsonObject> message) {
        if (e instanceof BaseException) {
            message.reply(ApiResponse.fail().setRetCode(((BaseException) e).getRetCode()));
            return;
        }
        log.error(ExceptionUtils.getStackTrace(e));
        message.reply(ApiResponse.fail().setRetCode(ConstantCode.SERVER_ERROR));
    }
}
