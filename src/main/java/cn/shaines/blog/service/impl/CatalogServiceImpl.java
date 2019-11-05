package cn.shaines.blog.service.impl;

import cn.shaines.blog.entity.Catalog;
import cn.shaines.blog.repository.CatalogRepository;
import cn.shaines.blog.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: Catalog service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class CatalogServiceImpl extends BaseServiceImpl<Catalog, String, CatalogRepository> implements CatalogService {

    @Override
    public Page<Catalog> findByUsername(String username, Pageable pageable) {
        return this.baseRepository.findByUsername(username, pageable);
    }

    @Override
    public List<Catalog> findList(Sort sort) {
        return this.baseRepository.findAll(sort);
    }
}
