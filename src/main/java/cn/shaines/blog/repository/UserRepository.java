package cn.shaines.blog.repository;

import cn.shaines.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: user 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository//("userRepository")
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据 username 查找用户
     * @param username
     * @return
     */
    User findByUsername(String username);

}
