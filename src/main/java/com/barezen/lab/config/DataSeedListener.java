package com.barezen.lab.config;

import com.barezen.lab.dao.LabDao;
import com.barezen.lab.dao.UserDao;
import com.barezen.lab.entity.Lab;
import com.barezen.lab.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Statement;

/**
 * 启动初始化器：首次启动自动建表并写入演示数据（与 sql/init.sql 同一套）。
 * 数据库由 JDBC URL 的 createDatabaseIfNotExist=true 自动创建。
 */
@WebListener
public class DataSeedListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = com.barezen.lab.util.DBUtil.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, username VARCHAR(50) NOT NULL, "
                    + "password VARCHAR(100) NOT NULL, name VARCHAR(50) NULL, "
                    + "role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', contact VARCHAR(100) NULL, "
                    + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (id), UNIQUE KEY uk_username (username)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS labs ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(100) NOT NULL, "
                    + "location VARCHAR(100) NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS equipments ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, code VARCHAR(30) NOT NULL, lab_id BIGINT NULL, "
                    + "name VARCHAR(100) NOT NULL, category VARCHAR(50) NOT NULL, "
                    + "status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', "
                    + "total INT NOT NULL DEFAULT 1, available INT NOT NULL DEFAULT 1, "
                    + "deposit DECIMAL(10,2) NULL, description VARCHAR(255) NULL, "
                    + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (id), UNIQUE KEY uk_code (code), "
                    + "CONSTRAINT fk_equipment_lab FOREIGN KEY (lab_id) REFERENCES labs (id)) "
                    + "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS loans ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, equipment_id BIGINT NOT NULL, "
                    + "borrow_time DATETIME NOT NULL, due_time DATETIME NOT NULL, return_time DATETIME NULL, "
                    + "purpose VARCHAR(100) NULL, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', "
                    + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (id), CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES users (id), "
                    + "CONSTRAINT fk_loan_equipment FOREIGN KEY (equipment_id) REFERENCES equipments (id)) "
                    + "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            seedUsers();
            seedLabs(st);
        } catch (Exception e) {
            throw new IllegalStateException("数据库初始化失败，请检查 MySQL 连接配置（jdbc.properties）", e);
        }
    }

    /** 种子账号：admin/admin123，student/123456（BCrypt 哈希，运行期生成保证兼容） */
    private void seedUsers() throws Exception {
        UserDao dao = new UserDao();
        if (dao.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setName("管理员");
            admin.setRole("ADMIN");
            admin.setContact("admin@5c.edu.cn");
            dao.save(admin);
        }
        if (dao.findByUsername("student") == null) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
            student.setName("演示学生");
            student.setRole("STUDENT");
            student.setContact("student@5c.edu.cn");
            dao.save(student);
        }
    }

    /** 种子实验室（演示用，完整演示数据见 sql/init.sql） */
    private void seedLabs(Statement st) throws Exception {
        LabDao dao = new LabDao();
        if (dao.findAll().isEmpty()) {
            String[][] labs = {
                    {"电工电子实验室", "实验楼 A-201"},
                    {"计算机网络实验室", "实验楼 B-305"},
                    {"嵌入式开发实验室", "实验楼 C-402"},
                    {"软件工程实验室", "实验楼 D-108"}
            };
            for (String[] s : labs) {
                Lab lab = new Lab();
                lab.setName(s[0]);
                lab.setLocation(s[1]);
                dao.save(lab);
            }
        }
    }
}
