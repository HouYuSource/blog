package cn.shaines.blog.exception;

/**
 * @description: 业务异常
 * @date: created in 2019-07-13 15:06:14
 * @author: houyu for.houyu@foxmail.com
 */
public class BusinessException extends RuntimeException {

    public BusinessException(Exception e) {
        super(e);
    }

    public BusinessException(String msg, Exception e) {
        super(msg, e);
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
