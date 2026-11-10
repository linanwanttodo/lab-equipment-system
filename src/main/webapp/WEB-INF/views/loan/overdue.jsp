<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="逾期提醒"/>
<c:set var="pageActive" value="overdue"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>逾期提醒</h2>
            <p class="subtitle muted">逾期由「借用中且已过应还时间」实时动态判定，管理员可代归还</p>
        </div>
        <span class="badge ${fn:length(requestScope.list) > 0 ? 'danger' : 'ok'}">
            <c:choose>
                <c:when test="${fn:length(requestScope.list) > 0}">${fn:length(requestScope.list)} 条逾期</c:when>
                <c:otherwise>无逾期</c:otherwise>
            </c:choose>
        </span>
    </div>

    <table class="table">
        <thead>
        <tr><th>设备</th><th>借用人</th><th>应还时间</th><th>逾期天数</th></tr>
        </thead>
        <tbody>
        <c:forEach var="l" items="${requestScope.list}">
            <tr>
                <td>${l.equipmentName} (${l.equipmentCode})</td>
                <td>${l.userName} (${l.username})</td>
                <td><fmt:formatDate value="${l.dueTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td><span class="badge danger">${l.overdueDays} 天</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="4"><div class="empty"><t:uicon name="inbox" size="36"/><div>当前没有逾期记录</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
