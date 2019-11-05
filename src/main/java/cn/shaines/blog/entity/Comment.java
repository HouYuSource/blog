package cn.shaines.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 评论实体
 * @date: created in 2019-05-01 22:04:45
 * @author: houyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "Comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {

    @Id
    private String id;

    /** 评论者的昵称 */
    @NotEmpty(message = "昵称不能为空")
    @Size(max=50)
    @Column(nullable = false, length = 10)
    private String nickname;

    /** 评论者的邮箱 */
    @NotEmpty(message = "邮箱不能为空")
    @Size(max=50)
    @Email(message= "邮箱格式不对")
    @Column(nullable = false, length = 50)
    private String email;

    /** 博客Id */
    @NotNull(message = "博客不能为空")
    private String blogId;

    /** 博客标题 */
    @NotNull(message = "博客不能为空")
    private String blogTitle;

    @NotEmpty(message = "评论内容不能为空")
    @Size(min=2, max=500)
    @Column(nullable = false)
    private String content;

    /** 回复评论者的邮箱 */
    @Size(max=50)
    @Email(message= "邮箱格式不对")
    @Column(nullable = false, length = 50)
    private String toEmail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Builder.Default
    private java.util.Date createDate = new Date();

    /** 0:不显示(未审核) 1:显示(审核通过) */
    @Builder.Default
    private Integer status = 0;

}
