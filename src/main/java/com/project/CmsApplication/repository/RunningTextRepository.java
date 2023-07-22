package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.RunningText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RunningTextRepository extends JpaRepository<RunningText, Integer> {

    @Query(value = "SELECT * from cms_2.running_text WHERE " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "lower(tittle) like lower(:tittle) AND " +
            "lower(description) like lower(:description) AND " +
            "lower(running_text) like lower(:running_text) AND " +
            "CAST(start_date AS VARCHAR) like :start_date AND " +
            "CAST(end_date AS VARCHAR) like :end_date AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<RunningText> getRunningTextList(
            @Param("branch_id") String branch_id,
            @Param("region_id") String region_id,
            @Param("company_id") String company_id,
            @Param("tittle") String tittle,
            @Param("description") String description,
            @Param("running_text") String running_text,
            @Param("start_date") String start_date,
            @Param("end_date") String end_date,
            @Param("status") String status,
            @Param("created_by") String created_by,
            @Param("created_date") String created_date,
            @Param("updated_by") String updated_by,
            @Param("updated_date") String updated_at);

    @Query(value = "SELECT * from cms_2.running_text WHERE " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "status not in ('deleted','inactive') AND CURRENT_TIMESTAMP BETWEEN start_date and end_date  ORDER BY updated_date DESC", nativeQuery = true)
    List<RunningText> getRunningTextAndroid(@Param("branch_id") String branch_id,
                                      @Param("region_id") String region_id,
                                      @Param("company_id") String company_id);

//    @Modifying
//    @Query(value = "INSERT INTO cms_2.running_text(status,branch_id,region_id,company_id,tittle,description,running_text,start_date,end_date,created_by,created_date,updated_by,updated_date) " +
//            "VALUES('active',:branch_id,:region_id,:company_id,:tittle,:description,:running_text,CAST(:start_date AS timestamp),CAST(:end_date AS timestamp),:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
//    void save(@Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id,
//              @Param("tittle") String tittle,
//              @Param("description") String description, @Param("running_text") String running_text,
//              @Param("start_date") String start_date, @Param("end_date") String end_date,
//              @Param("created_by") String created_by);

    @Query(value = "SELECT * from cms_2.running_text WHERE running_text_id =:running_text_id", nativeQuery = true)
    List<RunningText> getRunningTextById(@Param("running_text_id") int running_text_id);

    @Query(value = "SELECT * from cms_2.running_text WHERE branch_id =:branch_id AND status not in ('deleted')", nativeQuery = true)
    List<RunningText> getRunningTextByBranchId(@Param("branch_id") int branch_id);

    @Query(value = "SELECT * from cms_2.running_text WHERE lower(tittle) =lower(:tittle) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<RunningText> getRunningTextByTittle(@Param("tittle") String tittle, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.running_text WHERE lower(tittle) =lower(:tittle) AND running_text_id not in (:running_text_id) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<RunningText> getRunningTextByTittleExceptId(@Param("tittle") String tittle, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("running_text_id") int running_text_id);


    @Modifying
    @Query(value = "UPDATE cms_2.running_text SET branch_id=:branch_id,region_id=:region_id,company_id=:company_id,tittle=:tittle,description=:description,running_text=:running_text," +
            "start_date=CAST(:start_date AS timestamp),end_date =CAST(:end_date AS timestamp) ,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE running_text_id =:running_text_id ", nativeQuery = true)
    void updateRunningText(@Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id,
                           @Param("tittle") String tittle, @Param("description") String description,
                           @Param("running_text") String running_text,
                           @Param("start_date") String start_date, @Param("end_date") String end_date, @Param("status") String status,
                           @Param("updated_by") String updated_by, @Param("running_text_id") int running_text_id);

    @Modifying
    @Query(value = "UPDATE cms_2.running_text SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE running_text_id=:running_text_id", nativeQuery = true)
    void deleteRunningText(@Param("running_text_id") int running_text_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * from cms_2.running_text WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<RunningText> getExpiredRunningTextId();


}
