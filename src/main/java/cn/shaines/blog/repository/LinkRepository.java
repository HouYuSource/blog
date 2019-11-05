package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: Link 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, String> {

}
