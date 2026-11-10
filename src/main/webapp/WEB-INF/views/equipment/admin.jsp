<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="设备管理"/>
<c:set var="pageActive" value="equipmentAdmin"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section>
    <div class="page-head">
        <div>
            <h2>设备管理</h2>
            <p class="subtitle muted">新增 / 编辑 / 删除设备与库存</p>
        </div>
        <button class="btn btn-primary" type="button" onclick="openCreate()">+ 新增设备</button>
    </div>

    <%-- 新增 / 编辑表单卡（JS 切换显隐，1:1 原版 AdminEquipmentView 交互） --%>
    <div id="eqFormCard" class="card" style="display:${not empty formOpen ? '' : 'none'}">
        <h3 id="eqFormTitle">新增设备</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/equipment" class="form">
            <input type="hidden" name="action" value="add" id="eqAction"/>
            <input type="hidden" name="id" value="" id="eqId"/>
            <div class="grid2">
                <label>编号</label>
                <input name="code" required id="eqCode"/>
                <label>名称</label>
                <input name="name" required id="eqName"/>
                <label>类别</label>
                <input name="category" id="eqCategory"/>
                <label>实验室</label>
                <select name="labId" id="eqLabId">
                    <option value="">无</option>
                    <c:forEach var="lab" items="${requestScope.labs}">
                        <option value="${lab.id}">${lab.name}</option>
                    </c:forEach>
                </select>
                <label>总数</label>
                <input type="number" min="1" name="total" required id="eqTotal"/>
                <label>押金</label>
                <input type="number" step="0.01" min="0" name="deposit" id="eqDeposit"/>
                <label>状态</label>
                <select name="status" id="eqStatus">
                    <option value="AVAILABLE">可借</option>
                    <option value="MAINTENANCE">维护中</option>
                </select>
                <label>说明</label>
                <input name="description" id="eqDescription"/>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">保存</button>
                <button class="btn" type="button" onclick="closeEqForm()">取消</button>
            </div>
        </form>
    </div>

    <table class="table">
        <thead>
        <tr><th>编号</th><th>名称</th><th>实验室</th><th>可借 / 总数</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="e" items="${requestScope.list}">
            <tr>
                <td>${e.code}</td>
                <td>${e.name}</td>
                <td>${empty e.labName ? '-' : e.labName}</td>
                <td>${e.available} / ${e.total}</td>
                <td>
                    <span class="badge ${e.status == 'AVAILABLE' ? 'ok' : 'warn'}">
                        ${e.status == 'AVAILABLE' ? '可借' : '维护中'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-small" type="button"
                            data-lab-id="${e.labId}"
                            onclick="openEdit(${e.id}, '${e.code}', '${e.name}', '${empty e.category ? '' : e.category}', this.dataset.labId, ${e.total}, ${e.deposit}, '${e.status}', '${empty e.description ? '' : e.description}')">编辑</button>
                    <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/equipment">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="id" value="${e.id}"/>
                        <button type="submit" class="btn btn-small btn-danger"
                                onclick="return confirm('确认删除设备「${e.name}」？')">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty requestScope.list}">
            <tr><td colspan="6"><div class="empty"><t:uicon name="inbox" size="36"/><div>暂无设备</div></div></td></tr>
        </c:if>
        </tbody>
    </table>
</section>

<script>
    function openCreate() {
        document.getElementById('eqFormCard').style.display = '';
        document.getElementById('eqFormTitle').textContent = '新增设备';
        document.getElementById('eqAction').value = 'add';
        document.getElementById('eqId').value = '';
        document.getElementById('eqCode').value = '';
        document.getElementById('eqName').value = '';
        document.getElementById('eqCategory').value = '';
        document.getElementById('eqLabId').value = '';
        document.getElementById('eqTotal').value = 1;
        document.getElementById('eqDeposit').value = '';
        document.getElementById('eqStatus').value = 'AVAILABLE';
        document.getElementById('eqDescription').value = '';
        window.scrollTo(0, 0);
    }
    function openEdit(id, code, name, category, labId, total, deposit, status, description) {
        document.getElementById('eqFormCard').style.display = '';
        document.getElementById('eqFormTitle').textContent = '编辑设备';
        document.getElementById('eqAction').value = 'update';
        document.getElementById('eqId').value = id;
        document.getElementById('eqCode').value = code;
        document.getElementById('eqName').value = name;
        document.getElementById('eqCategory').value = category;
        document.getElementById('eqLabId').value = labId || '';
        document.getElementById('eqTotal').value = total;
        document.getElementById('eqDeposit').value = deposit > 0 ? deposit : '';
        document.getElementById('eqStatus').value = status;
        document.getElementById('eqDescription').value = description;
        window.scrollTo(0, 0);
    }
    function closeEqForm() {
        document.getElementById('eqFormCard').style.display = 'none';
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
