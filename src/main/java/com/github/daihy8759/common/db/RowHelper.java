package com.github.daihy8759.common.db;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.sqlclient.Row;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class RowHelper {

    public JsonObject toJson(Row row) {
        JsonObject jsonObject = new JsonObject();
        int size = row.size();
        for (int i = 0; i < size; i++) {
            String columnName = row.getColumnName(i);
            Object value = row.getValue(i);
            if (value != null) {
                if (value instanceof LocalDateTime) {
                    jsonObject.put(columnName, ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    jsonObject.put(columnName, row.getValue(i));
                }
            }
        }
        return jsonObject;
    }

    public <T> T toObject(Row row, Class<T> t) {
        return toJson(row).mapTo(t);
    }
}
