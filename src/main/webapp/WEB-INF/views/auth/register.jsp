<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="注册"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>注册 - 5C 校园实验室设备借用系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>

<div class="auth-page">

    <div class="auth-brand">
        <div class="logo-row">
            <div class="logo-mark" style="width:38px;height:38px"><t:uicon name="flask" size="20"/></div>
            <span class="logo-text" style="font-size:18px">实验室设备借用系统</span>
        </div>
        <h1>创建你的账号</h1>
        <p class="tagline">注册后即可在线浏览与借用实验室设备</p>

        <div class="feature">
            <div class="f-ico"><t:uicon name="user" size="17"/></div>
            <div>
                <b>学生角色</b>
                <span>注册用户默认为学生，可自助借用与归还设备</span>
            </div>
        </div>
        <div class="feature">
            <div class="f-ico"><t:uicon name="calendar" size="17"/></div>
            <div>
                <b>借用记录</b>
                <span>借出、应还、归还全程留痕，随时查看进度</span>
            </div>
        </div>

        <div class="auth-foot">5C 校园实验室设备借用系统</div>
    </div>

    <div class="auth-panel">
        <div class="auth-card">
            <h1>注册</h1>
            <p class="subtitle">注册后默认为学生角色</p>
            <form method="post" action="${pageContext.request.contextPath}/register">
                <label>用户名</label>
                <input type="text" name="username" placeholder="3-50 位字符" required/>
                <label>姓名</label>
                <input type="text" name="name" placeholder="真实姓名" required/>
                <label>密码</label>
                <input type="password" name="password" placeholder="至少 6 位" required minlength="6"/>
                <label>确认密码</label>
                <input type="password" name="password2" placeholder="再次输入密码" required minlength="6"/>
                <label>邮箱（用于逾期提醒，选填）</label>
                <input type="text" name="contact" placeholder="you@example.com"/>
                <c:if test="${not empty requestScope.error}">
                    <p class="error">${requestScope.error}</p>
                </c:if>
                <button class="btn btn-primary" type="submit">注 册</button>
            </form>
            <p class="switch">已有账号？<a href="${pageContext.request.contextPath}/login">直接登录</a></p>
        </div>
    </div>

</div>
</body>
</html>
