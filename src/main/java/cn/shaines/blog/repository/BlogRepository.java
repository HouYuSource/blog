package cn.shaines.blog.repository;

import cn.shaines.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: blog 资源库
 * @date: created in 2019-05-02 13:44:56
 * @author: houyu
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog, String>, JpaSpecificationExecutor<Blog> {

    @Query(nativeQuery=true, value = "SELECT * FROM blog b WHERE b.status = :status AND (b.title LIKE :keyword OR b.summary LIKE :keyword OR b.tags LIKE :keyword)")
    Page<Blog> findByKeywordLikeAndStatus(@Param("keyword") String keyword, @Param("status") Integer status, Pageable pageable);

    Blog findByIdAndStatus(@Param("id") String id, @Param("status") Integer status);

    @Query(nativeQuery=true, value = "SELECT b.tags FROM blog b WHERE b.status = 1")
    List<String> findTagList();

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE blog SET blog.vote_size = blog.vote_size + 1  WHERE blog.id = :id")
    int updateBlogIncrVoteSize(@Param("id") String id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE blog SET blog.read_size = blog.read_size + 1  WHERE blog.id = :id")
    int updateBlogIncrReadSize(@Param("id") String id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE blog SET blog.comment_size = blog.comment_size + 1  WHERE blog.id = :id")
    int updateBlogIncrCommentSize(@Param("id") String id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE blog SET blog.comment_size = blog.comment_size - 1  WHERE blog.id = :id")
    int updateBlogDecrCommentSize(@Param("id") String id);


    @Query(nativeQuery=true, value = "SELECT * FROM blog b WHERE (b.title LIKE :keyword OR b.catalog_name LIKE :keyword OR b.tags LIKE :keyword)")
    Page<Blog> findAllByKeywordLike(@Param("keyword") String keyword, Pageable pageable);

    List<Blog> findAllByCatalogId(@Param("catalogId") String catalogId);

    @Query(nativeQuery=true, value = "SELECT SUM(b.vote_size) FROM blog b WHERE b.status = 1")
    int voteTotal();

    @Query(nativeQuery=true, value = "SELECT SUM(b.comment_size) FROM blog b WHERE b.status = 1")
    int commentTotal();

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE blog b SET b.catalog_name = (SELECT c.catalog_name FROM catalog c WHERE c.id = b.catalog_id)")
    int updateCatalogName();

    @Query(nativeQuery=true, value = "SELECT b.* FROM blog b WHERE b.status = :status")
    List<Blog> findAllByStatus(@Param("status") int status);

    @Query(nativeQuery=true, value = "" + //
            "(SELECT * FROM blog WHERE id < :id AND status = 1 ORDER BY create_date DESC LIMIT 1)\n" + //
            "UNION\n" + //
            "(SELECT * FROM blog WHERE id = :id AND status = 1)\n" + //
            "UNION\n" + //
            "(SELECT * FROM blog WHERE id > :id AND status = 1 ORDER BY create_date DESC LIMIT 1)") //
    List<Blog> findNearList(@Param("id") String id);
}
