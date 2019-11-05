package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @description: Vote 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {

    Vote findByBlogIdAndEmail(String blogId, String email);

    @Query(nativeQuery=true, value = "SELECT * FROM vote v WHERE (v.blog_title LIKE :keyword OR v.email LIKE :keyword)")
    Page<Vote> findPageByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
