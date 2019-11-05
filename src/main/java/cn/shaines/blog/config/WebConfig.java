package cn.shaines.blog.config;

import cn.shaines.blog.interceptor.BaseInterceptor;
import cn.shaines.blog.interceptor.LogInterceptor;
import cn.shaines.blog.interceptor.RoleInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

import java.text.SimpleDateFormat;

/**
 * @description: MVC配置
 * @date: created in 2019-05-03 18:37:47
 * @author: houyu
 */
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    BaseInterceptor baseInterceptor;
    @Autowired
    LogInterceptor logInterceptor;
    @Autowired
    RoleInterceptor roleInterceptor;

    /**
     * 注入拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor).addPathPatterns("/**").excludePathPatterns("/admin/user/login", "/admin/user/logout").order(1001);
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns("/admin/log/**", "/static/**", "/assets/**", "/images/**", "/favicon.ico").order(1002);
        registry.addInterceptor(roleInterceptor).addPathPatterns("/admin/**").order(1003);
    }

    /**
     * 时间格式转换器,将Date类型统一转换为yyyy-MM-dd HH:mm:ss格式的字符串
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        converter.setObjectMapper(mapper);
        return converter;
    }


    /**
     * 注册CORS过滤器
     */
    @Bean
    public FilterRegistrationBean corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 是否支持安全证书
        config.setAllowCredentials(true);
        // 允许任何域名使用
        config.addAllowedOrigin("*");
        // 允许任何头
        config.addAllowedHeader("*");
        // 允许任何方法（post、get等）
        config.addAllowedMethod("*");
        // 预检请求的有效期，单位为秒
        //config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    /**
     * 配置静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/");
    }

    // /**
    //  * 配置默认启动页
    //  * 如: http://localhost:8080 自动跳转页面到/index.html
    //  */
    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/").setViewName("forward:/index.html");
    //     registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    // }


}
