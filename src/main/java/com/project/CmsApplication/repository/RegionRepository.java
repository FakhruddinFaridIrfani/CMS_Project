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

    @Query(value = "SELECT * from cms_2.Region WHERE " +
            "lower(region_name) like lower(:region_name) AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND " +
            "CAST(region_id AS VARCHAR) like :region_id AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Region> getRegionList(@Param("region_name") String region_name,
                               @Param("company_id") String company_id,
                               @Param("region_id") String region_id,
                               @Param("status") String status,
                               @Param("created_by") String created_by,
                               @Param("created_date") String created_date,
                               @Param("updated_by") String updated_by,
                               @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms_2.Region(status,company_id,region_name,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:company_id,:region_name,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("company_id") int company_id, @Param("region_name") String region_name, @Param("created_by") String created_by);

    @Query(value = "SELECT * from cms_2.Region WHERE region_id =:region_id", nativeQuery = true)
    List<Region> getRegionById(@Param("region_id") int region_id);

    @Query(value = "SELECT * from cms_2.Region WHERE company_id =:company_id AND status not in ('deleted')", nativeQuery = true)
    List<Region> getRegionByCompanyId(@Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.Region WHERE lower(region_name) =lower(:region_name) AND status not in ('deleted') AND company_id =:company_id", nativeQuery = true)
    List<Region> getRegionByName(@Param("region_name") String region_name, @Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.Region WHERE lower(region_name) =lower(:region_name) AND region_id not in (:region_id) AND status not in ('deleted') AND company_id =:company_id", nativeQuery = true)
    List<Region> getRegionByNameExceptId(@Param("region_name") String region_name, @Param("company_id") int company_id, @Param("region_id") int region_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Region SET region_name=:region_name,company_id=:company_id,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE region_id =:region_id ", nativeQuery = true)
    void updateRegion(@Param("region_name") String region_name, @Param("company_id") int company_id, @Param("status") String status,
                      @Param("updated_by") String updated_by, @Param("region_id") int region_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Region SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE region_id=:region_id", nativeQuery = true)
    void deleteRegion(@Param("region_id") int region_id, @Param("updated_by") String updated_by);


}
