package com.barezen.lab.web;

import com.barezen.lab.entity.User;
import com.barezen.lab.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** Servlet 公共工具：当前登录用户、管理员校验、消息重定向 */
public final class WebUtil {

    private WebUtil() { }

    /** 从 Session 取当前登录用户（LoginFilter 已保证受保护路径下必有） */
    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    /** 管理员校验：非管理员直接拒绝 */
    public static void requireAdmin(HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null || !user.isAdmin()) {
            throw new ServiceException("该操作需要管理员权限");
        }
    }

    /** 成功消息重定向：?msg=...（JSP 中显示一次性提示） */
    public static String redirect(HttpServletRequest request, String path, String msg)
            throws UnsupportedEncodingException {
        return request.getContextPath() + path
                + (msg == null ? "" : "?msg=" + URLEncoder.encode(msg, "UTF-8"));
    }
}
