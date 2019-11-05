package cn.shaines.blog.service.impl;

import cn.shaines.blog.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * T   实体
 * ID  实体的id类型
 * J   实体的资源库实现
 *
 * @description: service基础接口实现
 * @date: created in 2019-05-02 14:36:23
 * @author: houyu
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BaseServiceImpl<T, ID, J extends JpaRepository<T, ID>> implements BaseService<T, ID> {

    @Autowired//不可以使用@Resource
    protected J baseRepository;

    public J getBaseRepository(){
        return this.baseRepository;
    }

    @Override
    public T save(T t) {
        return baseRepository.save(t);
    }

    @Override
    public void deleteAll() {
        baseRepository.deleteAll();
    }

    @Override
    public void delete(T t) {
        baseRepository.delete(t);
    }

    @Override
    public void deleteById(ID id) {
        baseRepository.deleteById(id);
    }

    @Override
    public T update(T t) {
        return baseRepository.save(t);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    @Override
    public List<T> findAll() {
        return baseRepository.findAll();
    }

    @Override
    public T findByIdAndStatus(ID id) {
        return baseRepository.findById(id).orElse(null);
    }

    @Override
    public T findById(ID id) {
        return baseRepository.findById(id).get();
    }

    @Override
    public boolean existsById(ID id) {
        return baseRepository.existsById(id);
    }
}
