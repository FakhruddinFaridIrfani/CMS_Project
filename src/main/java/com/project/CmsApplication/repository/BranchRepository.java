package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    @Query(value = "SELECT * FROM cms.Branch WHERE " +
            "lower(branch_name) like lower(:branch_name) AND " +
            "CAST(region_id AS VARCHAR) like :region_id AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Branch> getBranchList(@Param("branch_name") String branch_name,
                               @Param("region_id") String region_id,
                               @Param("status") String status,
                               @Param("created_by") String created_by,
                               @Param("created_date") String created_date,
                               @Param("updated_by") String updated_by,
                               @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Branch(status,region_id,branch_name,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:region_id,:branch_name,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("region_id") int region_id, @Param("branch_name") String branch_name, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Branch WHERE branch_id =:branch_id", nativeQuery = true)
    List<Branch> getBranchById(@Param("branch_id") int branch_id);

    @Query(value = "SELECT * FROM cms.Branch WHERE branch_name like :branch_name", nativeQuery = true)
    List<Branch> getBranchByName(@Param("branch_name") String branch_name);

    @Modifying
    @Query(value = "UPDATE cms.Branch SET branch_name=:branch_name,region_id=:region_id,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE branch_id =:branch_id ", nativeQuery = true)
    void updateBranch(@Param("branch_name") String branch_name,@Param("region_id") int region_id ,@Param("status") String status,
                      @Param("updated_by") String updated_by, @Param("branch_id") int branch_id);

    @Modifying
    @Query(value = "UPDATE cms.Branch SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE branch_id=:branch_id", nativeQuery = true)
    void deleteBranch(@Param("branch_id") int branch_id, @Param("updated_by") String updated_by);


}
