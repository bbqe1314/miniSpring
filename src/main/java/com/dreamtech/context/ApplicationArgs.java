package com.dreamtech.context;

/**
 * mini-spring所需的所有参数
 */
public class ApplicationArgs {

    // tomcat 服务器端口号
    public static final String SERVER_PORT = "server.port";
    // tomcat 超时时间
    public static final String TIME_OUT = "server.timeout";
    // tomcat 最大连接数
    public static final String MAX_THREADS = "server.maxthreads";
    // 数据库地址
    public static final String DATABASE_ADDRESS = "database.address";
    // 数据库名
    public static final String DATABASE_NAME = "database.name";
    // 数据库端口
    public static final String DATABASE_PORT = "database.port";
    // 数据库用户名
    public static final String DATABASE_USERNAME = "database.username";
    // 数据库密码
    public static final String DATABASE_PASSWORD = "database.password";
    // 数据库配置参数
    public static final String DATABASE_CONFIG = "database.config";

    public static final String DATABASE_CONNECTION_POOL_MAX_COUNT = "database.connpool.maxcount";
    public static final String DATABASE_CONNECTION_POOL_MIN_COUNT = "database.connpool.mincount";


    public static final String OSS_ACCESS_KEY_ID = "oss.accessKeyId";
    public static final String OSS_ACCESS_KEY_SECRET = "oss.accessKeySecret";
    public static final String OSS_BUCKET_NAME = "oss.bucketName";
    public static final String OSS_END_POINT = "oss.endPoint";

    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final int DEFAULT_TIME_OUT = 20;
    public static final int DEFAULT_MAX_THREADS = 200;
    public static final String DEFAULT_DATABASE_ADDRESS = "172.0.0.1";
    public static final String DEFAULT_DATABASE_NAME = "test";
    public static final int DEFAULT_DATABASE_PORT = 3306;
    public static final String DEFAULT_DATABASE_USERNAME = "root";
    public static final String DEFAULT_DATABASE_PASSWORD = "123456";
    public static final String DEFAULT_DATABASE_CONFIG = "characterEncoding=utf-8";
    public static final int DEFAULT_DATABASE_CONNECTION_POOL_MAX_COUNT = 5;
    public static final int DEFAULT_DATABASE_CONNECTION_POOL_MIN_COUNT = 1;

    public static final String DEFAULT_OSS_ACCESS_KEY_ID = "accessKeyId";
    public static final String DEFAULT_OSS_ACCESS_KEY_SECRET = "accessKeySecret";
    public static final String DEFAULT_OSS_BUCKET_NAME = "bucketName";
    public static final String DEFAULT_OSS_END_POINT = "endPoint";
}
