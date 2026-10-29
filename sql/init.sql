-- =============================================================
-- 5C 校园实验室设备借用系统 —— 数据库初始化脚本
-- 适用：MySQL 8.x
-- 说明：
--   1. 脚本可重复执行（建库建表均为 IF NOT EXISTS，种子数据防重复）
--   2. 应用启动时也会通过 JPA 自动建表并初始化同样的演示数据，
--      本脚本用于需要手工初始化数据库的场景
--   3. 演示账号（密码均为 BCrypt 密文存储）：
--      admin   / admin123   （管理员）
--      student / 123456     （学生）
--      liwei / zhangmin / chenjie / wangfang / 123456（学生）
-- =============================================================

CREATE DATABASE IF NOT EXISTS lab_equipment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab_equipment;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密',
    name       VARCHAR(50)  NOT NULL,
    contact    VARCHAR(100) NULL,
    role       VARCHAR(20)  NOT NULL COMMENT 'STUDENT / ADMIN',
    created_at DATETIME     NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- ----------------------------
-- 实验室表
-- ----------------------------
CREATE TABLE IF NOT EXISTS labs (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    location   VARCHAR(200) NULL,
    manager_id BIGINT       NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='实验室表';

-- ----------------------------
-- 设备表
-- ----------------------------
CREATE TABLE IF NOT EXISTS equipments (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    code          VARCHAR(50)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    category      VARCHAR(50)  NULL,
    lab_id        BIGINT       NULL,
    total_qty     INT          NOT NULL,
    available_qty INT          NOT NULL,
    deposit       DOUBLE       NULL COMMENT '押金',
    status        VARCHAR(20)  NOT NULL COMMENT 'AVAILABLE / MAINTENANCE',
    description   VARCHAR(500) NULL,
    created_at    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_equipments_code (code),
    CONSTRAINT fk_equipments_lab FOREIGN KEY (lab_id) REFERENCES labs (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='设备表';

-- ----------------------------
-- 借用记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS loans (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    equipment_id  BIGINT       NOT NULL,
    purpose       VARCHAR(200) NULL COMMENT '借用用途',
    borrow_time   DATETIME     NOT NULL COMMENT '借出时间',
    due_time      DATETIME     NOT NULL COMMENT '应还时间',
    return_time   DATETIME     NULL COMMENT '实际归还时间',
    status        VARCHAR(20)  NOT NULL COMMENT 'BORROWING / RETURNED / OVERDUE',
    overdue_days  INT          NULL DEFAULT 0 COMMENT '逾期天数',
    remark        VARCHAR(200) NULL,
    created_at    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_loans_equipment FOREIGN KEY (equipment_id) REFERENCES equipments (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='借用记录表';

-- ----------------------------
-- 种子数据：用户（密码为 BCrypt 密文；学生密码统一为 123456）
-- ----------------------------
INSERT IGNORE INTO users (username, password, name, contact, role)
VALUES ('admin',    '$2a$10$ieSsBYxe8WuWtZlyferRm.MKHCiozfO4FwLLP3MnzK9hOIHozKGVG', '系统管理员', 'admin@lab.edu',     'ADMIN'),
       ('student',  '$2a$10$cmI886TlFIVMz8A0syCHwuIJsEyRyCFlBkOudIgESvTL5e3Z3Xn8e', '演示学生',   'student@lab.edu',   'STUDENT'),
       ('liwei',    '$2a$10$cmI886TlFIVMz8A0syCHwuIJsEyRyCFlBkOudIgESvTL5e3Z3Xn8e', '李伟',       'liwei@lab.edu',     'STUDENT'),
       ('zhangmin', '$2a$10$cmI886TlFIVMz8A0syCHwuIJsEyRyCFlBkOudIgESvTL5e3Z3Xn8e', '张敏',       'zhangmin@lab.edu',  'STUDENT'),
       ('chenjie',  '$2a$10$cmI886TlFIVMz8A0syCHwuIJsEyRyCFlBkOudIgESvTL5e3Z3Xn8e', '陈杰',       'chenjie@lab.edu',   'STUDENT'),
       ('wangfang', '$2a$10$cmI886TlFIVMz8A0syCHwuIJsEyRyCFlBkOudIgESvTL5e3Z3Xn8e', '王芳',       'wangfang@lab.edu',  'STUDENT');

-- ----------------------------
-- 种子数据：实验室
-- ----------------------------
INSERT INTO labs (name, location)
SELECT '物理实验室', 'A 栋 301' WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name = '物理实验室');
INSERT INTO labs (name, location)
SELECT '化学实验室', 'A 栋 402' WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name = '化学实验室');
INSERT INTO labs (name, location)
SELECT '生物实验室', 'B 栋 205' WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name = '生物实验室');
INSERT INTO labs (name, location)
SELECT '计算机实验室', 'C 栋 501' WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name = '计算机实验室');

-- ----------------------------
-- 种子数据：设备（17 台，覆盖 4 个实验室、4 个类别）
-- ----------------------------
INSERT IGNORE INTO equipments (code, name, category, lab_id, total_qty, available_qty, deposit, status, description)
VALUES
-- 物理实验室
('PHY-001', '示波器',       '仪器', (SELECT id FROM labs WHERE name = '物理实验室'), 10, 9,  0,  'AVAILABLE',   '用于信号观测'),
('PHY-002', '激光笔',       '仪器', (SELECT id FROM labs WHERE name = '物理实验室'), 20, 20, 20, 'AVAILABLE',   '教学演示用'),
('PHY-003', '高倍显微镜',   '仪器', (SELECT id FROM labs WHERE name = '物理实验室'), 5,  4,  50, 'AVAILABLE',   '光学显微镜'),
('PHY-004', '万用表',       '仪器', (SELECT id FROM labs WHERE name = '物理实验室'), 15, 15, 30, 'AVAILABLE',   '数字万用表'),
('PHY-005', '信号发生器',   '仪器', (SELECT id FROM labs WHERE name = '物理实验室'), 6,  6,  60, 'AVAILABLE',   '函数信号发生器'),
-- 化学实验室
('CHE-001', '酒精灯',       '耗材', (SELECT id FROM labs WHERE name = '化学实验室'), 50, 50, 5,  'AVAILABLE',   '实验室常用'),
('CHE-002', '量筒(100ml)',  '耗材', (SELECT id FROM labs WHERE name = '化学实验室'), 30, 30, 0,  'AVAILABLE',   '玻璃量筒'),
('CHE-003', '电子天平',     '仪器', (SELECT id FROM labs WHERE name = '化学实验室'), 8,  8,  40, 'AVAILABLE',   '精度 0.01g'),
('CHE-004', '烧杯套装',     '耗材', (SELECT id FROM labs WHERE name = '化学实验室'), 60, 60, 0,  'AVAILABLE',   '多种规格组合'),
('CHE-005', 'pH计',         '仪器', (SELECT id FROM labs WHERE name = '化学实验室'), 10, 10, 45, 'AVAILABLE',   '台式酸度计'),
-- 生物实验室
('BIO-001', '生物显微镜',   '仪器', (SELECT id FROM labs WHERE name = '生物实验室'), 8,  8,  80, 'AVAILABLE',   '1600 倍光学放大'),
('BIO-002', '离心机',       '仪器', (SELECT id FROM labs WHERE name = '生物实验室'), 4,  4,  100,'AVAILABLE',   '台式高速离心机'),
('BIO-003', '移液器',       '耗材', (SELECT id FROM labs WHERE name = '生物实验室'), 40, 40, 0,  'AVAILABLE',   '微量移液器套装'),
-- 计算机实验室
('CS-001',  '笔记本电脑',   '设备', (SELECT id FROM labs WHERE name = '计算机实验室'), 15, 14, 500,'AVAILABLE',  '编程实训用'),
('CS-002',  '投影仪',       '设备', (SELECT id FROM labs WHERE name = '计算机实验室'), 6,  6,  200,'AVAILABLE',  '会议教学投屏'),
('CS-003',  '树莓派开发套件','设备', (SELECT id FROM labs WHERE name = '计算机实验室'), 10, 10, 150,'AVAILABLE',  '含配件与电源'),
('CS-004',  '3D打印机',     '设备', (SELECT id FROM labs WHERE name = '计算机实验室'), 2,  1,  300,'AVAILABLE',  'FDM 桌面级');

-- ----------------------------
-- 种子数据：借用记录（借用中 / 已归还 / 逾期样例各若干）
-- 时间基于执行时刻动态计算，导入后与当前时间保持相对关系
-- ----------------------------
-- 李伟 借 示波器（借用中，5 天后到期）
INSERT INTO loans (user_id, equipment_id, purpose, borrow_time, due_time, return_time, status, overdue_days)
SELECT u.id, e.id, '电子电路实验', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), NULL, 'BORROWING', 0
FROM users u, equipments e
WHERE u.username = 'liwei' AND e.code = 'PHY-001'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id = u.id AND l.equipment_id = e.id AND l.status = 'BORROWING');

-- 张敏 借 笔记本电脑（借用中，13 天后到期）
INSERT INTO loans (user_id, equipment_id, purpose, borrow_time, due_time, return_time, status, overdue_days)
SELECT u.id, e.id, '程序设计课程作业', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 13 DAY), NULL, 'BORROWING', 0
FROM users u, equipments e
WHERE u.username = 'zhangmin' AND e.code = 'CS-001'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id = u.id AND l.equipment_id = e.id AND l.status = 'BORROWING');

-- 陈杰 借 高倍显微镜（已超期 3 天未还 —— 应用启动后由定时任务自动标记为逾期并发提醒）
INSERT INTO loans (user_id, equipment_id, purpose, borrow_time, due_time, return_time, status, overdue_days)
SELECT u.id, e.id, '光学实验观察', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, 'BORROWING', 0
FROM users u, equipments e
WHERE u.username = 'chenjie' AND e.code = 'PHY-003'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id = u.id AND l.equipment_id = e.id AND l.status = 'BORROWING');

-- 王芳 借 酒精灯（按时归还样例）
INSERT INTO loans (user_id, equipment_id, purpose, borrow_time, due_time, return_time, status, overdue_days)
SELECT u.id, e.id, '化学演示实验', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 'RETURNED', 0
FROM users u, equipments e
WHERE u.username = 'wangfang' AND e.code = 'CHE-001'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id = u.id AND l.equipment_id = e.id AND l.status = 'RETURNED');

-- 演示学生 借 3D打印机（借用中，7 天后到期）
INSERT INTO loans (user_id, equipment_id, purpose, borrow_time, due_time, return_time, status, overdue_days)
SELECT u.id, e.id, '毕业设计模型打印', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NULL, 'BORROWING', 0
FROM users u, equipments e
WHERE u.username = 'student' AND e.code = 'CS-004'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id = u.id AND l.equipment_id = e.id AND l.status = 'BORROWING');

-- ----------------------------
-- 验证
-- ----------------------------
SELECT 'users'      AS tbl, COUNT(*) AS cnt FROM users
UNION ALL SELECT 'labs',       COUNT(*) FROM labs
UNION ALL SELECT 'equipments', COUNT(*) FROM equipments
UNION ALL SELECT 'loans',      COUNT(*) FROM loans;
