package com.barezen.lab.web.servlet;

import com.barezen.lab.exception.ServiceException;
import com.barezen.lab.service.LoanService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 借用入口：
 * GET  /loans?scope=mine      我的借用（默认）
 * GET  /loans?scope=all       全部借用（管理员）
 * GET  /loans?scope=overdue   逾期列表（管理员）
 * POST /loans                 借用（action=borrow）或归还（action=return）
 */
@WebServlet("/loans")
public class LoanServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            WebUtil.requireAdmin(request);
            String scope = request.getParameter("scope");
            if ("all".equals(scope)) {
                request.setAttribute("list", loanService.all());
                request.getRequestDispatcher("/WEB-INF/views/loan/all.jsp").forward(request, response);
            } else if ("overdue".equals(scope)) {
                request.setAttribute("list", loanService.overdue());
                request.getRequestDispatcher("/WEB-INF/views/loan/overdue.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/loans?scope=all");
            }
        } catch (Exception e) {
            request.setAttribute("error", rootMessage(e));
            request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
        }
    }

    /** 我的借用：/loans/mine 由 LoginFilter 保护，映射到单独路径避免与 POST 混淆 */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = request.getParameter("back");
        String target = (back == null || back.isEmpty()) ? "/equipment" : back;
        try {
            WebUtil.currentUser(request); // 受保护路径必有用户
            String action = request.getParameter("action");
            if ("borrow".equals(action)) {
                loanService.borrow(WebUtil.currentUser(request),
                        Long.parseLong(request.getParameter("equipmentId")),
                        request.getParameter("dueTime"), request.getParameter("purpose"));
                response.sendRedirect(WebUtil.redirect(request, target, "借用成功，请在「我的借用」中查看"));
            } else if ("return".equals(action)) {
                loanService.giveBack(WebUtil.currentUser(request), Long.parseLong(request.getParameter("id")));
                response.sendRedirect(WebUtil.redirect(request, target, "归还成功"));
            } else {
                throw new ServiceException("未知的操作：" + action);
            }
        } catch (Exception e) {
            response.sendRedirect(WebUtil.redirect(request, target, rootMessage(e)));
        }
    }

    private String rootMessage(Exception e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        return t.getMessage() == null ? "操作失败" : t.getMessage();
    }
}
