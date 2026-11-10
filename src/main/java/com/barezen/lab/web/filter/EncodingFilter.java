package com.barezen.lab.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 编码过滤器：全局统一 UTF-8，解决 JavaEE8 传统栈的中文乱码问题。
 * 过滤器链顺序：encodingFilter 先于 loginFilter（按 filter-mapping 声明顺序）。
 */
@WebFilter(filterName = "encodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig config) {
        encoding = config.getInitParameter("encoding");
        if (encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }
}
