<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="后台总览"/>
<c:set var="pageActive" value="stats"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>后台总览</h2>
            <p class="subtitle">系统运行数据一览</p>
        </div>
    </div>

    <%-- 统计卡 8 项（1:1 原版 DashboardView.buildItems） --%>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="users" size="20"/></div>
            <div><div class="stat-num">${requestScope.users}</div><div class="stat-label">用户数</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="flask" size="20"/></div>
            <div><div class="stat-num">${requestScope.labs}</div><div class="stat-label">实验室</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="box" size="20"/></div>
            <div><div class="stat-num">${requestScope.equipments}</div><div class="stat-label">设备种类</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="check" size="20"/></div>
            <div><div class="stat-num">${requestScope.equipmentAvailable}</div><div class="stat-label">可借库存</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="clipboard" size="20"/></div>
            <div><div class="stat-num">${requestScope.loanTotal}</div><div class="stat-label">借用总数</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="clock" size="20"/></div>
            <div><div class="stat-num">${requestScope.loansActive}</div><div class="stat-label">借用中</div></div>
        </div>
        <div class="stat-card ${requestScope.loansOverdue > 0 ? 'alert' : ''}">
            <div class="stat-icon i-red"><t:uicon name="alert" size="20"/></div>
            <div><div class="stat-num">${requestScope.loansOverdue}</div><div class="stat-label">逾期未还</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon"><t:uicon name="check" size="20"/></div>
            <div><div class="stat-num">${requestScope.loansReturned}</div><div class="stat-label">已归还</div></div>
        </div>
    </div>

    <div class="quick">
        <a href="${pageContext.request.contextPath}/admin/equipment"><t:uicon name="plus" size="14"/> 新增设备</a>
        <a href="${pageContext.request.contextPath}/labs"><t:uicon name="flask" size="14"/> 实验室管理</a>
        <a href="${pageContext.request.contextPath}/loans?scope=all"><t:uicon name="list" size="14"/> 借用记录</a>
        <a href="${pageContext.request.contextPath}/loans?scope=overdue"><t:uicon name="alarm" size="14"/> 逾期提醒</a>
        <a href="${pageContext.request.contextPath}/users"><t:uicon name="users" size="14"/> 用户管理</a>
    </div>

    <div class="dash-cols">
        <div class="card">
            <div class="card-title-row">
                <h3>设备类别分布</h3>
                <a href="${pageContext.request.contextPath}/admin/equipment">查看全部</a>
            </div>
            <c:choose>
                <c:when test="${fn:length(requestScope.categoryDist) > 0}">
                    <c:set var="maxCnt" value="1"/>
                    <c:forEach var="it" items="${requestScope.categoryDist}">
                        <c:if test="${it.value > maxCnt}"><c:set var="maxCnt" value="${it.value}"/></c:if>
                    </c:forEach>
                    <table class="table plain">
                        <tbody>
                        <c:forEach var="it" items="${requestScope.categoryDist}">
                            <tr>
                                <td class="w-label">${it.key}</td>
                                <td><div class="bar-track"><div class="bar" style="width:${it.value * 100 / maxCnt}%"></div></div></td>
                                <td class="w-num">${it.value}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无数据</div></div></c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <div class="card-title-row">
                <h3>实验室设备分布</h3>
                <a href="${pageContext.request.contextPath}/labs">查看全部</a>
            </div>
            <c:choose>
                <c:when test="${fn:length(requestScope.labDist) > 0}">
                    <c:set var="maxLab" value="1"/>
                    <c:forEach var="it" items="${requestScope.labDist}">
                        <c:if test="${it.value > maxLab}"><c:set var="maxLab" value="${it.value}"/></c:if>
                    </c:forEach>
                    <table class="table plain">
                        <tbody>
                        <c:forEach var="it" items="${requestScope.labDist}">
                            <tr>
                                <td class="w-label">${it.key}</td>
                                <td><div class="bar-track"><div class="bar alt" style="width:${it.value * 100 / maxLab}%"></div></div></td>
                                <td class="w-num">${it.value}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无数据</div></div></c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card">
        <div class="card-title-row">
            <h3>逾期 TOP（按逾期天数）</h3>
            <a href="${pageContext.request.contextPath}/loans?scope=overdue">查看全部</a>
        </div>
        <c:choose>
            <c:when test="${fn:length(requestScope.overdueTop) > 0}">
                <table class="table">
                    <thead>
                    <tr><th>设备</th><th>借用人</th><th>应还时间</th><th>逾期天数</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="l" items="${requestScope.overdueTop}">
                        <tr>
                            <td>${l.equipmentName} (${l.equipmentCode})</td>
                            <td>${l.userName}</td>
                            <td><fmt:formatDate value="${l.dueTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td><span class="badge danger">${l.overdueDays} 天</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise><div class="empty"><t:uicon name="inbox" size="36"/><div>当前没有逾期记录</div></div></c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <div class="card-title-row">
            <h3>最近借用</h3>
            <a href="${pageContext.request.contextPath}/loans?scope=all">查看全部</a>
        </div>
        <c:choose>
            <c:when test="${fn:length(requestScope.recentLoans) > 0}">
                <table class="table">
                    <thead>
                    <tr><th>设备</th><th>借用人</th><th>借出时间</th><th>应还时间</th><th>状态</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="l" items="${requestScope.recentLoans}">
                        <tr>
                            <td>${l.equipmentName} (${l.equipmentCode})</td>
                            <td>${l.userName}</td>
                            <td><fmt:formatDate value="${l.borrowTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td><fmt:formatDate value="${l.dueTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty l.returnTime}"><span class="badge ok">已归还</span></c:when>
                                    <c:when test="${l.dueTime < now}"><span class="badge danger">逾期</span></c:when>
                                    <c:otherwise><span class="badge warn">借用中</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise><div class="empty"><t:uicon name="inbox" size="36"/><div>还没有借用记录</div></div></c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
