package cn.shaines.blog.service.impl;

import cn.shaines.blog.entity.Blog;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.repository.BlogRepository;
import cn.shaines.blog.service.BlogService;
import cn.shaines.blog.utils.PublicUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: blog service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service//("blogService")
public class BlogServiceImpl extends BaseServiceImpl<Blog, String, BlogRepository> implements BlogService {

    private Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    @Override
    public Page<Blog> findByKeywordLike(String keyword, Pageable pageable) {
        //return this.baseRepository.findByKeywordLikeAndStatus(keyword, 1, pageable);
        Specification<Blog> specification = (Specification<Blog>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate status = criteriaBuilder.equal(root.get("status").as(Integer.class), 1);
            predicates.add(status);
            if (!StringUtils.isEmpty(keyword)) {
                String tempKey = keyword;
                tempKey = PublicUtil.join("%", tempKey, "%");

                Predicate catalogName = criteriaBuilder.like(root.get("catalogName").as(String.class), tempKey);
                Predicate tags = criteriaBuilder.like(root.get("tags").as(String.class), tempKey);
                Predicate title = criteriaBuilder.like(root.get("title").as(String.class), tempKey);
                Predicate summary = criteriaBuilder.like(root.get("summary").as(String.class), tempKey);

                Predicate or = criteriaBuilder.or(catalogName, title, summary, tags);
                predicates.add(or);

            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
        return this.baseRepository.findAll(specification, pageable);
    }

    @Override
    public Blog findByIdAndStatus(String id) {
        return this.baseRepository.findByIdAndStatus(id, 1);
    }


    @Override
    public Map<String, Integer> findTagMap() {
        List<String> tagList = this.baseRepository.findTagList();
        List<String> tempList = new ArrayList<>(tagList.size() * 5);
        tagList.forEach(v -> {
            String[] split = PublicUtil.split(v, ",");
            tempList.addAll(Arrays.asList(split));
        });
        Map<String, Integer> resMap = PublicUtil.getAloneCountInList(tempList);
        return resMap;
    }

    @Override
    public int updateBlogIncrVoteSize(String id) {
        return this.baseRepository.updateBlogIncrVoteSize(id);
    }

    @Override
    public int updateBlogIncrReadSize(String id) {
        return this.baseRepository.updateBlogIncrReadSize(id);
    }

    @Override
    public int updateBlogIncrCommentSize(String id) {
        return this.baseRepository.updateBlogIncrCommentSize(id);
    }

    @Override
    public Page<Blog> findAllByKeywordLike(String keyword, Pageable pageable) {
        return this.baseRepository.findAllByKeywordLike(keyword, pageable);
    }

    @Override
    public int updateBlogDecrCommentSize(String blogId) {
        return this.baseRepository.updateBlogDecrCommentSize(blogId);
    }

    @Override
    public List<Blog> findAllBycatalogId(String catalogId) {
        return this.baseRepository.findAllByCatalogId(catalogId);
    }

    @Override
    public int voteTotal() {
        return this.baseRepository.voteTotal();
    }

    @Override
    public int commentTotal() {
        return this.baseRepository.commentTotal();
    }

    @Override
    public int updateCatalogName() {
        return this.baseRepository.updateCatalogName();
    }

    private static volatile List<Blog> query = new ArrayList<>(32);
    private static volatile List<Blog> fairBlogList = null;

    void init() {
        if (fairBlogList == null) {
            synchronized (BlogServiceImpl.class) {
                if (fairBlogList == null) {
                    // 第一次需要执行一下, 因为下面是异步的, 不执行一下的话, 第一次打开页面出不来轮播图
                    changeFairBlogList(4);
                    class Worker implements Runnable {
                        BlogServiceImpl blogService;
                        private Worker(BlogServiceImpl blogService) {
                            this.blogService = blogService;
                        }
                        @Override
                        public void run() {
                            blogService.changeFairBlogList(4);
                            logger.info("加载四张轮播图片");
                        }
                    }
                    ScheduledExecutorService monitorExecutorPool = new ScheduledThreadPoolExecutor(1);
                    monitorExecutorPool.scheduleAtFixedRate(new Worker(this), 0, 1, TimeUnit.DAYS);
                }
            }
        }
    }

    private void changeFairBlogList(int size) {
        List<Blog> result = new ArrayList<>(size);
        while (query.size() < size) {
            List<Blog> newList = this.baseRepository.findAllByStatus(1);
            query.addAll(newList.size() > 0 ? newList : Collections.singletonList(new Blog()));
        }
        Iterator<Blog> iterator = query.iterator();
        for (int i = 0; i < size; i++) {
            result.add(iterator.next());
            iterator.remove();
        }
        fairBlogList = result;
    }

    @Override
    public List<Blog> findFairBlogList() {
        init();
        return fairBlogList;
    }

    @Override
    public List<Blog> findNearList(String id) {
        List<Blog> list = this.baseRepository.findNearList(id);
        List<String> idList = list.stream().map(Blog::getId).collect(Collectors.toList());
        List<Blog> result = null;
        if (idList.size() <= 3) {
            int indexOf = idList.indexOf(id);
            if (indexOf < 0) { throw new BusinessException("找不到对应的博客"); }
            Blog[] array = { indexOf - 1 > -1 ? list.get(indexOf - 1) : null , list.get(indexOf) , indexOf + 1 < idList.size() ? list.get(indexOf + 1) : null};
            result = Arrays.asList(array);
        }
        return result;
    }

}
