# 5C 校园实验室设备借用系统

一个基于 **JavaEE8 (javax.*) + Tomcat 9 + JSP/JSTL + JDBC + MySQL 8** 的校园实验室设备借用管理平台，覆盖 **设备 / 借用 / 归还 / 逾期提醒** 四大核心功能，支持学生与管理员两种角色。

> 技术基线纪律：本项目固定使用 JavaEE8（`javax.servlet.*` / `javax.servlet.jsp.*`）、Tomcat 9、JDK 8 或 11、Maven 3.8+、MySQL 8。**不引入** JakartaEE 9+、Tomcat 10+，仓库中不出现 `jakarta.*` 包名。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | JavaEE8 · `javax.servlet-api` 4.0.1（provided）· `javax.servlet.jsp-api` 2.3.3 |
| 视图 | JSP + JSTL 1.2（`c:forEach` / `c:if` / `fmt:formatDate`） |
| 持久层 | 原生 JDBC `PreparedStatement`（无框架） |
| 安全 | BCrypt 密码哈希（jBCrypt 0.4）· `HttpSession` 会话 · `Filter` 编码与登录检查 |
| 数据库 | MySQL 8（utf8mb4） |
| 构建 | Maven（war 打包，`maven.compiler.release` 8） |

## 目录结构

```
demo/
├─ pom.xml                       # war 打包，JavaEE8 基线
├─ sql/init.sql                  # 数据库初始化（可重复执行，含演示数据）
├─ src/main/java/com/barezen/lab/
│  ├─ util/DBUtil.java           # 公共数据库工具类（连接获取/资源释放）
│  ├─ entity/                    # User / Lab / Equipment / Loan
│  ├─ dao/                       # JDBC DAO（PreparedStatement）
│  ├─ service/                   # 业务校验（登录/设备/借用/实验室/统计）
│  ├─ web/filter/                # EncodingFilter / LoginFilter
│  ├─ web/servlet/               # 登录注册注销 / 设备 / 实验室 / 借用 / 用户 / 统计
│  ├─ config/DataSeedListener.java  # 启动初始化器（自动建表+种子数据）
│  └─ exception/ServiceException.java
├─ src/main/webapp/
│  ├─ WEB-INF/web.xml            # 过滤器链、welcome-file、错误页
│  ├─ WEB-INF/views/             # JSP+JSTL 视图（auth/equipment/lab/loan/user/stats/error）
│  └─ css/style.css
├─ DEPLOY.md                     # 部署文档（从零复现）
├─ Dockerfile                    # Docker 容器部署
└─ docker-compose.yml            # MySQL8 + Tomcat9 一键启动
```

## 核心功能

- **认证**：注册（默认学生）、登录（BCrypt 校验 + 会话固定防护 `changeSessionId`）、注销（`session.invalidate()`）
- **设备**：公开列表（按实验室 / 类别 / 状态 / 关键字搜索）、详情；管理员增删改、库存维护
- **借用**：校验可用与库存 → 扣减库存 → 生成借用记录（默认 7 天期限）
- **归还**：本人或管理员可归还 → 恢复库存；超期按应还时间动态计入逾期天数
- **逾期提醒**：「借用中且已过应还」由逾期页实时动态判定，管理员可代归还
- **后台管理**：总览仪表盘（统计卡片、类别/实验室分布、最近借用、逾期 TOP）、实验室管理、用户管理

## 本地开发启动

```bash
# 1. 初始化数据库（可跳过：应用首次启动会自动建库建表并写入演示数据）
mysql -u root -p < sql/init.sql

# 2. 修改数据库连接：src/main/resources/jdbc.properties（默认 root/123456）

# 3. 构建 war
mvn package            # JDK 8 或 11 → target/demo.war

# 4. 部署到 Tomcat 9
cp target/demo.war $CATALINA_HOME/webapps/

# 5. 启动 Tomcat 9 并访问
$CATALINA_HOME/bin/startup.sh
# 浏览器打开 http://localhost:8080/demo
```

## 演示账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `admin123` |
| 学生 | `student` | `123456` |

## 部署

完整部署步骤（含 Docker、故障排查、非 IDE 部署）见 **[DEPLOY.md](DEPLOY.md)**。

## 配对部署验证

> 本小节记录搭档独立部署本项目的证据（实验六/七）。

- 本项目已由 **（搭档）** 在 **（环境：OS/JDK/Tomcat/MySQL）** 中独立部署成功。
