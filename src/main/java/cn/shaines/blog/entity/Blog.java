package cn.shaines.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 博客实体
 * @date: created in 2019-05-01 23:51:15
 * @author: houyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "Blog")
@EntityListeners(AuditingEntityListener.class)
public class Blog implements Serializable {

    @Id
    private String id;

    @NotNull(message = "用户不可以为空")
    private String userId;

    @Size(min = 2, max = 18, message = "用户名应设为2至18位")
    @Column(length = 20)
    private String username;

    @NotEmpty(message = "标题不能为空")
    @Size(min=2, max=50)
    @Column(nullable = false, length = 50)
    private String title;

    @NotEmpty(message = "摘要不能为空")
    @Size(min=2, max=300)
    @Column(nullable = false)
    private String summary;

    @Lob
    @NotEmpty(message = "内容不能为空")
    @Size(min=2)
    @Column(nullable = false)
    private String content;

    /**
     * 将 md 转为 html
     */
    @Lob
    //@NotEmpty(message = "内容不能为空")
    //@Size(min=2)
    //@Column(nullable = false)
    private String htmlContent;

    @Column(length = 150)
    private String displayImg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Builder.Default
    private java.util.Date createDate = new Date();

    /** 阅读量 */
    @Builder.Default
    private Integer readSize = 0;

    /** 评论量 */
    @Builder.Default
    private Integer commentSize = 0;

    /** 点赞量 */
    @Builder.Default
    private Integer voteSize = 0;

    /** 分类Id */
    private String catalogId;

    /** 分类名称 */
    @NotEmpty(message = "分类名称不能为空")
    @Size(min=2, max=18)
    @Column(nullable = false, length = 20)
    private String catalogName;

    /** 标签1,标签2,标签3 */
    @Column(length = 100)
    private String tags;

    /** 0:不显示(草稿) 1:显示(发布) */
    @Builder.Default
    private Integer status = 1;

    /** 是否可以评论  0:不可以评论  1:可以评论 */
    @Builder.Default
    private Integer canComment = 1;

}
