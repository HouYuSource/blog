package cn.shaines.blog.vo;

import cn.shaines.blog.exception.BaseExceptionHandler;
import lombok.Data;

/**
 * @description: 响应结果集
 * @date: created in 2019-07-13 15:09:31
 * @author: houyu for.houyu@foxmail.com
 */
@Data
public class Result {

    public static final String SUCCESS_MSG     = "请求成功";
    public static final String FAIL_MSG        = "请求失败";
    public static final String FREQUENTLY_MSG  = "请求频繁";
    public static final String ERROR_MSG       = "请求错误";

    public static final Result SUCCESS = new Result(Code.SUCCESS, SUCCESS_MSG, null);
    public static final Result FAIL = new Result(Code.FAIL, FAIL_MSG, null);
    public static final Result FREQUENTLY = new Result(Code.FREQUENTLY, FREQUENTLY_MSG, null);
    public static final Result ERROR = new Result(Code.ERROR, ERROR_MSG, null);

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (code != Code.SUCCESS) {
            handleErrorMsg(msg);
        }
    }

    public static Result success(Object data){
        return success(SUCCESS_MSG, data);
    }

    public static Result success(String msg, Object data){
        return new Result(Code.SUCCESS, msg, data);
    }

    public static Result fail(Object data){
        return fail(FAIL_MSG, data);
    }

    public static Result fail(String msg, Object data){
        return new Result(Code.FAIL, msg, data);
    }

    public static Result frequently(Object data){
        return frequently(FREQUENTLY_MSG, data);
    }

    public static Result frequently(String msg, Object data){
        return new Result(Code.FREQUENTLY, msg, data);
    }

    public static Result error(Object data){
        return error(ERROR_MSG, data);
    }

    public static Result error(String msg, Object data){
        return new Result(Code.ERROR, msg, data);
    }

    /**
     * 存储响应的错误信息
     * @param msg
     */
    public static void handleErrorMsg(String msg) {
        BaseExceptionHandler.putExceptionMsg(msg);
    }

    //状态码
    private int code;
    //消息
    private String msg;
    //额外的内容
    private Object data;

    public interface Code{
        // 成功
        int SUCCESS           = 200;
        // 失败
        int FAIL              = 400;
        // 请求频繁
        int FREQUENTLY        = 406;
        // 服务器内部错误
        int ERROR             = 500;
    }
}
