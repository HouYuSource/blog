package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @description: VisitLog 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface LogRepository extends JpaRepository<Log, String> {

    @Query(nativeQuery=true, value = "SELECT DATE_FORMAT(l.create_date, '%m/%d') AS date, SUM(1) AS sum FROM log l WHERE DATE_FORMAT(l.create_date, '%Y-%m-%d') >= :startDateStr AND DATE_FORMAT(l.create_date, '%Y-%m-%d') <= :endDateStr GROUP BY date ORDER BY date ASC")
    List<Map<String, Object>> findAccessByDateBetween(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM log WHERE DATE_FORMAT(create_date, '%Y-%m-%d') <= :endDataStr")
    int deleteToEndData(@Param("endDataStr") String endDataStr);
}
