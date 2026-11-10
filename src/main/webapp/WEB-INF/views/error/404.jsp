<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title" value="404 页面不存在"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="card">
    <div class="alert error">404：您访问的页面不存在，请检查地址或从菜单进入。</div>
    <a class="btn gray" href="${pageContext.request.contextPath}/equipment">返回设备列表</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
