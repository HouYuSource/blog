package cn.shaines.blog.interceptor;

import cn.shaines.blog.annotation.RequiresRole;
import cn.shaines.blog.entity.User;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 角色拦截器
 * @date: created in 2019-05-08 16:20:41
 * @author: houyu
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 角色处理
            roleHandle(request, method);
        }
        // 如果不是映射到方法直接通过
        return true;
    }

    private void roleHandle(HttpServletRequest request, Method method) {

        RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);
        if (requiresRole != null) {
            List<String> roleList = new ArrayList<>(3);
            String[] roles = requiresRole.roles();
            if (roles.length != 0) {
                roleList.addAll(Arrays.asList(roles));
            }
            String role = requiresRole.value();
            if (!StringUtils.isEmpty(role)) {
                roleList.add(role);
            }
            if (roleList.isEmpty()) { return; } // 控制层设置的权限为空
            String authorization = request.getHeader(JwtUtil.TOKEN_HEADER_AUTHORIZATION);
            Claims claims = jwtUtil.getClaims(authorization);
            String id = claims.getId();
            User user = jwtUtil.getUser(id);
            if (user == null) {
                throw new BusinessException("请登录再请求");
            }
            //String userRole = (String) claims.get(JwtUtil.ROLE);
            String userRole = user.getRole();
            if (!roleList.contains(userRole)) {
                // 控制要求的权限列表, 当前请求的用户不具备
                throw new BusinessException("无权访问");
            }
        }
    }

}
