package com.barezen.lab.web.servlet;

import com.barezen.lab.service.LoanService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 我的借用页（学生与管理员都可见）：
 * GET /loans/mine —— 当前登录用户的借用列表，可在此页归还
 */
@WebServlet("/loans/mine")
public class MineLoanServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("list", loanService.mine(WebUtil.currentUser(request).getId()));
        request.getRequestDispatcher("/WEB-INF/views/loan/mine.jsp").forward(request, response);
    }
}
