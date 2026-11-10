<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- 当前时间（页面比较 dueTime < now 判断逾期） --%>
<c:set var="now" value="<%= new java.util.Date() %>"/>
<%-- 公共布局 1:1 复刻原版 LayoutView：侧边栏（功能/管理双组）+ 顶栏（面包屑 + 用户菜单）+ 内容区。
     页面使用 <c:set var="title"> 设定面包屑当前页、<c:set var="pageActive"> 高亮菜单。 --%>
<c:set var="user" value="${sessionScope.user}"/>
<c:set var="isAdm" value="${not empty user and user.admin}"/>
<c:set var="ctx">${pageContext.request.contextPath}</c:set>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>${empty title ? '5C 校园实验室设备借用系统' : title}</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
</head>
<body>

<div class="layout">

    <aside class="side">
        <div class="side-logo">
            <div class="logo-mark"><t:uicon name="flask" size="16"/></div>
            <span class="logo-text">实验室设备借用</span>
        </div>
        <nav class="side-menu">
            <div class="side-group">功能</div>
            <a class="side-item ${pageActive == 'equipment' ? 'active' : ''}" href="${ctx}/equipment">
                <t:uicon name="box" size="16"/><span>设备浏览</span>
            </a>
            <c:if test="${not empty user}">
                <a class="side-item ${pageActive == 'mine' ? 'active' : ''}" href="${ctx}/loans/mine">
                    <t:uicon name="clipboard" size="16"/><span>我的借用</span>
                </a>
            </c:if>
            <c:if test="${isAdm}">
                <div class="side-group">管理</div>
                <a class="side-item ${pageActive == 'stats' ? 'active' : ''}" href="${ctx}/stats">
                    <t:uicon name="dashboard" size="16"/><span>后台总览</span>
                </a>
                <a class="side-item ${pageActive == 'equipmentAdmin' ? 'active' : ''}" href="${ctx}/admin/equipment">
                    <t:uicon name="box" size="16"/><span>设备管理</span>
                </a>
                <a class="side-item ${pageActive == 'labs' ? 'active' : ''}" href="${ctx}/labs">
                    <t:uicon name="flask" size="16"/><span>实验室管理</span>
                </a>
                <a class="side-item ${pageActive == 'loans' ? 'active' : ''}" href="${ctx}/loans?scope=all">
                    <t:uicon name="list" size="16"/><span>借用记录</span>
                </a>
                <a class="side-item ${pageActive == 'overdue' ? 'active' : ''}" href="${ctx}/loans?scope=overdue">
                    <t:uicon name="alarm" size="16"/><span>逾期提醒</span>
                </a>
                <a class="side-item ${pageActive == 'users' ? 'active' : ''}" href="${ctx}/users">
                    <t:uicon name="users" size="16"/><span>用户管理</span>
                </a>
            </c:if>
        </nav>
    </aside>

    <div class="main">
        <header class="topbar">
            <button class="icon-btn" type="button"><t:uicon name="menu" size="16"/></button>
            <div class="crumb">
                <span>${isAdm ? '管理后台' : '工作台'}</span>
                <span class="sep">/</span>
                <span class="cur">${empty title ? '页面' : title}</span>
            </div>
            <div class="spacer"></div>
            <c:choose>
                <c:when test="${empty user}">
                    <a href="${ctx}/login">登录</a>
                    <a href="${ctx}/register">注册</a>
                </c:when>
                <c:otherwise>
                    <details class="user-menu">
                        <summary class="user-btn">
                            <span class="avatar">${fn:toUpperCase(fn:substring(user.getDisplayName(), 0, 1))}</span>
                            <span>${user.getDisplayName()}</span>
                        </summary>
                        <div class="user-pop">
                            <div class="uinfo">
                                <b>${user.getDisplayName()}</b>
                                <span>${user.username} · ${isAdm ? '管理员' : '学生'}</span>
                            </div>
                            <a class="uitem" href="${ctx}/loans/mine">
                                <t:uicon name="clipboard" size="14"/> 我的借用
                            </a>
                            <a class="uitem" href="${ctx}/logout">
                                <t:uicon name="logout" size="14"/> 退出登录
                            </a>
                        </div>
                    </details>
                </c:otherwise>
            </c:choose>
        </header>

        <main class="content">
            <c:if test="${not empty param.msg}">
                <p class="msg">${param.msg}</p>
            </c:if>
            <c:if test="${not empty param.error}">
                <p class="error">${param.error}</p>
            </c:if>
            <c:if test="${not empty requestScope.msg}">
                <p class="msg">${requestScope.msg}</p>
            </c:if>
            <c:if test="${not empty requestScope.error}">
                <p class="error">${requestScope.error}</p>
            </c:if>
