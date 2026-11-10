# DEPLOY.md — 部署文档

> 目标：从零完成部署并访问验证。
> 技术基线：JavaEE8（`javax.*`）· Tomcat 9 · JDK 8 或 11 · Maven 3.8+ · MySQL 8。

## 1. 环境前提

| 组件 | 版本要求 | 验证命令 |
|---|---|---|
| JDK | 8 或 11（本项目按 release 8 编译） | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| Tomcat | 9.0.x（**禁止 Tomcat 10+**，10+ 是 jakarta.* 包名，无法运行本项目） | `$CATALINA_HOME/bin/version.sh` |
| MySQL | 8.x | `mysql --version` |

## 2. 获取源码与构建

```bash
git clone https://github.com/linanwanttodo/lab-equipment-system.git
cd lab-equipment-system
mvn package                # 产物：target/demo.war
```

## 3. 数据库初始化（二选一）

**方式 A（推荐）：手工执行初始化脚本**

```bash
mysql -u root -p < sql/init.sql
```

脚本可重复执行（建库建表均 IF NOT EXISTS，种子数据防重复）。

**方式 B：自动初始化**

应用首次启动会通过 `DataSeedListener` 自动建表并写入演示数据（建库由 JDBC URL 的 `createDatabaseIfNotExist=true` 完成），无需手工执行 SQL。

## 4. 数据库连接配置

编辑 `src/main/resources/jdbc.properties`（修改后需重新 `mvn package`）：

```properties
jdbc.url=jdbc:mysql://localhost:3306/lab_equipment?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
jdbc.user=root
jdbc.password=123456
```

## 5. 部署与启动（非 IDE）

```bash
# 5.1 复制 war 到 Tomcat 9（部署 5 秒内自动完成，无需重启）
cp target/demo.war $CATALINA_HOME/webapps/

# 5.2 启动 Tomcat 9（Linux/macOS）
$CATALINA_HOME/bin/startup.sh

# 5.3 观察部署日志（应出现 demo.war 部署完成的行）
tail -f $CATALINA_HOME/logs/catalina.$(date +%Y-%m-%d).log
```

## 6. 验证访问

| 验证项 | 命令/URL | 预期结果 |
|---|---|---|
| 设备列表（公开） | http://localhost:8080/demo/equipment | 200，出现设备表格 |
| 未登录拦截 | http://localhost:8080/demo/loans/mine | 302 → /demo/login |
| 管理员登录 | admin / admin123 | 302 → /demo/equipment |
| 管理员总览 | http://localhost:8080/demo/stats | 200，统计卡片 |
| 逾期提醒 | http://localhost:8080/demo/loans?scope=overdue | 200，逾期种子记录（zhangsan/路由器） |

**命令行验证：**

```bash
curl -s -o /dev/null -w "%{http_code} -> %{redirect_url}\n" http://localhost:8080/demo/loans/mine
# 预期：302 -> http://localhost:8080/demo/login?back=...
```

演示账号：`admin/admin123`（管理员）、`student/123456`（学生）。

## 7. Docker 一键部署

```bash
docker compose up -d       # 同时启动 MySQL8 与 Tomcat9 应用，首次启动自动建库建表
# 浏览器打开 http://localhost:8080/demo
```

## 8. 常见问题（故障分支）

| 现象 | 原因 | 处置 |
|---|---|---|
| 启动即 `UnsupportedClassVersionError ... class file version 61.0` | 环境变量 `CATALINA_HOME` 指向了 Tomcat 11（jakarta.*，需 Java 17） | 显式指定：`CATALINA_HOME=/path/to/tomcat9 CATALINA_BASE=/path/to/tomcat9 bin/catalina.sh run` |
| 访问 404 | 上下文路径是 war 包名 `/demo`，不是 `/` | 访问 http://localhost:8080/demo ；或把 war 改名为 `ROOT.war` |
| 端口冲突 `Address already in use: 8080` | 8080 被占用 | 改 `$CATALINA_HOME/conf/server.xml` 的 `port="8080"`，或释放端口 |
| 启动抛 `数据库初始化失败` | MySQL 未启动 / jdbc.properties 密码不对 / 驱动未联网下载 | 检查 MySQL 服务与第 4 节配置；确认 war 内含 mysql-connector-java |
| 页面中文乱码 | 编码过滤器未生效 / JDBC URL 缺 `characterEncoding=utf8` | 核对 web.xml 过滤器链与 jdbc.properties |
| war 放入 webapps 后访问仍 404 | war 未解压成功（权限/磁盘满） | 查看 `logs/catalina.*.log`；确认 `webapps/demo/` 目录已生成 |
| 登录报「用户名或密码错误」但账号正确 | 数据库里的哈希前缀与 jBCrypt 不兼容 | 重新执行 `sql/init.sql`（已统一为 `$2a$` 前缀） |

## 9. 部署工件清单

| 工件 | 仓库路径 |
|---|---|
| Dockerfile | `Dockerfile` |
| docker-compose.yml | `docker-compose.yml` |
| 数据库初始化脚本 | `sql/init.sql` |
| 数据库连接配置 | `src/main/resources/jdbc.properties` |
| war 构建产物 | `mvn package` → `target/demo.war`（不入库，构建获得） |
