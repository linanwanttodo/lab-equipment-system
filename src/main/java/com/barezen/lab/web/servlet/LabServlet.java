package com.barezen.lab.web.servlet;

import com.barezen.lab.exception.ServiceException;
import com.barezen.lab.service.LabService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 实验室入口：
 * GET  /labs  实验室列表（含管理员的增删改内联表单）
 * POST /labs  管理员增删改（action=add|update|delete），有设备时删除被业务规则拦截
 */
@WebServlet("/labs")
public class LabServlet extends HttpServlet {

    private final LabService labService = new LabService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("list", labService.list());
            request.getRequestDispatcher("/WEB-INF/views/lab/labs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", rootMessage(e));
            request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            WebUtil.requireAdmin(request);
            String action = request.getParameter("action");
            String name = trim(request.getParameter("name"));
            String location = trim(request.getParameter("location"));
            if ("add".equals(action)) {
                LabForm form = new LabForm(null, name, location);
                labService.add(form.toLab());
                response.sendRedirect(WebUtil.redirect(request, "/labs", "新增实验室成功"));
            } else if ("update".equals(action)) {
                LabForm form = new LabForm(Long.parseLong(request.getParameter("id")), name, location);
                labService.update(form.toLab());
                response.sendRedirect(WebUtil.redirect(request, "/labs", "修改实验室成功"));
            } else if ("delete".equals(action)) {
                labService.delete(Long.parseLong(request.getParameter("id")));
                response.sendRedirect(WebUtil.redirect(request, "/labs", "删除实验室成功"));
            } else {
                throw new ServiceException("未知的操作：" + action);
            }
        } catch (Exception e) {
            response.sendRedirect(WebUtil.redirect(request, "/labs", rootMessage(e)));
        }
    }

    private String trim(String s) { return s == null ? null : s.trim(); }

    private String rootMessage(Exception e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        return t.getMessage() == null ? "操作失败" : t.getMessage();
    }

    /** 内联表单数据载体 */
    private static class LabForm {
        private final Long id;
        private final String name;
        private final String location;

        LabForm(Long id, String name, String location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        com.barezen.lab.entity.Lab toLab() {
            com.barezen.lab.entity.Lab lab = new com.barezen.lab.entity.Lab();
            lab.setId(id);
            lab.setName(name);
            lab.setLocation(location);
            return lab;
        }
    }
}
