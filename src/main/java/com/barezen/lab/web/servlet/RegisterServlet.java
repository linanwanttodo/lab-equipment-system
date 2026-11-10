package com.barezen.lab.web.servlet;

import com.barezen.lab.exception.ServiceException;
import com.barezen.lab.service.AuthService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 注册：默认学生角色，成功后跳转登录页 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            authService.register(request.getParameter("username"), request.getParameter("name"),
                    request.getParameter("password"), request.getParameter("password2"),
                    request.getParameter("contact"));
            response.sendRedirect(WebUtil.redirect(request, "/login", "注册成功，请登录"));
        } catch (ServiceException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
