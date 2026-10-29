# 5C 校园实验室设备借用系统

一个前后端分离的校园实验室设备借用管理平台，覆盖 **设备 / 借用 / 归还 / 逾期提醒** 四大核心功能，支持学生与管理员两种角色。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 21 · Spring Boot 3.5 · Spring Web / Data JPA (Hibernate 6) / Security / Mail / Validation |
| 认证 | JWT（HS256/384/512，按密钥长度自动选择）· BCrypt 密码加密 |
| 数据库 | MySQL 8（默认） / H2 内存库（本地免安装演示） |
| 定时 | Spring `@Scheduled` 逾期扫描 + `JavaMailSender` 邮件提醒 |
| 前端 | Vue 3 · TypeScript · Vite 5 · Pinia · Vue Router · Axios |
| 构建 | Maven（后端） · npm（前端） |

## 目录结构

```
demo/
├─ pom.xml                     # Spring Boot 后端
├─ src/main/java/com/barezen/
│  ├─ Application.java         # 启动类（@EnableScheduling）
│  ├─ config/                  # SecurityConfig、DataInitializer（种子数据）
│  ├─ entity/                  # User / Lab / Equipment / Loan + 枚举
│  ├─ repository/              # Spring Data JPA
│  ├─ service/                 # Auth / Equipment / Loan / Lab 业务逻辑
│  ├─ controller/              # REST 接口
│  ├─ security/                # JWT 服务、过滤器、UserDetailsService
│  ├─ scheduler/               # 逾期定时扫描
│  ├─ mail/                    # 邮件提醒
│  ├─ dto/  exception/         # 请求/响应对象、统一异常
│  └─ resources/application.yml
└─ frontend/                   # Vite + Vue3 + TS 前端
   ├─ src/api  stores  router  views
   └─ package.json
```

## 快速开始

### 方式零：一键运行（最简单）

```bash
./run.sh          # Linux / macOS
run.bat           # Windows（双击或在命令行执行）
```

脚本会自动检查前端构建产物（缺失则自动构建），然后启动后端，浏览器打开 **http://localhost:8080** 即可使用。

### 数据库初始化

应用**首次启动会自动建库建表并写入演示数据**（JPA 自动建表 + 内置种子数据），无需手动执行 SQL。

如需手工初始化（或重置数据库），执行：

```bash
mysql -u root -p < sql/init.sql
```

`sql/init.sql` 可重复执行（建库建表均为 IF NOT EXISTS，种子数据防重复），包含：

- 建库 `lab_equipment`（utf8mb4）
- 四张表：`users` / `labs` / `equipments` / `loans`（含外键约束）
- 演示数据：6 个账号（1 管理员 + 5 学生，学生密码统一 `123456`）、4 个实验室、17 台设备、5 条借用记录样例（借用中 / 已归还 / 即将逾期各若干，其中一条已超期，启动后由定时任务自动标记逾期并触发提醒）

应用首次启动时通过内置初始化器写入**同一套演示数据**（与 `sql/init.sql` 一致），两种方式任选其一即可。

数据库连接配置在 `src/main/resources/application.yml`，按本机环境修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lab_equipment?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: "123456"   # 改成本机 MySQL 密码
```

> 免装 MySQL 快速演示可改用 H2：启动参数加 `--spring.datasource.url="jdbc:h2:mem:demo;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE" --spring.datasource.username=sa --spring.datasource.password= --spring.datasource.driver-class-name=org.h2.Driver`

### 方式一：单网址启动

前端页面已打包集成进 Spring Boot，一条命令即可获得完整系统（客户端页面与后台接口同一个地址）：

```bash
mvn spring-boot:run
```

浏览器打开 **http://localhost:8080** 即可使用。

> - 修改前端后重新集成：`cd frontend && npm run build:deploy`（构建并同步到后端静态资源），然后重启后端。

### 方式二：前后端分离开发（前端热更新）

```bash
# 终端 1：后端
mvn spring-boot:run

# 终端 2：前端开发服务器（页面热更新，/api 自动代理到 8080）
cd frontend
npm install
npm run dev        # http://localhost:5173
```

### 演示账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `admin123` |
| 学生 | `student` | `123456` |

## 功能清单

- **认证**：注册（默认学生）、登录（JWT）、会话鉴权、角色（学生 / 管理员）
- **设备**：公开列表（按实验室 / 类别 / 状态 / 关键字搜索）、详情；管理员增删改、库存维护
- **借用**：校验可用与库存 → 扣减 → 生成借用记录
- **归还**：本人或管理员可归还 → 恢复库存；超期自动计入逾期天数
- **逾期提醒**：后台每 60 秒扫描，把「借用中且已过应还」的记录标记为逾期，并发送邮件；管理员可在「逾期提醒」页查看实时逾期列表
- **后台管理**：总览仪表盘（统计卡片、类别/实验室分布、最近借用、逾期 TOP）、实验室管理、用户管理

## 接口一览

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/api/auth/register` | 公开 | 注册 |
| POST | `/api/auth/login` | 公开 | 登录，返回 token |
| GET  | `/api/auth/me` | 登录 | 当前用户 |
| GET  | `/api/equipment` | 公开 | 设备列表（可选筛选） |
| GET  | `/api/equipment/{id}` | 公开 | 设备详情 |
| POST | `/api/equipment` | 管理员 | 新增设备 |
| PUT  | `/api/equipment/{id}` | 管理员 | 修改设备 |
| DELETE | `/api/equipment/{id}` | 管理员 | 删除设备 |
| GET  | `/api/labs` | 公开 | 实验室列表 |
| POST | `/api/labs` | 管理员 | 新增实验室 |
| PUT  | `/api/labs/{id}` | 管理员 | 修改实验室 |
| DELETE | `/api/labs/{id}` | 管理员 | 删除实验室（有设备时拦截） |
| GET  | `/api/equipment/categories` | 公开 | 设备类别去重列表 |
| GET  | `/api/stats/dashboard` | 管理员 | 后台总览统计 |
| GET  | `/api/users` | 管理员 | 用户列表 |
| POST | `/api/loans` | 登录 | 申请借用 |
| POST | `/api/loans/{id}/return` | 本人/管理员 | 归还 |
| GET  | `/api/loans/mine` | 登录 | 我的借用 |
| GET  | `/api/loans` | 管理员 | 全部借用 |
| GET  | `/api/loans/overdue` | 管理员 | 逾期列表 |

## 开启逾期邮件提醒

默认 `app.mail.enabled: false`（不配置 SMTP 也不报错）。需要真实发邮件时，在 `application.yml` 填入 SMTP 信息并开启：

```yaml
spring:
  mail:
    host: smtp.qq.com        # 或你的 SMTP 服务器
    port: 587
    username: you@qq.com
    password: 你的SMTP授权码
app:
  mail:
    enabled: true
    from: you@qq.com
```

并在注册时填写邮箱（`contact`），即可在逾期时收到邮件。

## 打包

```bash
# 1) 构建前端并同步到后端静态资源
cd frontend && npm run build:deploy && cd ..

# 2) 打包（前端已内嵌，jar 独立运行）
mvn package -DskipTests        # → target/demo-1.0-SNAPSHOT.jar
java -jar target/demo-1.0-SNAPSHOT.jar
```
