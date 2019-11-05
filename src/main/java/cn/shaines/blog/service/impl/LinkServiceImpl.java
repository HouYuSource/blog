package cn.shaines.blog.service.impl;

import cn.shaines.blog.entity.Link;
import cn.shaines.blog.repository.LinkRepository;
import cn.shaines.blog.service.LinkService;
import org.springframework.stereotype.Service;

/**
 * @description: Link service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class LinkServiceImpl extends BaseServiceImpl<Link, String, LinkRepository> implements LinkService {

}
