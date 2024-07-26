package com.wzy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wzy.reggie.common.BaseContext;
import com.wzy.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户是否完成登录的过滤器(不登录无法进入其他网页)
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static  final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();//backend/index.html
        log.info("拦截到请求：{}", requestURI);
        String[] Urls = new String[]{
                //带有employee为employee类中的login方法，logout方法
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/common/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
        };
        //2、判断本次请求是否需要处理
        boolean check = check(Urls, requestURI);
        //3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //4-1.判断登录转曝光台，如果已登录直接放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录,用户id为{}", request.getSession().getAttribute("employee"));
            Long emplId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(emplId);

            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");


        //4-2.判断登录转曝光台，如果已登录直接放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录,用户id为{}", request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
       //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));//前端获取到信息为NOTLOGIN会跳转
        return;
    }

    public boolean check(String[] Urls,String requestURI) {
        for (String Url : Urls) {
            if (pathMatcher.match(Url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
