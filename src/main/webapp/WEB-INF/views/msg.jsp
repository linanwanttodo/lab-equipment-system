<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 通用消息页：Servlet 捕获异常后转发到这里展示 error/msg --%>
<c:set var="title" value="提示"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="card">
    <c:choose>
        <c:when test="${not empty requestScope.error}">
            <div class="alert error">${requestScope.error}</div>
        </c:when>
        <c:when test="${not empty requestScope.msg}">
            <div class="alert success">${requestScope.msg}</div>
        </c:when>
        <c:otherwise>
            <div class="alert error">页面提示信息缺失</div>
        </c:otherwise>
    </c:choose>
    <a class="btn gray" href="${pageContext.request.contextPath}/equipment">返回设备列表</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
