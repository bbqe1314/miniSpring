package com.dreamtech.component.jdbc;

import java.sql.Connection;

public interface IDatabase {

    //取得连接
    Connection getConnection();

    //关闭
    void closeConnection(Connection conn);
}
