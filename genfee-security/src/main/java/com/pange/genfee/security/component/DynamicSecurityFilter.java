package com.pange.genfee.security.component;

import com.pange.genfee.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @auther Pange
 * @description
 * @date {2024/3/18}
 */
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    public void setMyAccessDecisionManager(DynamicAccessDecisionManager dynamicDecisionManager){
        super.setAccessDecisionManager(dynamicDecisionManager);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        FilterInvocation fi = new FilterInvocation(servletRequest,servletResponse,filterChain);
        //OPTIONS请求直接放行
        if(request.getMethod().equals(HttpMethod.OPTIONS.toString())){
            fi.getChain().doFilter(fi.getRequest(),fi.getResponse());
            return;
        }
        //白名单请求直接放行
        PathMatcher pathManager = new AntPathMatcher();
        for(String path: ignoreUrlsConfig.getUrls()){
            if(pathManager.match(path,request.getRequestURI())){
                fi.getChain().doFilter(fi.getRequest(),fi.getResponse());
                return;
            }
        }
        //调用AccessDecisionManager中的decide方法进行鉴权操作
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }finally {
            super.afterInvocation(token,null);
        }

    }

    @Override
    public void destroy() {

    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return dynamicSecurityMetadataSource;
    }
}
