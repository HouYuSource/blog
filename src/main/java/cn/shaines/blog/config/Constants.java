package cn.shaines.blog.config;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 常量
 * @date: created in 2019-07-14 10:55:31
 * @author: houyu for.houyu@foxmail.com
 */
public interface Constants {

    /** 每页最大条数 */
    int MAX_PAGE_SIZE = 50;

    String ADMIN_MAIL = "for.houyu@foxmail.com";

    /** 博客部署的地址 */
    String ADDRESS = "https://shaines.cn";
    //String ADDRESS = "http://localhost:10001";

    String PASSWORD_SALT = "shaine%s.cn";

    /** 最近点赞数 */
    AtomicInteger recentVoteAtomic = new AtomicInteger(0);

}
