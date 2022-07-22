package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = "SELECT * FROM cms.Role WHERE " +
            "lower(role_name) like lower(:role_name) AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Role> getRoleList(@Param("role_name") String role_name,
                           @Param("status") String status,
                           @Param("created_by") String created_by,
                           @Param("created_date") String created_date,
                           @Param("updated_by") String updated_by,
                           @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Role(role_name,status,created_by,created_date,updated_by,updated_date) " +
            "VALUES(:role_name,'active',:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("role_name") String role_name, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Role WHERE role_id =:role_id", nativeQuery = true)
    List<Role> getRoleById(@Param("role_id") int role_id);

    @Query(value = "SELECT * FROM cms.Role WHERE lower(role_name) like lower(:role_name) AND status not in ('deleted')", nativeQuery = true)
    List<Role> getRoleByName(@Param("role_name") String role_name);

    @Query(value = "SELECT * FROM cms.Role WHERE lower(role_name) like lower(:role_name) AND role_id not in (:role_id) AND status not in ('deleted')", nativeQuery = true)
    List<Role> getRoleByNameExceptId(@Param("role_name") String role_name, @Param("role_id") int role_id);

    @Modifying
    @Query(value = "UPDATE cms.Role SET role_name=:role_name,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE role_id =:role_id ", nativeQuery = true)
    void updateRole(@Param("role_name") String role_name, @Param("status") String status,
                    @Param("updated_by") String updated_by, @Param("role_id") int role_id);

    @Modifying
    @Query(value = "UPDATE cms.Role SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE role_id=:role_id", nativeQuery = true)
    void deleteRole(@Param("role_id") int role_id, @Param("updated_by") String updated_by);


}
