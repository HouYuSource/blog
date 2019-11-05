package cn.shaines.blog.utils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @program: loading-blog
 * @description: 发送邮件工具类
 * @author: houyu
 * @create: 2018-12-10 22:51
 */
public class MailUtil {

    private static Properties PROPERTIES;                                              // 配置参数
    private static InternetAddress FROM_INTERNETADDRESS;                               // 发送者地址
    private static String USERNAME = "for.houyu@foxmail.com";                          // 用户名
    private static String PASSWORD = "baidu搜索如何获取qqmail密码填入即可";                               // 密码
    private static Session SESSION;                                                    // 发送者生成的session

    static {
        PROPERTIES = new Properties();
        PROPERTIES.put("mail.transport.protocol", "smtp");                              // 连接协议
        PROPERTIES.put("mail.smtp.host", "smtp.qq.com");                                // 主机名
        PROPERTIES.put("mail.smtp.port", 465);                                          // 端口号
        PROPERTIES.put("mail.smtp.auth", "true");
        PROPERTIES.put("mail.smtp.ssl.enable", "true");                                 // 设置是否使用ssl安全连接 ---  一般都使用
        // PROPERTIES.put("mail.debug", "true");                                        // 设置是否显示debug信息 true 会在控制台显示相关信息

        try {
            String nick =javax.mail.internet.MimeUtility.encodeText("Shy Site");
            FROM_INTERNETADDRESS = new InternetAddress(nick + " <" + "for.houyu@foxmail.com" + ">");
        } catch (AddressException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 得到回话对象
        SESSION = Session.getInstance(PROPERTIES);
    }

    /**
     * // 导入javax.mail依赖
     * compile group: 'javax.mail', name: 'mail', version: '1.4.7'
     * 详情浏览博客:https://www.cnblogs.com/xdp-gacl/p/4216311.html
     *
     * @param toInternetAddress : 邮箱
     * @param title             : 标题
     * @param data              : 内容
     */
    public static boolean sendMail(String toInternetAddress, String title, String data) {

        // 获取邮件对象
        MimeMessage message = new MimeMessage(SESSION);
        // 邮差对象
        Transport transport = null;
        try {
            // 设置发件人邮箱地址
            message.setFrom(FROM_INTERNETADDRESS);
            // 设置收件人地址
            message.setRecipients(MimeMessage.RecipientType.TO, new InternetAddress[]{new InternetAddress(toInternetAddress)});
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            message.setText(data, null, "html");
            // 得到邮差对象
            transport = SESSION.getTransport();
            // 连接自己的邮箱账户
            transport.connect(USERNAME, PASSWORD);      // 密码为刚才得到的授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());

            // System.out.println("发送成功");
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
                transport = null;
            }
        }
    }

    /**
     * 使用线程异步发送邮件
     * @param toInternetAddress
     * @param title
     * @param data
     */
    public static void asySendMail(String toInternetAddress, String title, String data) {
        ThreadPoolUtil.get().submit(() -> sendMail(toInternetAddress, title, data));
    }

    /**
     * --------------------------------------------------------------------------------------
     */
    private interface SingletonHolder {
        MailUtil INSTANCE = new MailUtil();
    }

    public static MailUtil get() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * --------------------------------------------------------------------------------------
     */

    public static void main(String[] args) throws Exception {
        MailUtil.asySendMail("272694308@qq.com","来自SHY BLOG的邮件", "<p style='color: red'>你好呀2!!</p>");
    }
}
