package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Region;
import com.project.CmsApplication.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RegionRepository extends JpaRepository<Region, Integer> {

    @Query(value = "SELECT * FROM cms.Region WHERE " +
            "region_name like :region_name AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Region> getRegionList(@Param("region_name") String region_name,
                               @Param("company_id") String company_id,
                               @Param("status") String status,
                               @Param("created_by") String created_by,
                               @Param("created_date") String created_date,
                               @Param("updated_by") String updated_by,
                               @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Region(status,company_id,region_name,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:company_id,:region_name,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("company_id") int company_id, @Param("region_name") String region_name, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Region WHERE region_id =:region_id", nativeQuery = true)
    List<Region> getRegionById(@Param("region_id") int region_id);

    @Query(value = "SELECT * FROM cms.Region WHERE region_name like :region_name", nativeQuery = true)
    List<Region> getRegionByName(@Param("region_name") String region_name);

    @Modifying
    @Query(value = "UPDATE cms.Region SET region_name=:region_name,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE region_id =:region_id ", nativeQuery = true)
    void updateRegion(@Param("region_name") String region_name, @Param("status") String status,
                      @Param("updated_by") String updated_by, @Param("region_id") int region_id);

    @Modifying
    @Query(value = "UPDATE cms.Region SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE region_id=:region_id", nativeQuery = true)
    void deleteRegion(@Param("region_id") int region_id, @Param("updated_by") String updated_by);


}
