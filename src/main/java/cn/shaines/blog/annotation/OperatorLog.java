package cn.shaines.blog.annotation;


import java.lang.annotation.*;

/**
 * 定义操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperatorLog {
    // 操作
    String operate();

}
