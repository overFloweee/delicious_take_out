package com.hjw.filter;

import com.alibaba.fastjson.JSON;
import com.hjw.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter
{
    // 路径匹配器
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取本次请求的URI
        String requestURI = request.getRequestURI();
        // log.info("拦截到请求： {} ", requestURI);

        String[] urls = new String[]{
                "/employee/login", "/employee/logout", "/backend/**", "/front/**",
                "/user/sendMsg","/user/login","/user/test",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"

        };

        // URL 匹配上了 urls，则直接放行
        if (check(urls, requestURI))
        {
            // log.info("本次请求不需要处理，并放行： {} ", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        Object id = request.getSession().getAttribute("employee");
        // 通过session 判断用户登陆状态，若登陆则直接放行(员工版)
        if (id !=null)
        {
            log.info("用户已登录，id： {} ", id);
            filterChain.doFilter(request, response);
            return;
        }
        // 判断用户登陆状态，若登陆则直接放行(用户版)

        Object userId = request.getSession().getAttribute("user");
        // 通过session 判断用户登陆状态，若登陆则直接放行(员工版)
        if (userId !=null)
        {
            log.info("用户已登录，id： {} ", userId);
            filterChain.doFilter(request, response);
            return;
        }




        // 未登录，返回未登录的结果
        Result<String> notlogin = Result.error("NOTLOGIN");
        response.getWriter().write(JSON.toJSONString(notlogin));
        log.info("用户未登录！");
    }

    @Override
    public void destroy()
    {
        Filter.super.destroy();
    }


    // 判断本次请求是否需要方行
    public boolean check(String[] urls, String requestURI)
    {
        for (String url : urls)
        {
            boolean match = antPathMatcher.match(url, requestURI);
            if (match)
            {
                return true;
            }
        }
        return false;
    }
}
