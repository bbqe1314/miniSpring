package com.dreamtech.component.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISQLHandler {

    ResultSet doQuerySQL(String sql) throws SQLException;

    boolean doUpdateSQL(String sql) throws SQLException;
}
