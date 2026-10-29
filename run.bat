@echo off
rem =============================================================
rem 一键运行脚本（Windows）
rem 用法：双击运行或在命令行执行 run.bat
rem 流程：检查前端产物（缺失则自动构建）→ 启动后端 → 浏览器访问 http://localhost:8080
rem 数据库：读取 src/main/resources/application.yml 中的连接配置（默认 MySQL，账号密码请按需修改）
rem =============================================================
chcp 65001 >nul
cd /d %~dp0

where java >nul 2>nul
if errorlevel 1 (
    echo [run] 错误：未安装 Java 21
    pause
    exit /b 1
)
where mvn >nul 2>nul
if errorlevel 1 (
    echo [run] 错误：未安装 Maven
    pause
    exit /b 1
)

if not exist "src\main\resources\static\index.html" (
    where node >nul 2>nul
    if not errorlevel 1 (
        echo [run] 未检测到前端构建产物，开始构建前端...
        pushd frontend
        call npm install --registry=https://registry.npmmirror.com
        call npm run build:deploy
        popd
        echo [run] 前端构建完成
    ) else (
        echo [run] 警告：未检测到前端产物且本机无 Node.js，本次将只提供后端接口
    )
) else (
    echo [run] 前端构建产物已存在，跳过构建
)

echo [run] 启动后端：http://localhost:8080 （Ctrl+C 停止）
call mvn -q spring-boot:run
pause
