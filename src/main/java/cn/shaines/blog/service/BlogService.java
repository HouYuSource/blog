package cn.shaines.blog.service;

import cn.shaines.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @description: blog service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface BlogService extends BaseService<Blog, String>{

    Page<Blog> findByKeywordLike(String keyword, Pageable pageable);

    Map<String, Integer> findTagMap();

    int updateBlogIncrVoteSize(@Param("id") String id);

    int updateBlogIncrReadSize(@Param("id") String id);

    int updateBlogIncrCommentSize(@Param("id") String id);

    Page<Blog> findAllByKeywordLike(@Param("keyword") String keyword, Pageable pageable);

    int updateBlogDecrCommentSize(String blogId);

    List<Blog> findAllBycatalogId(String catalogId);

    int voteTotal();

    int commentTotal();

    int updateCatalogName();

    List<Blog> findFairBlogList();

    List<Blog> findNearList(String id);
}
