<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="登录"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>登录 - 5C 校园实验室设备借用系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>

<div class="auth-page">

    <div class="auth-brand">
        <div class="logo-row">
            <div class="logo-mark" style="width:38px;height:38px"><t:uicon name="flask" size="20"/></div>
            <span class="logo-text" style="font-size:18px">实验室设备借用系统</span>
        </div>
        <h1>校园实验室设备管理平台</h1>
        <p class="tagline">设备 · 借用 · 归还 · 逾期提醒，一站式管理</p>

        <div class="feature">
            <div class="f-ico"><t:uicon name="box" size="17"/></div>
            <div>
                <b>设备台账</b>
                <span>实验室、类别、库存一表管理，实时掌握可借数量</span>
            </div>
        </div>
        <div class="feature">
            <div class="f-ico"><t:uicon name="clipboard" size="17"/></div>
            <div>
                <b>借用归还</b>
                <span>学生在线申请借用、按时归还，库存自动扣减与恢复</span>
            </div>
        </div>
        <div class="feature">
            <div class="f-ico"><t:uicon name="alarm" size="17"/></div>
            <div>
                <b>逾期提醒</b>
                <span>逾期记录实时动态判定，管理员一站代归还</span>
            </div>
        </div>

        <div class="auth-foot">5C 校园实验室设备借用系统</div>
    </div>

    <div class="auth-panel">
        <div class="auth-card">
            <h1>登录</h1>
            <p class="subtitle">使用系统账号登录工作台</p>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <input type="hidden" name="back" value="${requestScope.back}"/>
                <label>用户名</label>
                <input type="text" name="username" placeholder="请输入用户名" required/>
                <label>密码</label>
                <input type="password" name="password" placeholder="请输入密码" required/>
                <c:if test="${not empty requestScope.error}">
                    <p class="error">${requestScope.error}</p>
                </c:if>
                <button class="btn btn-primary" type="submit">登 录</button>
            </form>
            <p class="switch">还没有账号？<a href="${pageContext.request.contextPath}/register">立即注册</a></p>
        </div>
    </div>

</div>
</body>
</html>
