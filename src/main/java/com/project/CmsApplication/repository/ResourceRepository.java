package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    @Query(value = "SELECT * FROM cms.Resource WHERE " +
            "resource_name like :resource_name AND " +
            "\"type\" like :type AND " +
            "thumbnail like :thumbnail AND " +
            "\"file\" like :file AND " +
            "duration like :duration AND " +
            "stretch like :stretch AND " +
            "\"order\" like :order AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Resource> getResourceList(@Param("resource_name") String resource_name, @Param("type") String type,
                                   @Param("thumbnail") String thumbnail, @Param("file") String file,
                                   @Param("duration") String duration, @Param("stretch") String stretch,
                                   @Param("order") String order, @Param("status") String status,
                                   @Param("created_by") String created_by,
                                   @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by,
                                   @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Resource(status,resource_name,\"type\",thumbnail,\"file\",duration,stretch,\"order\",created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:resource_name,:type,:thumbnail,:file,:duration,:stretch,:order,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("resource_name") String resource_name, @Param("type") String type,
              @Param("thumbnail") String thumbnail, @Param("file") String file,
              @Param("duration") String duration, @Param("stretch") String stretch,
              @Param("order") String order,
              @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Resource WHERE resource_id =:resource_id", nativeQuery = true)
    List<Resource> getResourceById(@Param("resource_id") int resource_id);

    @Query(value = "SELECT * FROM cms.Resource WHERE resource_name like :resource_name", nativeQuery = true)
    List<Resource> getResourceByName(@Param("resource_name") String resource_name);

    @Modifying
    @Query(value = "UPDATE cms.Resource SET resource_name=:resource_name,\"type\" =:type,thumbnail = :thumbnail,\"file\"=:file," +
            "duration=:duration,stretch=:stretch,\"order\"=:order,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE resource_id =:resource_id ", nativeQuery = true)
    void updateResource(@Param("resource_name") String resource_name, @Param("type") String type,
                        @Param("thumbnail") String thumbnail, @Param("file") String file,
                        @Param("duration") String duration, @Param("stretch") String stretch,
                        @Param("order") String order, @Param("status") String status,
                        @Param("updated_by") String updated_by, @Param("resource_id") int resource_id);

    @Modifying
    @Query(value = "UPDATE cms.Resource SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE resource_id=:resource_id", nativeQuery = true)
    void deleteResource(@Param("resource_id") int resource_id, @Param("updated_by") String updated_by);


}
