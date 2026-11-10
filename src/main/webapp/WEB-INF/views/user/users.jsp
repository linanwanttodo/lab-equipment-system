<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="用户管理"/>
<c:set var="pageActive" value="users"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>用户管理</h2>
            <p class="subtitle muted">查看注册用户（只读）</p>
        </div>
    </div>

    <table class="table">
        <thead>
        <tr><th>ID</th><th>用户名</th><th>姓名</th><th>联系方式</th><th>角色</th><th>注册时间</th></tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${requestScope.list}">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${empty u.name ? '-' : u.name}</td>
                <td>${empty u.contact ? '-' : u.contact}</td>
                <td>
                    <span class="badge ${u.admin ? 'warn' : 'ok'}">${u.admin ? '管理员' : '学生'}</span>
                </td>
                <td><fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="6"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无用户</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
