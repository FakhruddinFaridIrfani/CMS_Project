package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    @Query(value = "SELECT * FROM cms.user_role " +
            "WHERE CAST(user_id AS VARCHAR) like :user_id " +
            "AND CAST(role_id AS VARCHAR) like :role_id " +
            "AND status like :status " +
            "AND status not in('deleted') " +
            "AND lower(created_by) like lower(:created_by) " +
            "AND CAST(created_date AS VARCHAR) like :created_date " +
            "AND lower(updated_by) like lower(:updated_by) " +
            "AND CAST(updated_date AS VARCHAR) like :updated_date", nativeQuery = true)
    List<UserRole> getUserRoleList(@Param("user_id") String user_id,
                                   @Param("role_id") String role_id,
                                   @Param("status") String status,
                                   @Param("created_by") String created_by,
                                   @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by,
                                   @Param("updated_date") String updated_date);

    @Modifying
    @Query(value = "INSERT INTO cms.user_role(status,user_id,role_id,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:user_id,:role_id,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("user_id") int user_id, @Param("role_id") int role_id, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.user_role WHERE user_id =:user_id and status not int ('deleted')", nativeQuery = true)
    List<UserRole> getUserRoleByUserId(@Param("user_id") int user_id);

    @Query(value = "SELECT * FROM cms.user_role WHERE role_id =:role_id and status not in ('deleted')", nativeQuery = true)
    List<UserRole> getUserRoleByRoleId(@Param("role_id") int role_id);

    @Query(value = "SELECT * FROM cms.user_role WHERE user_role_name like :user_role_name", nativeQuery = true)
    List<UserRole> getUserRoleByName(@Param("user_role_name") String user_role_name);

    @Modifying
    @Query(value = "UPDATE cms.user_role SET role_id=:role_id,status =:status,updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE user_role_id=:user_role_id", nativeQuery = true)
    void updateUserRole(@Param("role_id") int role_id, @Param("status") String status,
                        @Param("updated_by") String updated_by, @Param("user_role_id") int user_role_id);

    @Modifying
    @Query(value = "UPDATE cms.user_role SET status = 'deleted',updated_by=:updated_by,updated_date=current_timestamp WHERE user_role_id=:user_role_id", nativeQuery = true)
    void deleteUserRole(@Param("user_role_id") int user_role_id, @Param("updated_by") String updated_by);

    @Modifying
    @Query(value = "UPDATE cms.user_role SET status = 'deleted',updated_by=:updated_by,updated_date=current_timestamp WHERE user_id=:user_id", nativeQuery = true)
    void deleteUserRoleByUserId(@Param("user_id") int user_id, @Param("updated_by") String updated_by);
}
