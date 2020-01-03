package com.ita.if103java.ims.mapper.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public abstract class AbstractRowMapper {
    protected <T> void setValueOrNull(Consumer<T> consumer, T value, ResultSet rs) throws SQLException {
        consumer.accept(rs.wasNull() ? null : value);
    }
}
