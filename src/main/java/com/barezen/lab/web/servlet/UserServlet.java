package com.barezen.lab.web.servlet;

import com.barezen.lab.dao.UserDao;
import com.barezen.lab.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 用户管理（管理员）：GET /users 用户列表 */
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            WebUtil.requireAdmin(request);
            request.setAttribute("list", userDao.findAll());
            request.getRequestDispatcher("/WEB-INF/views/user/users.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/msg.jsp").forward(request, response);
        }
    }
}
