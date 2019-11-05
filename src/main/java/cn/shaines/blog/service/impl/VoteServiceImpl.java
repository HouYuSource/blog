package cn.shaines.blog.service.impl;

import cn.shaines.blog.config.Constants;
import cn.shaines.blog.entity.Blog;
import cn.shaines.blog.entity.Vote;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.repository.VoteRepository;
import cn.shaines.blog.service.*;
import cn.shaines.blog.utils.BlogUtil;
import cn.shaines.blog.utils.IdUtil;
import cn.shaines.blog.utils.MailUtil;
import cn.shaines.blog.utils.SimpleTimeCache;
import cn.shaines.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @description: Vote service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class VoteServiceImpl extends BaseServiceImpl<Vote, String, VoteRepository> implements VoteService {


    @Autowired
    private BlogService blogService;
    @Autowired
    private VoteService voteService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void save(Vote entity, SimpleTimeCache<String, String> emailTokenCache) {
        BlogUtil.checkSubmitToken(emailTokenCache, entity.getEmail(), request);
        //
        Vote tmpEntity = voteService.findByBlogId(entity.getBlogId(), entity.getEmail());
        if(tmpEntity != null){
            throw new BusinessException("亲, 你已经点赞过了!");
        }
        Constants.recentVoteAtomic.incrementAndGet();// 自增点赞数量
        // 用户是首次点赞
        entity.setId(IdUtil.generateId());
        entity.setCreateDate(new Date());
        voteService.save(entity);
        blogService.updateBlogIncrVoteSize(entity.getBlogId());
        String title = entity.getBlogTitle();
        MailUtil.asySendMail(entity.getEmail(), BlogUtil.voteTitle, BlogUtil.toReaderVoteContent(title));
        MailUtil.asySendMail(Constants.ADMIN_MAIL, BlogUtil.voteTitle, BlogUtil.toBloggerVoteContent(title, entity.getEmail()));

        super.save(entity);
    }

    @Override
    public Vote findByBlogId(String blogId, String email){
        return this.baseRepository.findByBlogIdAndEmail(blogId, email);
    }

    @Override
    public Page<Vote> findPageByKeyword(String keyword, Pageable pageable) {
        return this.baseRepository.findPageByKeyword("%" + keyword + "%", pageable);
    }
}
