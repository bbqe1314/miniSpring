package com.dreamtech.component.jdbc;

import com.dreamtech.context.ApplicationArgs;
import com.dreamtech.exceptions.MINIExceptionProcessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 自定义的数据库连接池
 * 扩容因子loadFactor作为扩展项 1.0暂未使用
 */
public class DatabaseConnectionPool implements IDatabase {

    // 最小连接数
    private int minCount = 1;

    //最大连接数
    private int maxCount = 10;
    //连接池
    private static final LinkedList<Connection> pools = new LinkedList<>();

    //扩容因子
    private float loadFactor = 0.75f;

    private HashMap<String, Object> appArgs;


    private Connection buildConnection() {
        try {
            String dbUrl = "jdbc:mysql://" +
                    appArgs.get(ApplicationArgs.DATABASE_ADDRESS) +
                    ":" + appArgs.get(ApplicationArgs.DATABASE_PORT) +
                    "/" + appArgs.get(ApplicationArgs.DATABASE_NAME) +
                    "?" + appArgs.get(ApplicationArgs.DATABASE_CONFIG);
            return DriverManager.getConnection(dbUrl, appArgs.get(ApplicationArgs.DATABASE_USERNAME).toString(), appArgs.get(ApplicationArgs.DATABASE_PASSWORD).toString());
        } catch (SQLException e) {
            MINIExceptionProcessor.getInstance().putException("build db connection error", e);
            return null;
        }
    }

    DatabaseConnectionPool(HashMap<String, Object> appArgs) {
        this.appArgs = appArgs;
    }


    void init() {
        for (int i = 0; i < minCount; i++) {
            pools.add(buildConnection());
        }
    }

    @Override
    public Connection getConnection() {
        Connection conn;
        if (pools.size() == 0) {
            conn = buildConnection();
        } else {
            conn = pools.remove(0);
        }
        return conn;
    }

    @Override
    public void closeConnection(Connection conn) {
        if (pools.size() < maxCount) {
            pools.add(conn);
        } else {
            //  maxCount = (int) (maxCount * loadFactor);
            try {
                conn.close();
            } catch (SQLException e) {
                MINIExceptionProcessor.getInstance().putException("db connection close error", e);
            }
        }
    }


    void setMinCount(int minCount) {
        this.minCount = Math.max(minCount, 1);
    }

    void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setLoadFactor(float loadFactor) {
        if (loadFactor >= 0)
            this.loadFactor = loadFactor;
    }

    void clear() {
        pools.clear();
    }
}
