package com.project.CmsApplication.repository;

import com.project.CmsApplication.dto.OutputDesktopPlaylist;
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

    @Query(value = "SELECT * from cms_2.Resource WHERE " +
            "lower(resource_name) like lower(:resource_name) AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "lower(\"type\") like lower(:type) AND " +
            "thumbnail like :thumbnail AND " +
            "\"file\" like :file AND " +
            "CAST(duration AS VARCHAR )like :duration AND " +
            "stretch like :stretch AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Resource> getResourceList(@Param("branch_id") String branch_id,
                                   @Param("region_id") String region_id,
                                   @Param("company_id") String company_id,
                                   @Param("resource_name") String resource_name, @Param("type") String type,
                                   @Param("thumbnail") String thumbnail, @Param("file") String file,
                                   @Param("duration") String duration, @Param("stretch") String stretch,
                                   @Param("status") String status,
                                   @Param("created_by") String created_by,
                                   @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by,
                                   @Param("updated_date") String updated_at);

//    @Modifying
//    @Query(value = "INSERT INTO cms_2.Resource(status,resource_name,\"type\",thumbnail,\"file\",duration,stretch,created_by,created_date,updated_by,updated_date,url_resource) " +
//            "VALUES('active',:resource_name,:type,:thumbnail,:file,:duration,:stretch,:created_by,current_timestamp,:created_by,current_timestamp,:url_resource)", nativeQuery = true)
//    void save(@Param("resource_name") String resource_name, @Param("type") String type,
//              @Param("thumbnail") String thumbnail, @Param("file") String file,
//              @Param("duration") int duration, @Param("stretch") String stretch,
//              @Param("created_by") String created_by, @Param("url_resource") String url_resource);

    @Query(value = "SELECT * from cms_2.Resource WHERE resource_id =:resource_id", nativeQuery = true)
    List<Resource> getResourceById(@Param("resource_id") int resource_id);

    @Query(value = "SELECT * from cms_2.Resource WHERE lower(resource_name) = lower(:resource_name) AND status not in ('deleted')", nativeQuery = true)
    List<Resource> getResourceByName(@Param("resource_name") String resource_name);

    @Query(value = "SELECT * from cms_2.Resource WHERE lower(resource_name) = lower(:resource_name) AND resource_id not in (:resource_id) AND status not in ('deleted')", nativeQuery = true)
    List<Resource> getResourceByNameExceptId(@Param("resource_name") String resource_name, @Param("resource_id") int resource_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Resource SET resource_name=:resource_name,\"type\" =:type,thumbnail = :thumbnail,\"file\"=:file," +
            "duration=:duration,stretch=:stretch,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp,url_resource=:url_resource WHERE resource_id =:resource_id ", nativeQuery = true)
    void updateResource(@Param("resource_name") String resource_name, @Param("type") String type,
                        @Param("thumbnail") String thumbnail, @Param("file") String file,
                        @Param("duration") int duration, @Param("stretch") String stretch,
                        @Param("status") String status,
                        @Param("updated_by") String updated_by, @Param("resource_id") int resource_id, @Param("url_resource") String url_resource);

    @Modifying
    @Query(value = "UPDATE cms_2.Resource SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE resource_id=:resource_id", nativeQuery = true)
    void deleteResource(@Param("resource_id") int resource_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT C.resource_id, C.type, C.file, C.url_resource " +
            ", C.duration, C.stretch, ROW_NUMBER() OVER (ORDER BY A.sort, B.resource_order) AS sequence " +
            "FROM cms_2.playlist A " +
            "LEFT JOIN cms_2.playlist_resource B ON A.playlist_id = B.playlist_id " +
            "LEFT JOIN cms_2.resource C ON B.resource_id = C.resource_id " +
            "WHERE B.position_id = :position_id " +
            "AND NOW() BETWEEN A.start_date AND A.end_date  " +
            "AND A.status = 'active' AND B.status = 'active' AND C.status = 'active';", nativeQuery = true)
    List<String> getDesktopPlayList(@Param("position_id") int position_id);


}
