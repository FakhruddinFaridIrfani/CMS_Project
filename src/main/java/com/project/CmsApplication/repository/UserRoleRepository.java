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

    @Query(value = "SELECT * FROM cms.user_role WHERE " +
            "user_role_name like :user_role_name " +
            "AND user_role_desc like :user_role_desc " +
            "AND created_by like :created_by " +
            "AND  CAST(created_date AS VARCHAR) like :created_date " +
            "AND updated_by like :updated_by " +
            "AND CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<UserRole> getUserRoleList(@Param("user_role_name") String user_role_name,
                                   @Param("user_role_desc") String desc, @Param("created_by") String created_by,
                                   @Param("created_date") String created_at, @Param("updated_by") String updated_by,
                                   @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.user_role(user_role_name,user_role_desc,created_by,created_date,updated_by,updated_date) " +
            "VALUES(:role_name,:role_desc,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("role_name") String role_name, @Param("role_desc") String role_desc, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.user_role WHERE user_role_id =:user_role_id", nativeQuery = true)
    List<UserRole> getUserRoleById(@Param("user_role_id") int user_role_id);

    @Query(value = "SELECT * FROM cms.user_role WHERE user_role_name like :user_role_name", nativeQuery = true)
    List<UserRole> getUserRoleByName(@Param("user_role_name") String user_role_name);

    @Query(value = "UPDATE cms.user_role SET user_role_name=:user_role_name,user_role_desc ",nativeQuery = true)
    void updateUserRole();
}
