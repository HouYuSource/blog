package cn.shaines.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

/**
 * @description: service基础接口
 * @date: created in 2019-05-02 14:53:03
 * @author: houyu
 */
public interface BaseService<T, ID> {

    /**
     * 增加单个数据
     *
     * @param t
     * @return
     */
    T save(T t);

    /**
     * 删除所有数据
     */
    void deleteAll();

    /**
     * 根据对象删除
     *
     * @param t
     */
    void delete(T t);

    /**
     * 根据id删除单个对象
     *
     * @param id
     */
    void deleteById(ID id);

    /**
     * 修改单个数据
     *
     * @param t
     */
    T update(T t);

    /**
     * 分页查询
     *
     * @param pageable
     * @return
     */
    Page<T> findAll(@PageableDefault Pageable pageable);

    /**
     * 查询所有数据
     *
     * @return
     */
    List<T> findAll();

    /**
     * 根据id查找单个数据
     *
     * @param id
     * @return
     */
    T findByIdAndStatus(ID id);


    /**
     * 根据id查找单个数据
     *
     * @param id
     * @return
     */
    T findById(ID id);

    /**
     * 判断是否存在
     *
     * @return
     */
    boolean existsById(ID id);


}
