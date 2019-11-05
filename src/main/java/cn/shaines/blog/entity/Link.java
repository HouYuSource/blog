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
 * @description: 友链实体
 * @date: created in 2019-05-01 21:51:42
 * @author: houyu
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "link")
@EntityListeners(AuditingEntityListener.class)
public class Link implements Serializable {

    @Id
    private String id;

    @NotEmpty(message = "标题不能为空")
    @Size(min=2, max=18)
    @Column(nullable = false, length = 20)
    private String text;

    @NotEmpty(message = "链接不能为空")
    @Size(max=50)
    @Column(nullable = false, length = 50)
    private String src;

}
