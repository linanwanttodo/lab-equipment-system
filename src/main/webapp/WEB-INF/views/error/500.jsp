<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title" value="500 服务器错误"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="card">
    <div class="alert error">500：服务器内部错误，请稍后重试或联系管理员。</div>
    <a class="btn gray" href="${pageContext.request.contextPath}/equipment">返回设备列表</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
