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

    @Query(value = "SELECT * FROM cms.user_role" +
            "WHERE " +
            "CAST(ur.user_id AS VARCHAR) like :user_id " +
            "AND CAST(ur.role_id AS VARCHAR) like :role_id " +
            "AND ur.status like :status" +
            "AND ur.created_by like :created_by " +
            "AND CAST(ur.created_date AS VARCHAR) like :created_date " +
            "AND ur.updated_by like :updated_by " +
            "AND CAST(ur.updated_date AS VARCHAR) like :updated_date", nativeQuery = true)
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

    @Query(value = "SELECT * FROM cms.user_role WHERE user_id =:user_id and status ='active'", nativeQuery = true)
    List<UserRole> getUserRoleByUserId(@Param("user_id") int user_id);

    @Query(value = "SELECT * FROM cms.user_role WHERE user_role_name like :user_role_name", nativeQuery = true)
    List<UserRole> getUserRoleByName(@Param("user_role_name") String user_role_name);

//    @Modifying
//    @Query(value = "UPDATE cms.user_role SET user_role_name=:user_role_name,user_role_desc ", nativeQuery = true)
//    void updateUserRole();
}
