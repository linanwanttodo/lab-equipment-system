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
 * 设备管理（管理员，1:1 原版 AdminEquipmentView）：
 * GET  /admin/equipment        管理列表 + 切换新增/编辑表单卡
 * POST /admin/equipment       action=add|update|delete
 */
@WebServlet("/admin/equipment")
public class AdminEquipmentServlet extends HttpServlet {

    private final EquipmentService equipmentService = new EquipmentService();
    private final LabDao labDao = new LabDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            WebUtil.requireAdmin(request);
            // 编辑：id 参数携带待编辑设备（表单卡回填）
            String editId = request.getParameter("id");
            if (editId != null && !editId.isEmpty()) {
                request.setAttribute("editing", equipmentService.getById(Long.parseLong(editId)));
            }
            request.setAttribute("formOpen", request.getParameter("form"));
            request.setAttribute("labs", labDao.findAll());
            request.setAttribute("list", equipmentService.search(null, null, null, null));
            request.getRequestDispatcher("/WEB-INF/views/equipment/admin.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", rootMessage(e));
            request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = "/admin/equipment";
        try {
            WebUtil.requireAdmin(request);
            String action = request.getParameter("action");
            Equipment e = new Equipment();
            e.setCode(trim(request.getParameter("code")));
            e.setLabId(parseOrNull(request.getParameter("labId")));
            e.setName(trim(request.getParameter("name")));
            e.setCategory(trim(request.getParameter("category")));
            e.setStatus(trim(request.getParameter("status")));
            e.setTotal(parseInt(request.getParameter("total"), 1));
            e.setAvailable(parseInt(request.getParameter("available"), 1));
            e.setDeposit(parseDouble(request.getParameter("deposit")));
            e.setDescription(trim(request.getParameter("description")));
            // 原版表单不含「可借数量」字段：新增时默认全部可借；编辑时保留现有可借
            if ("add".equals(action)) {
                if (request.getParameter("available") == null || request.getParameter("available").isEmpty()) {
                    e.setAvailable(e.getTotal());
                }
                equipmentService.add(e);
                response.sendRedirect(WebUtil.redirect(request, back, "新增设备成功"));
            } else if ("update".equals(action)) {
                e.setId(Long.parseLong(request.getParameter("id")));
                e.setAvailable(equipmentService.getById(e.getId()).getAvailable());
                equipmentService.update(e);
                response.sendRedirect(WebUtil.redirect(request, back, "修改设备成功"));
            } else if ("delete".equals(action)) {
                equipmentService.delete(Long.parseLong(request.getParameter("id")));
                response.sendRedirect(WebUtil.redirect(request, back, "删除设备成功"));
            } else {
                throw new ServiceException("未知的操作：" + action);
            }
        } catch (Exception ex) {
            response.sendRedirect(WebUtil.redirect(request, back, rootMessage(ex)));
        }
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

    private double parseDouble(String s) {
        try {
            return (s == null || s.isEmpty()) ? 0 : Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return 0;
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
