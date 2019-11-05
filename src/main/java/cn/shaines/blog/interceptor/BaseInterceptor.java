package cn.shaines.blog.interceptor;

import cn.shaines.blog.entity.User;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 基础拦截器(校验token)
 * @date: created in 2019-05-08 16:24:23
 * @author: houyu
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String authorization = request.getHeader(JwtUtil.TOKEN_HEADER_AUTHORIZATION);
        if (!StringUtils.isEmpty(authorization)) {
            String id = jwtUtil.getId(authorization);
            if (StringUtils.isEmpty(id)) {
                throw new BusinessException("请携带有效token请求资源");
            }
            User user = jwtUtil.getUser(id);
            if (user == null) {
                throw new BusinessException("请登录再请求资源");
            }
        }
        return true;
    }

}
