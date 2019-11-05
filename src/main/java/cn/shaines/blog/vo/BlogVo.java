package cn.shaines.blog.vo;

public class BlogVo {

    public BlogVo() {
    }

    public BlogVo(Long id, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }

    private Long id;

    private String avatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
