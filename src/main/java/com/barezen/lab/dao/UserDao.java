package com.barezen.lab.dao;

import com.barezen.lab.entity.User;
import com.barezen.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 用户 DAO：JDBC PreparedStatement 访问 users 表 */
public class UserDao {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setString(1, username);
            rs = st.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, id);
            rs = st.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询用户列表失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public void save(User u) {
        String sql = "INSERT INTO users (username, password, name, role, contact) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setString(1, u.getUsername());
            st.setString(2, u.getPassword());
            st.setString(3, u.getName());
            st.setString(4, u.getRole());
            st.setString(5, u.getContact());
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("新增用户失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    private User map(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setName(rs.getString("name"));
        u.setRole(rs.getString("role"));
        u.setContact(rs.getString("contact"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
