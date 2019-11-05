package cn.shaines.blog.controller;

import cn.shaines.blog.annotation.OperatorLog;
import cn.shaines.blog.annotation.RequiresRole;
import cn.shaines.blog.entity.Blog;
import cn.shaines.blog.entity.Catalog;
import cn.shaines.blog.entity.Comment;
import cn.shaines.blog.entity.Vote;
import cn.shaines.blog.service.BlogService;
import cn.shaines.blog.service.CatalogService;
import cn.shaines.blog.service.CommentService;
import cn.shaines.blog.service.VoteService;
import cn.shaines.blog.utils.BlogUtil;
import cn.shaines.blog.utils.IOUtil;
import cn.shaines.blog.utils.MvcUtil;
import cn.shaines.blog.utils.SimpleTimeCache;
import cn.shaines.blog.vo.Result;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author houyu
 * @createTime 2019/7/13 12:36
 */
@Controller
public class PublicController {

    /** 存储的是{blogId + ip, ""} */
    private SimpleTimeCache<String, String> readingCache = new SimpleTimeCache<>(32, 1000 * 15);
    /** 存储的是{email, token} */
    private SimpleTimeCache<String, String> emailTokenCache = new SimpleTimeCache<>(32, 1000 * 30);// 30秒
    //

    @Autowired
    private BlogService blogService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private VoteService voteService;
    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/")
    @OperatorLog(operate = "博客首页/")
    public String defaultRequest(@RequestParam(value = "details", required = false, defaultValue = "") String details) {
        if (!details.isEmpty()) {
            // 为了兼容旧版的URL
            return "redirect:/blog/detail/" + details;
        }
        return "forward:/blog/page/1";
    }
    @RequestMapping("/index")
    public String index() {
        return "forward:/blog/page/1";
    }

    // -------------------------------------博客---------------------------------------
    /**
     * 列表
     * @param order      排序方式(new 最新 | hot 最热)
     * @param keyword    查询关键字
     * @param pageIndex  页数
     * @param pageSize   页大小
     * @return
     */
    @GetMapping("/blog/page/{pageIndex}")
    @RequiresRole
    @OperatorLog(operate = "博客首页/index")
    public String blogPage(@PathVariable(value = "pageIndex") int pageIndex,//
            @RequestParam(value = "order", required = false, defaultValue = "new") String order,//
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,//
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,//
            @RequestParam(value="async",required = false) boolean async,
            ModelMap modelMap){
        Pageable pageable = BlogUtil.buildPageable(--pageIndex, pageSize, Sort.Direction.DESC, "new".equals(order) ? "id" : "readSize");
        Page<Blog> page = blogService.findByKeywordLike(keyword, pageable);
        Map<String,Integer> tagMapper = blogService.findTagMap();
        List<Catalog> catalogList = catalogService.findList(new Sort(Sort.Direction.ASC, "catalogName"));
        // 获取四条轮序博客
        List<Blog> hotList = blogService.findFairBlogList();
        //
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("tagMapper", tagMapper);
        modelMap.addAttribute("catalogList", catalogList);
        modelMap.addAttribute("hotList", hotList);
        modelMap.addAttribute("title", "houyu blog | 首页");
        modelMap.addAllAttributes(BlogUtil.getConfigVo());
        /*th:fragment="main-container"*/
        return async ? "lw-index :: main-container" : "lw-index";
    }
    /**
     * 详情
     */
    @GetMapping("/blog/detail/{id}")
    @Transactional(rollbackFor = Exception.class)
    @OperatorLog(operate = "博客详情")
    public String detailFullwidth(@PathVariable("id") String id, ModelMap modelMap){
        List<Blog> list = blogService.findNearList(id);
        Blog entity = list.get(1);
        String ipAddress = MvcUtil.get().getIpAddress(request);
        String key = entity.getId() + ipAddress;
        if (readingCache.get(key) == null) {
            // 不包含Key, 那就新增记录数
            blogService.updateBlogIncrReadSize(id);
        }
        readingCache.put(entity.getId() + ipAddress, "");
        //
        Page<Comment> commentPage = commentService.findByStatusAndBlogId(1, id, BlogUtil.buildPageable(0, 50, Sort.Direction.DESC, "id"));
        //
        //
        modelMap.put("data", entity);
        modelMap.put("prev", list.get(0));
        modelMap.put("next", list.get(2));
        modelMap.put("tagList", entity.getTags().split(","));
        modelMap.put("commentList", commentPage.getContent());
        //
        modelMap.put("keywords", entity.getTitle());
        modelMap.put("description", entity.getSummary());
        modelMap.put("title", "houyu blog | 文章详情");
        modelMap.addAllAttributes(BlogUtil.getConfigVo());

        return "lw-article-fullwidth";
    }

    /**
     * 时间轴
     */
    @GetMapping("/blog/timeline")
    @Transactional(rollbackFor = Exception.class)
    @OperatorLog(operate = "时间轴")
    public String timeline(ModelMap modelMap){
        Pageable pageable = BlogUtil.buildPageable(0, 300, Sort.Direction.ASC, "createDate");
        Page<Blog> page = blogService.findAll(pageable);
        List<Blog> list = page.getContent().stream().filter(v -> v.getStatus().equals(1)).collect(Collectors.toList());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        Map<String, Map<String, List<Blog>>> data = list.stream().collect(
                Collectors.groupingBy(
                        v -> yearFormat.format(v.getCreateDate()),
                        Collectors.groupingBy(
                                v -> monthFormat.format(v.getCreateDate()), () -> new TreeMap<String, List<Blog>>().descendingMap(), Collectors.toList()
                        )
                )
        );
        // 主要是逆序每一个月份的博客(使得时间最近的在前面)
        data.values().forEach(v -> v.values().forEach(Collections::reverse));
        modelMap.addAttribute("data", data);
        modelMap.put("title", "houyu blog | 归档");
        modelMap.addAllAttributes(BlogUtil.getConfigVo());
        return "lw-timeline";
    }


    // -------------------------------------分类---------------------------------------
    /**
     * 列表
     */
    @GetMapping("/catalog/list")
    @ResponseBody
    @OperatorLog(operate = "分类列表")
    public Result list() {
        List<Catalog> list = catalogService.findList(new Sort(Sort.Direction.ASC, "catalogName"));
        return Result.success(list);
    }
    /**
     * 保存
     */
    @PostMapping("/comment")
    @ResponseBody
    @OperatorLog(operate = "保存评论")
    public Result commentSave(@RequestBody Comment entity){
        commentService.save(entity, emailTokenCache);
        return Result.SUCCESS;
    }
    // -------------------------------------点赞---------------------------------------
    /**
     * 保存
     */
    @PostMapping("/vote")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    @OperatorLog(operate = "保存点赞")
    public Result save(@RequestBody Vote entity) {
        voteService.save(entity, emailTokenCache);
        return Result.SUCCESS;
    }
    //
    // -------------------------------------extra---------------------------------------
    /**
     * 生成token
     */
    @GetMapping("/generate-token")
    @ResponseBody
    @OperatorLog(operate = "生成token")
    public Result generateToken(@RequestParam String email) {
        String token = UUID.randomUUID().toString().replace("-", "");
        // 缓存一份在服务端
        emailTokenCache.put(email, token);
        return Result.success(token);
    }
    /**
     * 查看图片
     */
    @GetMapping("/view/image")
    public void viewImage(HttpServletResponse response, String src) throws IOException {
        byte[] bytes = BlogUtil.downloadImg(src);
        response.setContentType("image/png");
        // 缓存一天
        response.setDateHeader("expires", System.currentTimeMillis() + 86400000);
        IOUtil.copy(new ByteArrayInputStream(bytes), response.getOutputStream(), true);
    }


}
