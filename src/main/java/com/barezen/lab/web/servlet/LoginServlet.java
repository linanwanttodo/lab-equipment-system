package com.barezen.lab.web.servlet;

import com.barezen.lab.exception.ServiceException;
import com.barezen.lab.service.AuthService;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 登录：GET 显示登录页，POST 校验并写入 HttpSession */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = request.getParameter("back");
        try {
            com.barezen.lab.entity.User user =
                    authService.login(request.getParameter("username"), request.getParameter("password"));
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            // 会话固定防护：换发 Session ID
            request.changeSessionId();
            String target = (back == null || back.isEmpty()
                    || !back.startsWith("/") || back.startsWith("//"))
                    ? "/equipment" : back;
            response.sendRedirect(request.getContextPath() + target);
        } catch (ServiceException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("back", back);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
