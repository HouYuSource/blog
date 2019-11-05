package cn.shaines.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 浏览日志实体
 * @date: created in 2019-05-02 11:57:03
 * @author: houyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "Log")
@EntityListeners(AuditingEntityListener.class)
public class Log implements Serializable {

    @Id
    private String id;

    @Column(length = 30)
    private String ip;

    /** IP地址 */
    private String ipAddress;

    private Integer port;

    private String url;

    @Column(length = 500)
    private String param;

    /** 用户浏览器 */
    private String browser;

    /** 处理状态: 0(处理失败)   1(处理成功)   2(不处理:频繁请求) */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Builder.Default
    private Date createDate = new Date();

    @Column(length = 500)
    private String result;


}
