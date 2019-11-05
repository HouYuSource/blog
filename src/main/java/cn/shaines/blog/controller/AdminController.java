package cn.shaines.blog.controller;

import cn.shaines.blog.annotation.RequiresRole;
import cn.shaines.blog.config.Constants;
import cn.shaines.blog.entity.*;
import cn.shaines.blog.repository.BlogRepository;
import cn.shaines.blog.service.*;
import cn.shaines.blog.utils.BlogUtil;
import cn.shaines.blog.utils.IdUtil;
import cn.shaines.blog.utils.JwtUtil;
import cn.shaines.blog.vo.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author houyu for.houyu@foxmail.com
 * @createTime 2019/5/31 22:31
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("${blog.admin-user-id}")
    private String adminUserId;
    @Value("${blog.admin-username}")
    private String adminUsername;

    @Autowired
    private BlogService blogService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserService userService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private VoteService voteService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LogService logService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BlogRepository blogRepository;
    /**
     * ******************************************************* 博客 ********************************************************
     */
    @RequiresRole
    @GetMapping("/blog/page")
    public Result blogPage(//
                           @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, Sort.Direction.DESC, "id");
        Page<Blog> page = blogService.findAllByKeywordLike("%" + keyword + "%", pageable);
        return Result.success(page);
    }

    /** 详情 */
    @RequiresRole
    @GetMapping("/blog/{id}")
    public Result blogDetails(@PathVariable("id") String id) {
        Blog entity = blogService.findById(id);
        return Result.success(entity);
    }

    /** 保存 */
    @RequiresRole("ADMIN")
    @PostMapping("/blog")
    public Result saveBlog(@RequestBody Blog entity) {
        entity.setId(IdUtil.generateId());
        entity.setUserId(adminUserId);
        entity.setUsername(adminUsername);
        entity.setContent(BlogUtil.formatMDImageSrc(entity.getContent()));
        entity.setDisplayImg(BlogUtil.formatImageSrc(entity.getDisplayImg()));
        blogService.save(entity);
        return Result.SUCCESS;
    }

    /** 修改 */
    @RequiresRole("ADMIN")
    @PutMapping("/blog")
    public Result updateBlog(@RequestBody Blog entity) {
        entity.setUserId(adminUserId);
        entity.setUsername(adminUsername);
        entity.setDisplayImg(BlogUtil.formatImageSrc(entity.getDisplayImg()));
        blogService.save(entity);
        return Result.SUCCESS;
    }

    /** 删除 */
    @RequiresRole("ADMIN")
    @DeleteMapping("/blog/{id}")
    public Result deleteBlog(@PathVariable("id") String id) {
        blogService.deleteById(id);
        return Result.SUCCESS;
    }

    /**
     * ******************************************************* 分类 ********************************************************
     */
    @RequiresRole
    @GetMapping("/catalog/page")
    public Result catalogPage(//
                              @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "catalogName");
        Page<Catalog> page = catalogService.findAll(pageable);
        return Result.success(page);
    }

    /** 保存 */
    @RequiresRole("ADMIN")
    @PostMapping("/catalog")
    public Result saveCatalog(@RequestBody Catalog entity) {
        entity.setId(IdUtil.generateId());
        entity.setUserId(adminUserId);
        entity.setUsername(adminUsername);
        catalogService.save(entity);
        return Result.SUCCESS;
    }

    /** 修改 */
    @RequiresRole("ADMIN")
    @PutMapping("/catalog")
    public Result updateCatalog(@RequestBody Catalog entity) {
        entity.setUserId(adminUserId);
        entity.setUsername(adminUsername);
        catalogService.save(entity);
        blogService.updateCatalogName();
        return Result.SUCCESS;
    }

    /** 删除 */
    @RequiresRole("ADMIN")
    @DeleteMapping("/catalog/{id}")
    public Result deleteCatalog(@PathVariable("id") String id) {
        catalogService.deleteById(id);
        return Result.SUCCESS;
    }


    /**
     * ******************************************************* 友链 ********************************************************
     */

    /** 列表 */
    @RequiresRole
    @GetMapping("/link/page")
    public Result linkPage(//
                           @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "text");
        Page<Link> page = linkService.findAll(pageable);
        return Result.success(page);
    }

    /** 保存 */
    @RequiresRole("ADMIN")
    @PostMapping("/link")
    public Result saveLink(@RequestBody Link entity) {
        entity.setId(IdUtil.generateId());
        linkService.save(entity);
        return Result.SUCCESS;
    }

    /** 修改 */
    @RequiresRole("ADMIN")
    @PutMapping("/link")
    public Result updateLink(@RequestBody Link entity) {
        linkService.save(entity);
        return Result.SUCCESS;
    }

    /** 删除 */
    @RequiresRole("ADMIN")
    @DeleteMapping("/link/{id}")
    public Result deleteLink(@PathVariable("id") String id) {
        linkService.deleteById(id);
        return Result.SUCCESS;
    }

    /**
     * ******************************************************* 用户 ********************************************************
     */

    /** 列表 */
    @RequiresRole
    @GetMapping("/user/page")
    public Result userPage(//
                           @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "id");
        Page<User> page = userService.findAll(pageable);
        page.get().forEach(v -> v.setPassword(null));
        return Result.success(page);
    }

    /** 保存 */
    @RequiresRole("ADMIN")
    @PostMapping("/user")
    public Result saveUser(@RequestBody User entity) {
        entity.setId(IdUtil.generateId());
        entity.setPassword(BlogUtil.encryptPassword(entity.getPassword()));
        userService.save(entity);
        return Result.SUCCESS;
    }

    /** 详情 */
    @RequiresRole
    @GetMapping("/user/details")
    public Result userDetails() {
        String authorization = request.getHeader(JwtUtil.TOKEN_HEADER_AUTHORIZATION);
        String id = jwtUtil.getId(authorization);
        if (StringUtils.isEmpty(id)) {
            return Result.fail("请携带有效token请求资源");
        }
        User entity = userService.findById(id);
        entity = JSON.parseObject(JSON.toJSONString(entity), User.class);// 解决jpa自动触发执行update
        entity.setPassword(null);                   // 这个是关键
        return Result.success(entity);
    }

    /** 修改 */
    @RequiresRole("ADMIN")
    @PutMapping("/user")
    public Result updateUser(@RequestBody User entity) {
        userService.updateUser(entity);
        return Result.SUCCESS;
    }

    /** 删除 */
    @RequiresRole("ADMIN")
    @DeleteMapping("/user/{id}")
    public Result deleteUser(@PathVariable("id") String id) {
        if (adminUserId.equals(id)) {
            return Result.fail("超级管理员不允许删除", null);
        }
        userService.deleteById(id);
        return Result.SUCCESS;
    }

    /** 登录 */
    @PostMapping("/user/login")
    public Result login(@RequestBody User entity) {
        String token = userService.login(entity);
        return Result.success("登录成功", token);
    }

    /** 登出 */
    @DeleteMapping("/user/logout")
    public Result logout() {
        userService.logout();
        return Result.success(null, "退出成功");
    }


    /**
     * ******************************************************* 点赞 ********************************************************
     */

    /** 列表 */
    @RequiresRole("ADMIN")
    @GetMapping("/vote/page")
    public Result votePage(//
                           @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                           @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "id");
        Page<Vote> page = voteService.findPageByKeyword(keyword, pageable);
        return Result.success(page);
    }

    /**
     * ******************************************************* 评论 ********************************************************
     */

    /** 列表 */
    @RequiresRole("ADMIN")
    @GetMapping("/comment/page")
    public Result commentPage(//
                              @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                              @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize){
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "id");
        Page<Comment> page = commentService.findPageByKeyword(keyword, pageable);
        return Result.success(page);
    }

    /** 保存 */
    @RequiresRole("ADMIN")
    @PostMapping("/comment")
    @Transactional(rollbackFor = Exception.class)
    public Result saveComment(@RequestBody Comment entity){
        entity.setId(IdUtil.generateId());
        entity.setCreateDate(new Date());
        commentService.save(entity);
        if (entity.getStatus().equals(1)) {
            blogService.updateBlogIncrCommentSize(entity.getBlogId());
        }
        return Result.SUCCESS;
    }

    /** 修改 */
    @RequiresRole("ADMIN")
    @PutMapping("/comment/status")
    public Result updateComment(@RequestBody Comment entity){
        commentService.updateCommentStatus(entity);
        return Result.SUCCESS;
    }

    /** 删除 */
    @RequiresRole("ADMIN")
    @DeleteMapping("/comment/{id}")
    public Result deleteComment(@PathVariable("id") String id){
        commentService.deleteById(id);
        return Result.SUCCESS;
    }


    /**
     * ******************************************************* 日志 ********************************************************
     */

    /** 列表 */
    @RequiresRole("ADMIN")
    @GetMapping("/log/page")
    public Result logPage(
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order
            , @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex
            , @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Pageable pageable = BlogUtil.buildPageable(pageIndex, pageSize, "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, "id");
        Page<Log> page = logService.findAll(pageable);
        return Result.success(page);
    }

    /** 列表 */
    @RequiresRole("ADMIN")
    @GetMapping("/log/delete/week/before")
    public Result deleteWeekBefore() {
        logService.deleteWeekBefore();
        return Result.SUCCESS;
    }

    /**
     * ******************************************************* 额外 ********************************************************
     */

    /**
     * @description 处理图片MD地址问题
     * @date 2019-07-20 15:34:29
     * @author: houyu for.houyu@foxmail.com
     * @return
     */
    @RequiresRole("ADMIN")
    @RequestMapping("/blog/format/image/src")
    public Result formatImageSrc(){
        List<Blog> blogList = blogRepository.findAll();
        for (Blog blog : blogList) {
            blog.setContent(BlogUtil.formatMDImageSrc(blog.getContent()));
            blog.setDisplayImg(BlogUtil.formatImageSrc(blog.getDisplayImg()));
        }
        blogRepository.saveAll(blogList);
        return Result.SUCCESS;
    }

    /**
     * @description 获取首页统计数据
     * @date 2019-07-20 15:34:29
     * @author: houyu for.houyu@foxmail.com
     * @return
     */
    @RequiresRole
    @GetMapping("/index/statistical")
    public Result statistical(){
        int voteTotal = blogService.voteTotal();                                // 累计点赞数
        int recentVote = Constants.recentVoteAtomic.get();                      // 最近点赞数
        int commentTotal = blogService.commentTotal();                          // 累计评论数
        int commentNeedVerifyTotal = commentService.commentNeedVerifyTotal();   // 待审核评论数量
        Map<String, Integer> weekAccessMap = logService.getWeekAccess();        // 日期(01/20) : 访问量(50)
        //
        Map<String, Object> dataMap = new HashMap<>(5);
        dataMap.put("voteTotal", voteTotal);
        dataMap.put("recentVote", recentVote);
        dataMap.put("commentTotal", commentTotal);
        dataMap.put("commentNeedVerifyTotal", commentNeedVerifyTotal);
        dataMap.put("weekAccessMap", weekAccessMap);
        return Result.success(dataMap);
    }
}
