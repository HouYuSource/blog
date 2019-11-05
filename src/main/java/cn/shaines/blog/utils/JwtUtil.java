package cn.shaines.blog.utils;

import cn.shaines.blog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by houyu on 2018/4/11.
 */
@Component
public class JwtUtil {

    public static final String TOKEN_PREFIX = "Bearer ";        // token前缀
    public static final String TOKEN_HEADER_AUTHORIZATION = "Authorization";  // 请求头
    public static final String ROLE = "role";  // 权限key
    private static final Map<String, User> CURRENT_USER_MAP = new ConcurrentHashMap<>(8);
    public static final User NULL_USER = null;

    private String key = "shy_site";

    // 过期时间是3600秒，既是1个小时
    private long ttl = 3600 * 1000;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * 生成JWT
     *
     * @param id 用户ID
     * @param subject 用户名称
     * @param role 用户角色
     * @return
     */
    public String createJWT(String id, String subject, String role) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setId(id)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key).claim(ROLE, role);
        if (ttl > 0) {
            builder.setExpiration(new Date(nowMillis + ttl));
        }
        return builder.compact();
    }

    /**
     * 解析JWT
     * @param token
     * @return
     */
    public Claims parseJWT(String token) {
        Claims claims;                          // 可以看成一个Map,但是这个Map封装了个别key而已
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
            claims = new DefaultClaims();       // 减少空指针
        }
        return claims;
    }

    public Claims getClaims(String authorization){
        return authorization == null ? new DefaultClaims() : parseJWT(authorization.replaceFirst(TOKEN_PREFIX, ""));
    }

    public String getId(String authorization){
        Object id = authorization == null ? "" : parseJWT(authorization.replaceFirst(TOKEN_PREFIX, "")).getId();
        return id == null ? "" : (String) id;
    }

    public String getRole(String authorization){
        Object role = authorization == null ? "" : parseJWT(authorization.replaceFirst(TOKEN_PREFIX, "")).get(ROLE);
        return role == null ? "" : (String) role;
    }

    public User getUser(String id){
        if (id != null) {
            return CURRENT_USER_MAP.get(id);
        }
        return NULL_USER;
    }

    public User login(User user) {
        if (user != null && user.getId() != null) {
            CURRENT_USER_MAP.put(user.getId(), user);
        }
        return user;
    }

    public void logout(String id) {
        if (id != null) {
            CURRENT_USER_MAP.remove(id);
        }
    }

}
