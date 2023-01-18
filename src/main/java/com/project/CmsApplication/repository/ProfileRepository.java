package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    @Query(value = "SELECT * from cms_2.Profile WHERE " +
            "profile_name like :profile_name AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "lower(description) like lower(:description) AND " +
            "status_profile like :status_profile AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Profile> getProfileList(
            @Param("branch_id") String branch_id,
            @Param("region_id") String region_id,
            @Param("company_id") String company_id,
            @Param("profile_name") String profile_name,
            @Param("description") String description,
            @Param("status_profile") String status_profile,
            @Param("created_by") String created_by,
            @Param("created_date") String created_date,
            @Param("updated_by") String updated_by,
            @Param("updated_date") String updated_at);


    @Query(value = "SELECT * from cms_2.Profile WHERE profile_id =:profile_id", nativeQuery = true)
    List<Profile> getProfileById(@Param("profile_id") int profile_id);

    @Query(value = "SELECT * from cms_2.Profile WHERE branch_id =:branch_id AND status not in ('deleted')", nativeQuery = true)
    List<Profile> getProfileByBranchId(@Param("branch_id") int branch_id);

    @Query(value = "SELECT * from cms_2.Profile WHERE lower(profile_name) =lower(:profile_name) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<Profile> getProfileByProfileName(@Param("profile_name") String profile_name, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.Profile WHERE lower(profile_name) =lower(:profile_name) AND profile_id not in (:profile_id) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<Profile> getProfileByTittleExceptId(@Param("profile_name") String profile_name, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("profile_id") int profile_id);


    @Modifying
    @Query(value = "UPDATE cms_2.Profile SET profile_name=:profile_name,description=:description,status_profile=:status_profile," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE profile_id =:profile_id ", nativeQuery = true)
    void updateProfile(
            @Param("profile_name") String profile_name, @Param("description") String description, @Param("status_profile") String status_profile,
            @Param("updated_by") String updated_by, @Param("profile_id") int profile_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Profile SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE profile_id=:profile_id", nativeQuery = true)
    void deleteProfile(@Param("profile_id") int profile_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * from cms_2.Profile WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Profile> getExpiredProfileId();


}
