package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.AccountType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccountTypeRowMapper implements RowMapper<AccountType> {
    @Override
    public AccountType mapRow(ResultSet resultSet, int i) throws SQLException {
        AccountType accountType = new AccountType();
        accountType.setId(resultSet.getLong("id"));
        accountType.setName(resultSet.getString("name"));
        accountType.setPrice(resultSet.getDouble("price"));
        accountType.setMaxWarehouses(resultSet.getInt("max_warehouses"));
        accountType.setMaxWarehouseDepth(resultSet.getInt("max_warehouse_depth"));
        accountType.setMaxUsers(resultSet.getInt("max_users"));
        accountType.setMaxSuppliers(resultSet.getInt("max_suppliers"));
        accountType.setMaxClients(resultSet.getInt("max_clients"));
        accountType.setDeepWarehouseAnalytics(resultSet.getBoolean("deep_warehouse_analytics"));
        accountType.setItemStorageAdvisor(resultSet.getBoolean("item_storage_advisor"));
        accountType.setActive(resultSet.getBoolean("active"));
        return accountType;
    }
}
