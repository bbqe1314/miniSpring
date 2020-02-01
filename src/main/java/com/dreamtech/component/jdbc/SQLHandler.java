package com.dreamtech.component.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLHandler implements ISQLHandler {

    private DatabaseComponent manager;

    SQLHandler(DatabaseComponent manager) {
        this.manager = manager;
    }


    @Override
    public boolean doUpdateSQL(String sql) throws SQLException {
        Connection conn = manager.getConnection();
        Statement st = conn.createStatement();
        int result = st.executeUpdate(sql);
        st.close();
        manager.closeConnection(conn);
        return result != 0;
    }


    @Override
    public ResultSet doQuerySQL(String sql) throws SQLException {
        Connection conn = manager.getConnection();
        Statement st = conn.createStatement();
        ResultSet result = st.executeQuery(sql);
        st.close();
        manager.closeConnection(conn);
        return result;
    }
}
