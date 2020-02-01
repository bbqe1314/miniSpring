package com.dreamtech.component.jdbc;

import com.dreamtech.anno.Component;
import com.dreamtech.component.IComponent;
import com.dreamtech.context.ApplicationArgs;
import com.dreamtech.context.ApplicationContext;
import com.dreamtech.exceptions.MINIExceptionProcessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * 数据库组件。1.0仅支持mysql
 */
@Component
public class DatabaseComponent implements IDatabase, IComponent {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    HashMap<String, Object> appArgs;

    private DatabaseConnectionPool pool;

    private SQLHandler sqlHandler;

    public DatabaseComponent() {
    }

    @Override
    public void init() {
        try {
            this.appArgs = ApplicationContext.getInstance().getAppArgs();
            Class.forName(MYSQL_DRIVER);
            pool = new DatabaseConnectionPool(this.appArgs);

            pool.setMaxCount(Integer.parseInt(appArgs.get(ApplicationArgs.DATABASE_CONNECTION_POOL_MAX_COUNT).toString()));
            pool.setMinCount(Integer.parseInt(appArgs.get(ApplicationArgs.DATABASE_CONNECTION_POOL_MIN_COUNT).toString()));
            pool.init();

            sqlHandler = new SQLHandler(this);
        } catch (ClassNotFoundException e) {
            MINIExceptionProcessor.getInstance().putException("mysql driver create error", e);
        }
    }

    @Override
    public Connection getConnection() {
        return pool.getConnection();
    }


    @Override
    public void closeConnection(Connection conn) {
        pool.closeConnection(conn);
    }

    /**
     * 处理QuerySQL
     */
    public ResultSet doQuerySQL(String sql) throws Exception {
        return sqlHandler.doQuerySQL(sql);
    }

    /**
     * 处理UpdateSQL
     * 返回执行成功与否
     */
    public boolean doUpdateSQL(String sql) throws Exception {
        return sqlHandler.doUpdateSQL(sql);
    }

    @Override
    public void stop() {
        pool.clear();
    }
}
