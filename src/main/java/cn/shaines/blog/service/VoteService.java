package cn.shaines.blog.service;

import cn.shaines.blog.entity.Vote;
import cn.shaines.blog.utils.SimpleTimeCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @description: Vote service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface VoteService extends BaseService<Vote, String>{

    Vote findByBlogId(String blogId, String email);

    /**
     * 根据关键字查询分页
     * @param keyword
     * @param pageable
     * @return
     */
    Page<Vote> findPageByKeyword(String keyword, Pageable pageable);

    void save(Vote entity, SimpleTimeCache<String, String> emailTokenCache);

}
