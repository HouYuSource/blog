package cn.shaines.blog.service;

import cn.shaines.blog.entity.Log;

import java.util.Map;

/**
 * @description: Log service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface LogService extends BaseService<Log, String>{

    Map<String, Integer> getWeekAccess();

    int deleteWeekBefore();
}
