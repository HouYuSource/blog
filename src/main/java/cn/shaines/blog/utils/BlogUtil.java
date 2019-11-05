package cn.shaines.blog.utils;

import cn.shaines.blog.config.Constants;
import cn.shaines.blog.exception.BusinessException;
import cn.shaines.blog.utils.HttpClient.Response.BodyHandlers;
import cn.shaines.blog.vo.ConfigVo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * @author houyu
 * @createTime 2019/7/13 18:52
 */
public class BlogUtil {

    private static volatile ConfigVo CONFIG_VO = null;
    static {
        try (InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "/blog-config.properties")) {
            String configString = IOUtil.toString(inputStream, "UTF-8");
            String[] lines = configString.split("\r\n");
            Map<String, String> properties = new HashMap<>(lines.length);
            for(String line : lines) {
                if(line.contains("=")) {
                    String[] keyVal = line.split("=", 2);
                    properties.put(keyVal[0], keyVal[1]);
                }
            }
            CONFIG_VO = new ConfigVo(properties);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigVo getConfigVo() {
        return CONFIG_VO;
    }

    public static byte[] downloadImg(String src) throws IOException {
        //
        String type; // .png
        int indexOf;
        if ((indexOf = src.indexOf("?")) > -1) {
            src = src.substring(0, indexOf);
        }
        type = src.substring(src.lastIndexOf("."));
        String srcMd5 = PublicUtil.md5(src.getBytes(StandardCharsets.UTF_8));
        String path = System.getProperty("user.dir") + "/temp_blog_img";
        File file = new File(path, (srcMd5 + type).replace("/", "_"));
        if(file.exists()) {
            // 存在直接返回
            InputStream inputStream = IOUtil.toInputStream(file);
            return IOUtil.toByteArray(inputStream);
        } else {
            // 不存在就去网络请求获取一次, 并且存入文件中
            byte[] body = HttpClient.buildHttpClient().buildRequest(src).execute(BodyHandlers.ofByteArray()).getBody();
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            IOUtil.toFile(new ByteArrayInputStream(body), file);
            return body;
        }
    }

    public static Pageable buildPageable(int pageIndex,             // 当前页
                                   int pageSize,                    // 页大小
                                   Sort.Direction direction,        // 方向(Sort.Direction.DESC : Sort.Direction.ASC)
                                   String... properties) {          // 排序字段
        if (pageSize > Constants.MAX_PAGE_SIZE) {
            pageSize = Constants.MAX_PAGE_SIZE;
        }
        return PageRequest.of(pageIndex, pageSize, new Sort(direction, properties));
    }

    /**
     * 检查提交的token
     */
    public static void checkSubmitToken(SimpleTimeCache<String, String> emailTokenCache, String email, HttpServletRequest request) {
        String originalToken = emailTokenCache.get(email);
        String submitToken = request.getHeader("token");
        if (originalToken == null || !originalToken.equals(submitToken)) {
            throw new BusinessException("点赞失败: 没有token，不支持点赞");
        }
        emailTokenCache.remove(email);
    }

    public static String voteTitle = "Shy Site 点赞通知";
    public static String commentTitle = "Shy Site 评论通知";

    /**
     * 点赞通知读者
     * @param blogTitle
     * @return
     */
    public static String toReaderVoteContent(String blogTitle){
        return "感谢您的点赞, 很荣幸 \"" + blogTitle +"\" 博文引起您的共鸣, 如果您有更好的建议欢迎留言哦!!!     <br>---Shy Site.";
    }
    /**
     * 点赞通知博主
     * @param blogTitle
     * @return
     */
    public static String toBloggerVoteContent(String blogTitle, String readerEmail){
        return "您的博文 \"" + blogTitle + "\" , 收到 \"" + readerEmail +"\" 的点赞。    <br>---Shy Site.";
    }

    /**
     * 评论通知读者
     * @param blogTitle
     * @return
     */
    public static String toReaderCommentContent(String blogTitle){
        return "感谢您的评论, 很荣幸 \"" + blogTitle +"\" 博文引起你的共鸣, 您的留言已转发博主审核, 如有最新动态Shy Site会以邮件的形式通知您!!!     <br>---Shy Site.";
    }

    /**
     * 评论通知对应回复者
     * @param blogTitle
     * @return
     */
    public static String toBloggerCommentContent(String blogTitle, String readerEmail, String commentContent, String blogId){
        StringBuilder builder = new StringBuilder(256);
        builder.append("您相关的Shy Site博文 \"").append(blogTitle).append("\" 收到 \"").append(readerEmail).append("\" 的留言。")
                .append("<br><br>内容如下:<br>-------------------------------------<br>")
                .append(commentContent)
                .append("<br>-------------------------------------<br>")
                .append("<br>链接:<a href=\"https://shaines.cn/?details=").append(blogId).append("\" target=\"_blank\" title=\"").append(blogTitle).append("\">").append(blogTitle).append("</a>")
                .append("<br><br>---Shy Site.");
        return builder.toString();
    }

    /**
     * 评论展示通知
     * @param blogTitle
     * @return
     */
    public static String showReaderCommentContent(String blogTitle, String blogId){
        StringBuilder builder = new StringBuilder(256);
        builder.append("您留言Shy Site的博文 \"").append(blogTitle).append("\" 已通过博主的审核, 留言内容已展示。")
                .append("<br><br>链接:<a href=\"https://shaines.cn/?details=").append(blogId).append("\" target=\"_blank\" title=\"").append(blogTitle).append("\">").append(blogTitle).append("</a>")
                .append("<br><br>---Shy Site.");
        return builder.toString();
    }

    /**
     * 加密密码
     * @param sourcePassword 原密码(尚未机密之前的, 或者前端传递过来的)
     * @return
     */
    public static String encryptPassword(String sourcePassword) {
        if (StringUtils.isEmpty(sourcePassword)) {
            throw new BusinessException("密码不可以为空");
        }
        String pwd = String.format(Constants.PASSWORD_SALT.concat(sourcePassword), "pwd");
        String password = PublicUtil.md5(pwd.getBytes());
        return password;
    }

    private static final Pattern compile = Pattern.compile("!\\[.*?\\]\\(http.*?\\)");

    /**
     * @description 格式化md图片地址
     * @date 2019-07-20 16:12:59
     * @author: houyu for.houyu@foxmail.com
     * @param
     * @return
     */
    public static String formatMDImageSrc(String content) {
        Matcher matcher = compile.matcher(content);
        String targetMdImage;
        String oldAddress = null;
        while (matcher.find()) {
            String mdImage = matcher.group();
            if (mdImage.contains("/view/image")) {
                if (oldAddress == null) {
                    oldAddress = mdImage.substring(mdImage.indexOf("http"), mdImage.indexOf("/view/image?src="));
                }
                // 更改为当前配置域名
                targetMdImage = mdImage.replace(oldAddress, Constants.ADDRESS);
            } else {
                targetMdImage = mdImage.replace("](", "](" + Constants.ADDRESS + "/view/image?src=");
            }
            content = content.replace(mdImage, targetMdImage);
        }
        return content;
    }

    /**
     * @description 格式化图片地址
     * @date 2019-07-20 16:12:59
     * @author: houyu for.houyu@foxmail.com
     * @param
     * @return
     */
    public static String formatImageSrc(String src) {
        if (src.contains("?src=")) {
            String oldAddress = src.substring(src.indexOf("http"), src.indexOf("/view/image?src="));
            src = src.replace(oldAddress, Constants.ADDRESS);
        } else {
            src = Constants.ADDRESS + "/view/image?src=" + src;
        }
        return src;
    }

    // public static


    public static void main(String[] args) {

//        MailUtil.asySendMail("272694308@qq.com", voteTitle, toReaderVoteContent("lombok的builder设置默认值的问题"));
//        MailUtil.asySendMail("272694308@qq.com", voteTitle, toBloggerVoteContent("lombok的builder设置默认值的问题", "test@qq.com"));
//        MailUtil.asySendMail("272694308@qq.com", commentTitle, toReaderCommentContent("lombok的builder设置默认值的问题"));
//        MailUtil.asySendMail("272694308@qq.com", commentTitle, toBloggerCommentContent("lombok的builder设置默认值的问题", "test@qq.com", "写的很不错哟, 帮了很大忙!!!", "1148923384585490432"));
//        MailUtil.asySendMail("272694308@qq.com", commentTitle, replyReaderCommentContent("lombok的builder设置默认值的问题", "嗯嗯  确实如此呢, 一起进步!!!", "1148923384585490432"));

    }


}
