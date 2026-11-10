<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="我的借用"/>
<c:set var="pageActive" value="mine"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>我的借用</h2>
            <p class="subtitle muted">查看借用记录并归还设备</p>
        </div>
        <a href="${pageContext.request.contextPath}/equipment" class="btn btn-primary" style="text-decoration:none">去借用设备</a>
    </div>

    <%-- 统计：借用中 / 逾期未还 / 已归还（逾期由应还时间动态判定） --%>
    <c:set var="borrowingCount" value="0"/>
    <c:set var="overdueCount" value="0"/>
    <c:set var="returnedCount" value="0"/>
    <c:forEach var="l" items="${requestScope.list}">
        <c:if test="${empty l.returnTime}">
            <c:set var="borrowingCount" value="${borrowingCount + 1}"/>
            <c:if test="${l.dueTime < now}">
                <c:set var="overdueCount" value="${overdueCount + 1}"/>
            </c:if>
        </c:if>
        <c:if test="${not empty l.returnTime}">
            <c:set var="returnedCount" value="${returnedCount + 1}"/>
        </c:if>
    </c:forEach>

    <div class="stats-grid">
        <div class="stat-card">
            <div><div class="stat-num">${fn:length(requestScope.list)}</div><div class="stat-label">全部记录</div></div>
        </div>
        <div class="stat-card">
            <div><div class="stat-num">${borrowingCount}</div><div class="stat-label">借用中</div></div>
        </div>
        <div class="stat-card ${overdueCount > 0 ? 'alert' : ''}">
            <div><div class="stat-num">${overdueCount}</div><div class="stat-label">逾期未还</div></div>
        </div>
        <div class="stat-card">
            <div><div class="stat-num">${returnedCount}</div><div class="stat-label">已归还</div></div>
        </div>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th>设备</th><th>编号</th><th>借出时间</th><th>应还时间</th>
            <th>归还时间</th><th>状态</th><th>逾期</th><th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="l" items="${requestScope.list}">
            <tr>
                <td>${l.equipmentName}</td>
                <td>${l.equipmentCode}</td>
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
                <td>
                    <c:if test="${empty l.returnTime}">
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/loans">
                            <input type="hidden" name="action" value="return"/>
                            <input type="hidden" name="id" value="${l.id}"/>
                            <input type="hidden" name="back" value="/loans/mine"/>
                            <button type="submit" class="btn btn-small">归还</button>
                        </form>
                    </c:if>
                    <c:if test="${not empty l.returnTime}"><span class="muted">-</span></c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="8"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无借用记录</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
