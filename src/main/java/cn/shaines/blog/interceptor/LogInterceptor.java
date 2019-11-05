package cn.shaines.blog.interceptor;

import cn.shaines.blog.entity.Log;
import cn.shaines.blog.exception.BaseExceptionHandler;
import cn.shaines.blog.service.LogService;
import cn.shaines.blog.utils.IdUtil;
import cn.shaines.blog.utils.MvcUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @description: 日志拦截器
 * @date: created in 2019-05-08 16:24:23
 * @author: houyu
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private MvcUtil mvcUtil = MvcUtil.get();

    @Autowired
    private LogService logService;

    /**
     * 页面渲染之后调用，一般用于资源清理操作, 日志记录等
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {

        String msg = BaseExceptionHandler.getExceptionMsg();
        //
        Log log = new Log();
        log.setId(IdUtil.generateId());
        log.setIp(mvcUtil.getIpAddress(request));
        log.setIpAddress(null);
        log.setPort(request.getRemotePort());
        log.setUrl(request.getRequestURI());
        log.setParam(JSON.toJSONString(mvcUtil.parseParam(request)));
        log.setBrowser(request.getHeader("User-Agent"));
        log.setCreateDate(new Date());
        log.setStatus(StringUtils.isEmpty(msg) ? 1 : 0);
        log.setResult(StringUtils.isEmpty(msg) ? "请求成功" : msg);
        logService.save(log);
    }

}
