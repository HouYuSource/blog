package cn.shaines.blog.exception;

import cn.shaines.blog.utils.HttpContextUtils;
import cn.shaines.blog.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Map;

/**
 * @description: 统一异常处理类
 * @date: created in 2019-05-08 15:57:43
 * @author: houyu
 */
@ControllerAdvice
public class BaseExceptionHandler {

    private static final Map<String, String> EXCEPTION_MAP = new java.util.concurrent.ConcurrentHashMap<>(32);
    private static final Logger logger = LoggerFactory.getLogger(BaseExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handleException(Exception e) {
        //
        logger.warn("全局捕获异常", e);
        putExceptionMsg(e.getMessage());
        if (e instanceof BusinessException){
            // 业务异常直接反馈给用户
            return Result.fail(e.getMessage(), null);
        }
        if (e instanceof ConstraintViolationException) {
            String msg = ((ConstraintViolationException) e).getConstraintViolations().iterator().next().getMessage();
            return Result.fail(msg, null);
        }
        // 未知异常返回统一报错信息
        return Result.error("系统发生未知错误，请联系管理员", null);
    }

    /**
     * 记录日志需要
     */
    public static void putExceptionMsg(String msg){
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request != null && msg != null) {
            EXCEPTION_MAP.put(String.valueOf(request.toString()), msg);
        }
    }

    /**
     * 记录日志需要
     */
    public static String getExceptionMsg(){
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request != null) {
            return EXCEPTION_MAP.remove(String.valueOf(request.toString()));
        }
        return "";
    }
}
