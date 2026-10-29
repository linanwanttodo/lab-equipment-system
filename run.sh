#!/usr/bin/env bash
# =============================================================
# 一键运行脚本（Linux / macOS）
# 用法：./run.sh
# 流程：检查前端产物（缺失则自动构建）→ 启动后端 → 浏览器访问 http://localhost:8080
# 数据库：读取 src/main/resources/application.yml 中的连接配置（默认 MySQL，账号密码请按需修改）
# =============================================================
set -e
cd "$(dirname "$0")"

# 1. 环境检查
command -v java  >/dev/null 2>&1 || { echo "[run] 错误：未安装 Java 21"; exit 1; }
command -v mvn   >/dev/null 2>&1 || { echo "[run] 错误：未安装 Maven";   exit 1; }

# 2. 前端产物检查：无则自动构建（需要 Node.js）
if [ ! -f src/main/resources/static/index.html ]; then
  if command -v node >/dev/null 2>&1 && command -v npm >/dev/null 2>&1; then
    echo "[run] 未检测到前端构建产物，开始构建前端..."
    (cd frontend && (npm install --registry=https://registry.npmmirror.com) && npm run build:deploy)
    echo "[run] 前端构建完成"
  else
    echo "[run] 警告：未检测到前端产物且本机无 Node.js，本次将只提供后端接口"
  fi
else
  echo "[run] 前端构建产物已存在，跳过构建"
fi

# 3. 启动后端（首次启动会自动建表并初始化演示数据；也可先手动执行 sql/init.sql）
echo "[run] 启动后端：http://localhost:8080 （Ctrl+C 停止）"
mvn -q spring-boot:run
