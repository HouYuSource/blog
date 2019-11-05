package cn.shaines.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 用户实体
 * @date: created in 2019-05-02 11:56:54
 * @author: houyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "User")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    @Id
    private String id;

    /** unique = true,不允许数据库出现相同值, nullable = false设置不可以为空 */
    @NotEmpty(message = "账号不能为空")
    @Size(min=3, max=20)
    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Size(max=100)
    @Column(length = 100)
    private String password;

    @NotEmpty(message = "邮箱不能为空")
    @Size(max=50)
    @Email(message= "邮箱格式不对")
    @Column(nullable = false, length = 50)
    private String email;

    /** 头像图片地址 */
    @Column(length = 200)
    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Builder.Default
    private java.util.Date createDate = new Date();

    /** 角色(ROLE_ADMIN,ROLE_USER,ROLE_VISIT) */
    @Column(nullable = false, length = 100)
    private String role;

}
