package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: Catalog 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface CatalogRepository extends JpaRepository<Catalog, String> {


    Page<Catalog> findByUsername(String username, Pageable pageable);


}
