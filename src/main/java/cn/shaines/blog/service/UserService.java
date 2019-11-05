package cn.shaines.blog.service;

import cn.shaines.blog.entity.User;

/**
 * @description: user service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface UserService extends BaseService<User, String>{

    /**
     * 根据 username 查找用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 登录
     * @param entity
     * @return
     */
    String login(User entity);

    /**
     * 登出
     */
    void logout();

    /**
     * 更改
     * @param entity
     */
    void updateUser(User entity);
}
