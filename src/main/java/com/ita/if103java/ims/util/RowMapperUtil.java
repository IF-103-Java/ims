package com.ita.if103java.ims.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class RowMapperUtil {
    public static <T> void setValueOrNull(Consumer<T> consumer, T value, ResultSet rs) throws SQLException {
        consumer.accept(rs.wasNull() ? null : value);
    }
}
