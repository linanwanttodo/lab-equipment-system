<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title" value="设备表单"/>
<c:set var="pageActive" value="equipment"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-head">
    <div>
        <h2>${empty requestScope.equipment ? '新增设备' : '编辑设备'}</h2>
        <p class="subtitle muted">管理员维护设备台账，可借数量不得超过总数</p>
    </div>
    <a class="btn" href="${pageContext.request.contextPath}/equipment">返回列表</a>
</div>

<c:if test="${not empty requestScope.error}">
    <div class="alert error">${requestScope.error}</div>
</c:if>

<div class="card">
    <form method="post" action="${pageContext.request.contextPath}/equipment">
        <input type="hidden" name="action" value="${empty requestScope.equipment ? 'add' : 'update'}"/>
        <c:if test="${not empty requestScope.equipment}">
            <input type="hidden" name="id" value="${requestScope.equipment.id}"/>
        </c:if>
        <div class="form-grid">
            <label for="name">设备名称 *</label>
            <input type="text" id="name" name="name" required maxlength="100"
                   value="${requestScope.equipment.name}"/>
            <label for="category">类别 *</label>
            <input type="text" id="category" name="category" required maxlength="50"
                   value="${requestScope.equipment.category}" list="catList"/>
            <datalist id="catList">
                <c:forEach var="cat" items="${requestScope.categories}">
                    <option value="${cat}"/>
                </c:forEach>
            </datalist>
            <label for="labId">所属实验室</label>
            <select id="labId" name="labId">
                <option value="">—</option>
                <c:forEach var="lab" items="${requestScope.labs}">
                    <option value="${lab.id}" ${requestScope.equipment.labId == lab.id ? 'selected' : ''}>${lab.name}</option>
                </c:forEach>
            </select>
            <label for="total">总数</label>
            <input type="text" id="total" name="total" value="${empty requestScope.equipment ? 1 : requestScope.equipment.total}"/>
            <label for="available">可借数量</label>
            <input type="text" id="available" name="available" value="${empty requestScope.equipment ? 1 : requestScope.equipment.available}"/>
            <label for="status">状态</label>
            <select id="status" name="status">
                <option value="AVAILABLE" ${requestScope.equipment.status == 'AVAILABLE' ? 'selected' : ''}>可借用</option>
                <option value="BORROWED" ${requestScope.equipment.status == 'BORROWED' ? 'selected' : ''}>已借出</option>
                <option value="DISABLED" ${requestScope.equipment.status == 'DISABLED' ? 'selected' : ''}>已停用</option>
            </select>
            <label for="description">描述</label>
            <textarea id="description" name="description" maxlength="255">${requestScope.equipment.description}</textarea>
            <label></label>
            <div style="display:flex;gap:10px">
                <button type="submit" class="btn">保 存</button>
                <a class="btn gray" href="${pageContext.request.contextPath}/equipment">取 消</a>
            </div>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
