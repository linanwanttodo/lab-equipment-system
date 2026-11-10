package com.barezen.lab.web.servlet;

import com.barezen.lab.service.StatsService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 后台总览仪表盘（管理员）：GET /stats */
@WebServlet("/stats")
public class StatsServlet extends HttpServlet {

    private final StatsService statsService = new StatsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            WebUtil.requireAdmin(request);
            request.setAttribute("users", statsService.users());
            request.setAttribute("labs", statsService.labs());
            request.setAttribute("equipments", statsService.equipments());
            request.setAttribute("equipmentAvailable", statsService.equipmentAvailable());
            request.setAttribute("loanTotal", statsService.loanTotal());
            request.setAttribute("loansActive", statsService.loansActive());
            request.setAttribute("loansOverdue", statsService.loansOverdue());
            request.setAttribute("loansReturned", statsService.loansReturned());
            request.setAttribute("categoryDist", statsService.categoryDistribution());
            request.setAttribute("labDist", statsService.labDistribution());
            request.setAttribute("recentLoans", statsService.recentLoans(8));
            request.setAttribute("overdueTop", statsService.overdueTop());
            request.getRequestDispatcher("/WEB-INF/views/stats/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
        }
    }
}
