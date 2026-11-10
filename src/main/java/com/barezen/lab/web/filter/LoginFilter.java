package com.barezen.lab.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录检查过滤器：未登录访问受保护资源时跳转登录页。
 * 放行规则：登录页、注册页、错误页、静态资源（/css、/js、/favicon.ico）。
 * 注意：web.xml 中的 encodingFilter/loginFilter 映射与本注解同时存在时以容器合并为准；
 * 本项目实际启用 web.xml 中声明的 loginFilter（拦截 /*），过滤器链顺序：编码 → 登录检查。
 */
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getRequestURI().substring(request.getContextPath().length());

        boolean publicPath = path.equals("/login") || path.equals("/register") || path.equals("/logout")
                || path.equals("/404.jsp") || path.equals("/500.jsp")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/favicon");

        if (publicPath) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // 记录原始地址，登录成功后跳回
            String back = request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
            response.sendRedirect(request.getContextPath() + "/login?back=" + java.net.URLEncoder.encode(back, "UTF-8"));
            return;
        }
        chain.doFilter(req, resp);
    }
}
