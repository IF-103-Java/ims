package com.ita.if103java.ims.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

public class JDBCTemplateConfig {
    @Value("${db.driver}")
    private String driverClassName;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.minPoolSize}")
    private int minPoolSize;

    @Value("${db.maxPoolSize}")
    private int maxPoolSize;

    @Value("${db.poolIncrement}")
    private int poolIncrement;

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setAcquireIncrement(poolIncrement);
        dataSource.setMaxPoolSize(maxPoolSize);

        return dataSource;
    }
}
