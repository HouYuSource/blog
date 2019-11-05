package cn.shaines.blog.service.impl;

import cn.shaines.blog.config.Constants;
import cn.shaines.blog.entity.Blog;
import cn.shaines.blog.entity.Comment;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.repository.CommentRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @description: Comment service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment, String, CommentRepository> implements CommentService {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public Page<Comment> findByStatus(int status, Pageable pageable) {
        return this.baseRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Comment> findByStatusAndBlogId(int status, String blogId, Pageable pageable) {
        return this.baseRepository.findByStatusAndBlogId(status, blogId, pageable);
    }

    @Override
    public Page<Comment> findPageByKeyword(String keyword, Pageable pageable) {
        return this.baseRepository.findPageByKeyword("%" + keyword + "%", pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentStatus(Comment entity) {
        Comment dbEntity = this.findById(entity.getId());
        if (dbEntity.getStatus().equals(entity.getStatus())) {// 状态相同
            return;
        }
        dbEntity.setStatus(entity.getStatus());
        this.save(dbEntity);
        if (entity.getStatus().equals(1)) {
            blogService.updateBlogIncrCommentSize(entity.getBlogId());
            Blog blog = blogService.findById(entity.getBlogId());
            MailUtil.asySendMail(dbEntity.getEmail(), BlogUtil.commentTitle, BlogUtil.showReaderCommentContent(blog.getTitle(), blog.getId()));
            if (!StringUtils.isEmpty(entity.getToEmail())) {
                MailUtil.asySendMail(dbEntity.getToEmail(), BlogUtil.commentTitle, BlogUtil.toBloggerCommentContent(blog.getTitle(), dbEntity.getEmail(), dbEntity.getContent(), blog.getId()));
            }
        } else {
            blogService.updateBlogDecrCommentSize(entity.getBlogId());
        }
    }

    @Override
    public int commentNeedVerifyTotal() {
        return this.baseRepository.commentNeedVerifyTotal();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Comment entity, SimpleTimeCache<String, String> emailTokenCache) {
        BlogUtil.checkSubmitToken(emailTokenCache, entity.getEmail(), request);
        if (entity.getEmail().equals(entity.getToEmail())) {
            throw new BusinessException("自己不可以回复自己");
        }
        Blog blog = blogService.findById(entity.getBlogId());
        if (Integer.valueOf(0).equals(blog.getCanComment())) {
            throw new BusinessException("该博客暂时不开启评论功能!", null);
        }
        entity.setId(IdUtil.generateId());
        entity.setCreateDate(new Date());
        commentService.save(entity);
        String title = blog.getTitle();
        MailUtil.asySendMail(entity.getEmail(), BlogUtil.commentTitle, BlogUtil.toReaderCommentContent(title));
        MailUtil.asySendMail(Constants.ADMIN_MAIL, BlogUtil.commentTitle, BlogUtil.toBloggerCommentContent(title, entity.getEmail(), entity.getContent(), blog.getId()));
    }

}
