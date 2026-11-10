<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="实验室管理"/>
<c:set var="pageActive" value="labs"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>实验室管理</h2>
            <p class="subtitle muted">维护实验室信息，删除前需先清空其设备</p>
        </div>
        <button class="btn btn-primary" type="button" onclick="openCreate()">+ 新增实验室</button>
    </div>

    <%-- 新增 / 编辑表单卡（JS 切换显隐，1:1 原版 AdminLabsView 交互） --%>
    <div id="labFormCard" class="card" style="display:none">
        <h3 id="labFormTitle">新增实验室</h3>
        <form method="post" action="${pageContext.request.contextPath}/labs" class="form">
            <input type="hidden" name="action" value="add" id="labAction"/>
            <input type="hidden" name="id" value="" id="labId"/>
            <div class="grid2">
                <label>名称</label>
                <input name="name" required id="labName"/>
                <label>位置</label>
                <input name="location" placeholder="例如：A 栋 301" id="labLocation"/>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">保存</button>
                <button class="btn" type="button" onclick="closeLabForm()">取消</button>
            </div>
        </form>
    </div>

    <table class="table">
        <thead>
        <tr><th>ID</th><th>名称</th><th>位置</th><th>设备数</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="lab" items="${requestScope.list}">
            <tr>
                <td>${lab.id}</td>
                <td>${lab.name}</td>
                <td>${empty lab.location ? '-' : lab.location}</td>
                <td>${lab.equipmentCount}</td>
                <td>
                    <button class="btn btn-small" type="button"
                            onclick="openEdit(${lab.id}, '${lab.name}', '${empty lab.location ? '' : lab.location}')">编辑</button>
                    <form class="inline" method="post" action="${pageContext.request.contextPath}/labs">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="id" value="${lab.id}"/>
                        <button type="submit" class="btn btn-small btn-danger"
                                onclick="return confirm('确认删除实验室「${lab.name}」？（有设备时会被业务规则拦截）')">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="5"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无实验室</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<script>
    function openCreate() {
        document.getElementById('labFormCard').style.display = '';
        document.getElementById('labFormTitle').textContent = '新增实验室';
        document.getElementById('labAction').value = 'add';
        document.getElementById('labId').value = '';
        document.getElementById('labName').value = '';
        document.getElementById('labLocation').value = '';
    }
    function openEdit(id, name, location) {
        document.getElementById('labFormCard').style.display = '';
        document.getElementById('labFormTitle').textContent = '编辑实验室';
        document.getElementById('labAction').value = 'update';
        document.getElementById('labId').value = id;
        document.getElementById('labName').value = name;
        document.getElementById('labLocation').value = location;
        window.scrollTo(0, 0);
    }
    function closeLabForm() {
        document.getElementById('labFormCard').style.display = 'none';
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
