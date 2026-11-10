-- ============================================================
-- 5C 校园实验室设备借用系统 数据库初始化脚本（可重复执行）
-- 技术基线：MySQL8 · JavaEE8 · Tomcat9 · JDK8
-- ============================================================

CREATE DATABASE IF NOT EXISTS lab_equipment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE lab_equipment;

-- 用户表：学生(STUDENT) / 管理员(ADMIN)，密码为 BCrypt 哈希
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 哈希',
    name        VARCHAR(50)  NULL COMMENT '姓名（显示名）',
    role        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT '角色 ADMIN/STUDENT',
    contact     VARCHAR(100) NULL COMMENT '联系方式（邮箱，逾期提醒用）',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- 实验室表
CREATE TABLE IF NOT EXISTS labs (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100) NOT NULL COMMENT '实验室名称',
    location    VARCHAR(100) NULL COMMENT '位置',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室';

-- 设备表：status AVAILABLE 可借用 / MAINTENANCE 维护中
CREATE TABLE IF NOT EXISTS equipments (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    code        VARCHAR(30)   NOT NULL COMMENT '设备编号',
    lab_id      BIGINT        NULL COMMENT '所属实验室',
    name        VARCHAR(100)  NOT NULL COMMENT '设备名称',
    category    VARCHAR(50)   NOT NULL COMMENT '类别',
    status      VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态',
    total       INT           NOT NULL DEFAULT 1 COMMENT '总数',
    available   INT           NOT NULL DEFAULT 1 COMMENT '可借数量',
    deposit     DECIMAL(10,2) NULL COMMENT '押金（元）',
    description VARCHAR(255)  NULL COMMENT '说明',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    CONSTRAINT fk_equipment_lab FOREIGN KEY (lab_id) REFERENCES labs (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备';

-- 借用记录表：status ACTIVE 借用中 / RETURNED 已归还（逾期由应还时间动态判定）
CREATE TABLE IF NOT EXISTS loans (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id       BIGINT        NOT NULL COMMENT '借用人',
    equipment_id  BIGINT        NOT NULL COMMENT '设备',
    borrow_time   DATETIME      NOT NULL COMMENT '借出时间',
    due_time      DATETIME      NOT NULL COMMENT '应还时间',
    return_time   DATETIME      NULL COMMENT '归还时间',
    purpose       VARCHAR(100)  NULL COMMENT '借用用途',
    status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_loan_equipment FOREIGN KEY (equipment_id) REFERENCES equipments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借用记录';

-- ============================================================
-- 种子数据（INSERT ... SELECT 防重复：同名/同用户名已存在则跳过）
-- ============================================================

-- 账号：admin/admin123，学生统一 123456（BCrypt 哈希，$2a$ 前缀与 jBCrypt 兼容）
INSERT INTO users (username, password, name, role, contact)
SELECT 'admin' AS username, '$2a$10$jQyxKSptR7pmJ1v7wY/vUOjXKANGGmXpbwP6qfIZxEvGutZl2uJAS' AS password, '管理员' AS name, 'ADMIN' AS role, 'admin@5c.edu.cn' AS contact FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

INSERT INTO users (username, password, name, role, contact)
SELECT 'student' AS username, '$2a$10$Xy1AtG5bLF6/neJxSB5bjuF21nkSTQPnA8EjWWMJbEV68y15s1AHK' AS password, '演示学生' AS name, 'STUDENT' AS role, 'student@5c.edu.cn' AS contact FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='student');

INSERT INTO users (username, password, name, role, contact)
SELECT 'zhangsan' AS username, '$2a$10$Xy1AtG5bLF6/neJxSB5bjuF21nkSTQPnA8EjWWMJbEV68y15s1AHK' AS password, '张三' AS name, 'STUDENT' AS role, 'zhangsan@5c.edu.cn' AS contact FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='zhangsan');

-- 实验室 4 个
INSERT INTO labs (name, location)
SELECT '电工电子实验室' AS name, '实验楼 A-201' AS location FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name='电工电子实验室');

INSERT INTO labs (name, location)
SELECT '计算机网络实验室', '实验楼 B-305' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name='计算机网络实验室');

INSERT INTO labs (name, location)
SELECT '嵌入式开发实验室', '实验楼 C-402' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name='嵌入式开发实验室');

INSERT INTO labs (name, location)
SELECT '软件工程实验室', '实验楼 D-108' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM labs WHERE name='软件工程实验室');

-- 设备 12 台（编号 EQ-001..EQ-012，含押金；用派生表引用实验室名）
INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-001' col1, '电工电子实验室' col9, '数字示波器 DS1102Z-E' col2, '测量仪器' col3, 'AVAILABLE' col4, 5 col5, 5 col6, 200.00 col7, '100MHz 双通道数字示波器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-001');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-002' col1, '电工电子实验室' col9, '函数信号发生器 FY2300' col2, '测量仪器' col3, 'AVAILABLE' col4, 3 col5, 3 col6, 150.00 col7, '30MHz 函数信号发生器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-002');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-003' col1, '电工电子实验室' col9, '直流稳压电源 DP832' col2, '测量仪器' col3, 'AVAILABLE' col4, 4 col5, 4 col6, 100.00 col7, '三路可编程直流稳压电源' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-003');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-004' col1, '计算机网络实验室' col9, '路由器 RSR20-X' col2, '网络设备' col3, 'AVAILABLE' col4, 6 col5, 6 col6, 250.00 col7, '企业级模块化路由器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-004');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-005' col1, '计算机网络实验室' col9, '三层交换机 S5750' col2, '网络设备' col3, 'AVAILABLE' col4, 4 col5, 4 col6, 300.00 col7, '千兆三层交换机' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-005');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-006' col1, '计算机网络实验室' col9, '网络抓包器 WiresharkPro' col2, '网络设备' col3, 'AVAILABLE' col4, 2 col5, 2 col6, 400.00 col7, '硬件抓包器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-006');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-007' col1, '嵌入式开发实验室' col9, 'STM32F407 开发板' col2, '开发板' col3, 'AVAILABLE' col4, 8 col5, 8 col6, 80.00 col7, 'STM32F407ZGT6 核心板' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-007');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-008' col1, '嵌入式开发实验室' col9, 'J-Link 仿真器 V11' col2, '开发工具' col3, 'AVAILABLE' col4, 5 col5, 5 col6, 200.00 col7, 'SWD/JTAG 调试器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-008');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-009' col1, '嵌入式开发实验室' col9, '树莓派 4B 套件' col2, '开发板' col3, 'AVAILABLE' col4, 3 col5, 3 col6, 150.00 col7, '4GB 内存版含电源与 SD 卡' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-009');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-010' col1, '软件工程实验室' col9, '惠普 Z2 工作站' col2, '计算机' col3, 'AVAILABLE' col4, 4 col5, 4 col6, 2000.00 col7, 'i7-12700/32G/1TSSD' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-010');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-011' col1, '软件工程实验室' col9, '戴尔 U2723 显示器' col2, '计算机' col3, 'AVAILABLE' col4, 6 col5, 6 col6, 500.00 col7, '27 寸 4K 显示器' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-011');

INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description)
SELECT s.col1, l.id, s.col2, s.col3, s.col4, s.col5, s.col6, s.col7, s.col8 FROM
(SELECT 'EQ-012' col1, '软件工程实验室' col9, 'LogicPro 逻辑分析仪' col2, '测量仪器' col3, 'MAINTENANCE' col4, 1 col5, 0 col6, 300.00 col7, '送修中' col8) s
JOIN labs l ON l.name = s.col9
WHERE NOT EXISTS (SELECT 1 FROM equipments WHERE code='EQ-012');

-- 借用记录样例：借用中 / 已归还 / 逾期各若干（含借用用途）
INSERT INTO loans (user_id, equipment_id, borrow_time, due_time, return_time, purpose, status)
SELECT u.id, e.id,
       DATE_SUB(NOW(), INTERVAL 2 DAY),
       DATE_ADD(NOW(), INTERVAL 5 DAY),
       NULL, '课程实验', 'ACTIVE'
FROM users u, equipments e
WHERE u.username='student' AND e.code='EQ-007'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id=u.id AND l.equipment_id=e.id AND l.return_time IS NULL);

INSERT INTO loans (user_id, equipment_id, borrow_time, due_time, return_time, purpose, status)
SELECT u.id, e.id,
       DATE_SUB(NOW(), INTERVAL 10 DAY),
       DATE_ADD(NOW(), INTERVAL 20 DAY),
       NULL, '毕业设计', 'ACTIVE'
FROM users u, equipments e
WHERE u.username='zhangsan' AND e.code='EQ-001'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id=u.id AND l.equipment_id=e.id AND l.return_time IS NULL);

INSERT INTO loans (user_id, equipment_id, borrow_time, due_time, return_time, purpose, status)
SELECT u.id, e.id,
       DATE_SUB(NOW(), INTERVAL 20 DAY),
       DATE_SUB(NOW(), INTERVAL 6 DAY),
       DATE_SUB(NOW(), INTERVAL 5 DAY), '课程实验', 'RETURNED'
FROM users u, equipments e
WHERE u.username='student' AND e.code='EQ-009'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id=u.id AND l.equipment_id=e.id AND l.return_time IS NOT NULL);

INSERT INTO loans (user_id, equipment_id, borrow_time, due_time, return_time, purpose, status)
SELECT u.id, e.id,
       DATE_SUB(NOW(), INTERVAL 30 DAY),
       DATE_SUB(NOW(), INTERVAL 16 DAY),
       NULL, '竞赛调试', 'ACTIVE'
FROM users u, equipments e
WHERE u.username='zhangsan' AND e.code='EQ-004'
  AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.user_id=u.id AND l.equipment_id=e.id AND l.return_time IS NULL);
