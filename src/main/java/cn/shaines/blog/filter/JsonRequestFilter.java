package cn.shaines.blog.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description: 过滤器, 实现request可以多次读取输入流
 * @date: created in 2019-07-13 15:06:53
 * @author: houyu for.houyu@foxmail.com
 */
@Component
@WebFilter(filterName = "crownFilter", urlPatterns = "/*")
public class JsonRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (String.valueOf(servletRequest.getContentType()).contains("application/json")){
            // 如果请求类型是json.那就加上封装的CacheHttpServletRequestWrapper
            filterChain.doFilter(new CacheHttpServletRequestWrapper((HttpServletRequest)servletRequest), servletResponse);
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
