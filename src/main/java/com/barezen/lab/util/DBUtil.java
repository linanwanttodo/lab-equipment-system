package com.barezen.lab.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 公共数据库工具类：统一获取连接与释放资源（实验三分层重构产物）。
 * 连接参数来自 classpath 下的 jdbc.properties。
 */
public class DBUtil {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            Properties p = new Properties();
            p.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
            URL = p.getProperty("jdbc.url");
            USER = p.getProperty("jdbc.user");
            PASSWORD = p.getProperty("jdbc.password");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** 统一释放资源：ResultSet → Statement → Connection，逆序关闭 */
    public static void close(ResultSet rs, Statement st, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) { }
        try { if (st != null) st.close(); } catch (SQLException ignored) { }
        try { if (conn != null) conn.close(); } catch (SQLException ignored) { }
    }
}
