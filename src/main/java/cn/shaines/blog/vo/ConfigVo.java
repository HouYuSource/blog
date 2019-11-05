package cn.shaines.blog.vo;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author houyu
 * @createTime 2019/9/26 15:22
 */
@Getter
public class ConfigVo extends HashMap<String, String> {

    private String userName;
    private String userDescription;
    private String userExplain;
    private String userMotto;
    private String userQq;
    private String userWx;
    private String userGithub;
    private String userMail;
    private String userCsdn;
    private String userIco;

    private String indexAbout;
    private String indexAchievements;
    private String indexLink;

    public ConfigVo(Map<String, String> properties) {
        this.userName           = properties.get("user.name");
        this.userDescription    = properties.get("user.description");
        this.userMotto          = properties.get("user.motto");
        this.userQq = properties.get("user.qq");
        this.userWx = properties.get("user.wx");
        this.userGithub = properties.get("user.github");
        this.userMail = properties.get("user.mail");
        this.userCsdn = properties.get("user.csdn");
        this.userIco = properties.get("user.ico");
        this.userExplain = properties.get("user.explain");
        this.indexAbout = properties.get("index.about");
        this.indexAchievements = properties.get("index.achievements");
        this.indexLink = properties.get("index.link");
        //
        this.put("userName", userName);
        this.put("userDescription", userDescription);
        this.put("userMotto", userMotto);
        this.put("userQq", userQq);
        this.put("userWx", userWx);
        this.put("userGithub", userGithub);
        this.put("userMail", userMail);
        this.put("userCsdn", userCsdn);
        this.put("userIco", userIco);
        this.put("userExplain", userExplain);
        this.put("indexAbout", indexAbout);
        this.put("indexAchievements", indexAchievements);
        this.put("indexLink", indexLink);
    }

}
