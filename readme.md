## 关于blog
blog是一个开源的Java项目 ( 个人站点 / 博客 )
* 前端展示: <a href="https://shaines.cn" target="_blank">https://shaines.cn</a> [尽可能适配移动端]
* 后端管理: <a href="https://admin.shaines.cn" target="_blank">https://admin.shaines.cn</a> [尚未适配移动端]
	username: test
	password: test

### 技术栈
* springboot2.14
* spring mvc
* JPA
* vue-xuAdmin(后台管理模板) <a href="https://github.com/Nirongxu/vue-xuAdmin" target="_blank">https://github.com/Nirongxu/vue-xuAdmin</a> 
* element-ui
* layui

主要功能有如下(包括整体部署):
* 自定义权限注解实现功能权限的划分
* 自定义拦截器实现全局日志记录
* 自定义拦截器实现拦截恶意请求
* 博客的草稿保存
* 博客发布
* 评论审核
* 评论回复
* 点赞
* 访问统计
* 数据分析
* 点赞 / 评论 状态跟进,邮件异步通知
* 适配移动端( 尽可能适配, 局部尚未适配完全 )
* jar部署
* nginx https 配置 [感谢我司大佬K.]
* nginx端口转发
* 后台管理系统静态资源部署
* 解决JWT登录不失效问题


### 历史版本
* #### V1.0.0 是一个基于多用户的博客系统

1. <a href="https://shaines.cn/?details=1150323302640689152" target="_blank">博客V1.0.0版本说明</a>

* #### V2.0.0 是一个个人站点 , 界面如下
1. 前台显示
![在这里插入图片描述](https://shaines.cn/view/image?src=https://img-blog.csdnimg.cn/20191027221240616.png)
2. 管理中心展示
![在这里插入图片描述](https://shaines.cn/view/image?src=https://img-blog.csdnimg.cn/20190727154851502.png)

## 关于我
97的后端程序员Shy [ [后宇](https://shaines.cn) ], 是一个关注编程, 热爱技术的开发者, 热衷于 `网站后端开发`, `数据爬虫`, `大数据领域`。

在这里会一直记录着我成长的点点滴滴， 毕竟好记性不如烂笔头， 如果你在我的博客中有所收获， 这也将是我毕生的荣幸。

* GitHub
https://github.com/HouYuSource

* CSDN
https://blog.csdn.net/jinglongsource

* 联系我
for.houyu@foxmail.com
for.houyu@qq.com

### 特别鸣谢
* vue-xuAdmin 
是基于vue2.0全家桶 + element-ui 开发的一个后台模板，实现了无限级菜单，页面、按钮级别的权限管理
https://github.com/Nirongxu/vue-xuAdmin

---


# 配置邮箱账号
* cn.shaines.blog.utils.MailUtil
```java
private static String USERNAME = "for.houyu@foxmail.com";                          // 用户名
private static String PASSWORD = "xxxxxxxxxxxxxxxx";                               // 密码
```

# jar部署

* window部署
```java
java -jar blog-2.0.0.jar
```

* linux部署

```java
cd 到 blog-sevice.sh目录
sh blog-sevice.sh restart c
```


