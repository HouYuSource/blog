package cn.shaines.blog.service;

import cn.shaines.blog.entity.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @description: Catalog service
 * @date: created in 2019-05-02 14:53:28
 * @author: houyu
 */
public interface CatalogService extends BaseService<Catalog, String>{

    Page<Catalog> findByUsername(String username, Pageable pageable);

    /**
     * new Sort(Sort.Direction.ASC, "catalogName")
     * @param sort
     * @return
     */
    List<Catalog> findList(Sort sort);
}
