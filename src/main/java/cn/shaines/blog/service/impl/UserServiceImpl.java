package cn.shaines.blog.service.impl;

import cn.shaines.blog.entity.User;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.repository.UserRepository;
import cn.shaines.blog.service.UserService;
import cn.shaines.blog.utils.BlogUtil;
import cn.shaines.blog.utils.JwtUtil;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: user service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, String, UserRepository> implements UserService {

    @Value("${blog.admin-user-id}")
    private String adminUserId;
    @Value("${blog.admin-username}")
    private String adminUsername;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    @Override
    public User findByUsername(String username) {
        return this.baseRepository.findByUsername(username);
    }

    @Override
    public String login(User entity) {
        String username = entity.getUsername();
        String password = entity.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BusinessException("用户名或者密码不可以为空");
        }
        User dbUser = this.findByUsername(username);
        if (dbUser == null) {
            throw new BusinessException("用户不存在");
        }
        password = password.replaceFirst(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)), "");
        if (BlogUtil.encryptPassword(password).equals(dbUser.getPassword())) {
            jwtUtil.login(dbUser);
            return jwtUtil.createJWT(dbUser.getId(), dbUser.getUsername(), dbUser.getRole());
        } else {
            throw new BusinessException("用户名或者密码不正确");
        }
    }

    @Override
    public void logout() {
        String authorization = request.getHeader(JwtUtil.TOKEN_HEADER_AUTHORIZATION);
        String id = jwtUtil.getId(authorization);
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException("请携带有效token进行退出操作");
        }
        jwtUtil.logout(id);
    }

    @Override
    public void updateUser(User entity) {
        if (adminUserId.equals(entity.getId())) {
            entity.setRole("ADMIN");            // 不允许修改ADMIN权限
        } else {
            entity.setRole("USER");             // 其他用户一律设置为USER
        }
        entity.setPassword(BlogUtil.encryptPassword(entity.getPassword()));
        this.save(entity);
    }
}
