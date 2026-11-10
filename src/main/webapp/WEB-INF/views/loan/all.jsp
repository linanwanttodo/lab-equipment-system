<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="借用记录"/>
<c:set var="pageActive" value="loans"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>全部借用记录</h2>
            <p class="subtitle muted">管理员查看全平台借用情况</p>
        </div>
    </div>

    <table class="table">
        <thead>
        <tr><th>设备</th><th>借用人</th><th>借出</th><th>应还</th><th>归还</th><th>状态</th><th>逾期</th></tr>
        </thead>
        <tbody>
        <c:forEach var="l" items="${requestScope.list}">
            <tr>
                <td>${l.equipmentName} (${l.equipmentCode})</td>
                <td>${l.userName} (${l.username})</td>
                <td><fmt:formatDate value="${l.borrowTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td><fmt:formatDate value="${l.dueTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td><fmt:formatDate value="${l.returnTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td>
                    <c:choose>
                        <c:when test="${not empty l.returnTime}"><span class="badge ok">已归还</span></c:when>
                        <c:when test="${l.dueTime < now}"><span class="badge danger">逾期</span></c:when>
                        <c:otherwise><span class="badge warn">借用中</span></c:otherwise>
                    </c:choose>
                </td>
                <td><c:choose><c:when test="${l.overdueDays > 0}">${l.overdueDays} 天</c:when><c:otherwise>-</c:otherwise></c:choose></td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="7"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无借用记录</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
