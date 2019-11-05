package cn.shaines.blog.service;

import cn.shaines.blog.entity.Comment;
import cn.shaines.blog.utils.SimpleTimeCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @description: Comment service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface CommentService extends BaseService<Comment, String>{


    Page<Comment> findByStatus(int status, Pageable pageable);

    Page<Comment> findByStatusAndBlogId(int status, String blogId, Pageable pageable);

    Page<Comment> findPageByKeyword(String keyword, Pageable pageable);

    void updateCommentStatus(Comment entity);

    int commentNeedVerifyTotal();

    void save(Comment entity, SimpleTimeCache<String, String> emailTokenCache);
}
