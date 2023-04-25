package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.CmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CmsLogRepository extends JpaRepository<CmsLog, Integer> {


    @Query(value = "SELECT * FROM cms_2.cms_log WHERE user_name like :user_name AND company_id like :company_id " +
            "AND region_id like :region_id " +
            "AND branch_id like :branch_id " +
            "AND " +
            "case when :start_date is NULL AND :end_date is NULL then created_date IS NOT NULL else CAST(created_date AS DATE) BETWEEN CAST(:start_date AS DATE) AND CAST (:end_date AS DATE) end " +
            "ORDER BY created_date DESC", nativeQuery = true)
    List<CmsLog> getCmsLogListWithDate(@Param("user_name") String user_name, @Param("company_id") String company_id, @Param("region_id") String region_id,
                                       @Param("branch_id") String branch_id, @Param("start_date") String start_date,
                                       @Param("end_date") String end_date);

    @Query(value = "SELECT * FROM cms_2.cms_log WHERE user_name like :user_name " +
            "AND company_id like :company_id " +
            "AND region_id like :region_id " +
            "AND branch_id like :branch_id " +
            "ORDER BY created_date DESC", nativeQuery = true)
    List<CmsLog> getCmsLogListWithNoDate(@Param("user_name") String user_name, @Param("company_id") String company_id, @Param("region_id") String region_id,
                                         @Param("branch_id") String branch_id);
}
