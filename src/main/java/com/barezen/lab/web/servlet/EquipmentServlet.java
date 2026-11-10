package com.barezen.lab.web.servlet;

import com.barezen.lab.dao.LabDao;
import com.barezen.lab.entity.Equipment;
import com.barezen.lab.entity.User;
import com.barezen.lab.exception.ServiceException;
import com.barezen.lab.service.EquipmentService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 设备入口：
 * GET  /equipment                列表（支持 labId/category/status/keyword 筛选）
 * GET  /equipment?id=1           详情
 * GET  /equipment?form=add|edit  管理员新增/编辑表单
 * POST /equipment                管理员增删改（action=add|update|delete）
 */
@WebServlet("/equipment")
public class EquipmentServlet extends HttpServlet {

    private final EquipmentService equipmentService = new EquipmentService();
    private final LabDao labDao = new LabDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            String form = request.getParameter("form");
            if (id != null && !id.isEmpty()) {
                request.setAttribute("equipment", equipmentService.getById(Long.parseLong(id)));
                request.getRequestDispatcher("/WEB-INF/views/equipment/detail.jsp").forward(request, response);
            } else if (form != null && !form.isEmpty()) {
                WebUtil.requireAdmin(request);
                if ("edit".equals(form)) {
                    String editId = request.getParameter("editId");
                    request.setAttribute("equipment", equipmentService.getById(Long.parseLong(editId)));
                } else {
                    request.setAttribute("labs", labDao.findAll());
                    request.getRequestDispatcher("/WEB-INF/views/equipment/form.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("labs", labDao.findAll());
                request.getRequestDispatcher("/WEB-INF/views/equipment/form.jsp").forward(request, response);
            } else {
                Long labId = parseOrNull(request.getParameter("labId"));
                request.setAttribute("labId", labId);
                request.setAttribute("category", request.getParameter("category"));
                request.setAttribute("status", request.getParameter("status"));
                request.setAttribute("keyword", request.getParameter("keyword"));
                request.setAttribute("list", equipmentService.search(labId,
                        request.getParameter("category"), request.getParameter("status"),
                        request.getParameter("keyword")));
                request.setAttribute("labs", labDao.findAll());
                request.setAttribute("categories", equipmentService.categories());
                request.getRequestDispatcher("/WEB-INF/views/equipment/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            forwardError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = "/equipment";
        try {
            WebUtil.requireAdmin(request);
            String action = request.getParameter("action");
            Equipment e = new Equipment();
            e.setLabId(parseOrNull(request.getParameter("labId")));
            e.setName(trim(request.getParameter("name")));
            e.setCategory(trim(request.getParameter("category")));
            e.setStatus(trim(request.getParameter("status")));
            e.setTotal(parseInt(request.getParameter("total"), 1));
            e.setAvailable(parseInt(request.getParameter("available"), 1));
            e.setDescription(trim(request.getParameter("description")));
            if ("add".equals(action)) {
                equipmentService.add(e);
                response.sendRedirect(WebUtil.redirect(request, back, "新增设备成功：" + e.getName()));
            } else if ("update".equals(action)) {
                e.setId(Long.parseLong(request.getParameter("id")));
                equipmentService.update(e);
                response.sendRedirect(WebUtil.redirect(request, back, "修改设备成功"));
            } else if ("delete".equals(action)) {
                equipmentService.delete(Long.parseLong(request.getParameter("id")));
                response.sendRedirect(WebUtil.redirect(request, back, "删除设备成功"));
            } else {
                throw new ServiceException("未知的操作：" + action);
            }
        } catch (Exception e) {
            User user = WebUtil.currentUser(request);
            // 表单错误回到表单页；列表页错误回列表
            boolean hasForm = "add".equals(request.getParameter("action")) || "update".equals(request.getParameter("action"));
            if (hasForm && user != null && user.isAdmin()) {
                request.setAttribute("error", rootMessage(e));
                request.setAttribute("labs", labDao.findAll());
                request.getRequestDispatcher("/WEB-INF/views/equipment/form.jsp").forward(request, response);
            } else {
                response.sendRedirect(WebUtil.redirect(request, back, rootMessage(e)));
            }
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        request.setAttribute("error", rootMessage(e));
        request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
    }

    private Long parseOrNull(String s) {
        return (s == null || s.isEmpty()) ? null : Long.parseLong(s);
    }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private String rootMessage(Exception e) {
        Throwable t = e;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage() == null ? "操作失败" : t.getMessage();
    }
}
