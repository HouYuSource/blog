package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @description: Comment 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {


    Page<Comment> findByStatus(int status, Pageable pageable);

    Page<Comment> findByStatusAndBlogId(int status, String blogId, Pageable pageable);

    @Query(nativeQuery=true, value = "SELECT * FROM comment c WHERE (c.content LIKE :keyword OR c.email LIKE :keyword OR c.to_email LIKE :keyword OR c.blog_title LIKE :keyword)")
    Page<Comment> findPageByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(nativeQuery=true, value = "SELECT COUNT(1) FROM comment c WHERE c.status != 1")
    int commentNeedVerifyTotal();
}
