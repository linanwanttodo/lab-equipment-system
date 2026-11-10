<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="设备详情"/>
<c:set var="pageActive" value="equipment"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="dev" value="${requestScope.equipment}"/>
<c:set var="canBorrow" value="${dev.status == 'AVAILABLE' and dev.available > 0}"/>

<section>
    <a class="btn" href="${pageContext.request.contextPath}/equipment">← 返回</a>

    <div class="card">
        <h2>
            ${dev.name}
            <span class="badge ${canBorrow ? 'ok' : 'warn'}">${canBorrow ? '可借' : '不可借'}</span>
        </h2>
        <dl class="detail">
            <div><dt>编号</dt><dd>${dev.code}</dd></div>
            <div><dt>类别</dt><dd>${empty dev.category ? '-' : dev.category}</dd></div>
            <div><dt>实验室</dt><dd>${empty dev.labName ? '-' : dev.labName}</dd></div>
            <div><dt>可借 / 总数</dt><dd>${dev.available} / ${dev.total}</dd></div>
            <div><dt>押金</dt><dd><c:choose><c:when test="${dev.deposit > 0}">¥${dev.deposit}</c:when><c:otherwise>-</c:otherwise></c:choose></dd></div>
            <div><dt>状态</dt><dd>${dev.status == 'AVAILABLE' ? '可借' : '维护中'}</dd></div>
            <div style="grid-column: 1 / -1"><dt>说明</dt><dd>${empty dev.description ? '-' : dev.description}</dd></div>
        </dl>
    </div>

    <c:if test="${canBorrow and not empty sessionScope.user}">
        <div class="card">
            <h3>申请借用</h3>
            <form method="post" action="${pageContext.request.contextPath}/loans" class="form">
                <input type="hidden" name="action" value="borrow"/>
                <input type="hidden" name="equipmentId" value="${dev.id}"/>
                <input type="hidden" name="back" value="/equipment?id=${dev.id}"/>
                <label>应还时间</label>
                <input type="datetime-local" name="dueTime" required/>
                <label>借用用途（选填）</label>
                <input name="purpose" placeholder="例如：课程实验"/>
                <button class="btn btn-primary" type="submit">提交借用</button>
            </form>
        </div>
    </c:if>
    <c:if test="${canBorrow and empty sessionScope.user}">
        <div class="card">
            <p class="muted">登录后可申请借用该设备。<a href="${pageContext.request.contextPath}/login">去登录</a></p>
        </div>
    </c:if>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
