package cn.shaines.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * @description: 分类实体
 * @date: created in 2019-05-01 21:51:42
 * @author: houyu
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "Catalog")
@EntityListeners(AuditingEntityListener.class)
public class Catalog implements Serializable {

    @Id
    private String id;

    @NotEmpty(message = "名称不能为空")
    @Size(min=2, max=18)
    @Column(nullable = false, length = 20)
    private String catalogName;

    private String userId;

    //@NotEmpty(message = "用户的用户名不能为空")
    //@Size(min=2, max=18)
    //@Column(nullable = false, length = 20)
    private String username;

}
