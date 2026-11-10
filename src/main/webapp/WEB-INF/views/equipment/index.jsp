<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="设备浏览"/>
<c:set var="pageActive" value="equipment"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>设备列表</h2>
            <p class="subtitle muted">浏览并借用实验室设备</p>
        </div>
    </div>

    <div class="filters">
        <form method="get" action="${pageContext.request.contextPath}/equipment" style="display:contents">
            <input type="text" name="keyword" placeholder="搜索名称 / 编号" value="${param.keyword}"/>
            <select name="labId">
                <option value="">全部实验室</option>
                <c:forEach var="lab" items="${requestScope.labs}">
                    <option value="${lab.id}" ${param.labId == lab.id ? 'selected' : ''}>${lab.name}</option>
                </c:forEach>
            </select>
            <select name="category">
                <option value="">全部类别</option>
                <c:forEach var="cat" items="${requestScope.categories}">
                    <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
                </c:forEach>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="AVAILABLE" ${param.status == 'AVAILABLE' ? 'selected' : ''}>可借</option>
                <option value="MAINTENANCE" ${param.status == 'MAINTENANCE' ? 'selected' : ''}>维护中</option>
            </select>
            <button class="btn btn-primary" type="submit">查询</button>
        </form>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th>编号</th><th>名称</th><th>类别</th><th>实验室</th>
            <th>可借 / 总数</th><th>押金</th><th>状态</th><th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="e" items="${requestScope.list}">
            <tr>
                <td>${e.code}</td>
                <td><a href="${pageContext.request.contextPath}/equipment?id=${e.id}">${e.name}</a></td>
                <td>${empty e.category ? '-' : e.category}</td>
                <td>${empty e.labName ? '-' : e.labName}</td>
                <td>${e.available} / ${e.total}</td>
                <td>
                    <c:choose>
                        <c:when test="${e.deposit > 0}">¥${e.deposit}</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <span class="badge ${e.status == 'AVAILABLE' ? 'ok' : 'warn'}">
                        ${e.status == 'AVAILABLE' ? '可借' : '维护中'}
                    </span>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${e.status == 'AVAILABLE' and e.available > 0 and not empty sessionScope.user}">
                            <button class="btn btn-primary btn-small" type="button"
                                    onclick="if(confirm('借用【${e.name}】，默认 7 天后归还？')) quickBorrow(${e.id}, '${e.name}')">借用</button>
                        </c:when>
                        <c:otherwise>
                            <span class="muted">不可借</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="8"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无设备</div></div></td></tr>
        </c:if>
        </tbody>
    </table>

    <form id="borrowForm" method="post" action="${pageContext.request.contextPath}/loans" style="display:none">
        <input type="hidden" name="action" value="borrow"/>
        <input type="hidden" name="equipmentId" value="" id="borrowEquipmentId"/>
        <input type="hidden" name="back" value="/equipment"/>
    </form>
</section>

<script>
    function quickBorrow(id, name) {
        document.getElementById('borrowEquipmentId').value = id;
        document.getElementById('borrowForm').submit();
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
