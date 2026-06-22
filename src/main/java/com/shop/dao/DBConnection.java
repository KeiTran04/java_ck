package com.shop.dao;

import com.shop.config.DataSourceConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        return DataSourceConfig.getConnection();
    }
}
