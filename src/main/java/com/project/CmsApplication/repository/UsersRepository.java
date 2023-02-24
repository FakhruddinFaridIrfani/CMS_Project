package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Users;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Integer> {

    @Query(value = "SELECT * from cms_2.Users WHERE " +
            "lower(user_name) like lower(:user_name) " +
            "AND CAST(branch_id AS VARCHAR) like :branch_id " +
            "AND CAST(region_id AS VARCHAR) like :region_id " +
            "AND CAST(company_id AS VARCHAR) like :company_id " +
            "AND CAST(role_id AS VARCHAR) like :role_id " +
            "AND lower(user_email) like lower(:user_email) " +
            "AND status like :status " +
            "AND status not in('deleted') " +
            "AND lower(user_full_name) like lower(:user_full_name) " +
            "AND lower(created_by) like lower(:created_by) " +
            "AND CAST(created_date AS VARCHAR) like :created_date " +
            "AND lower(updated_by) like lower(:updated_by) " +
            "AND CAST(updated_date AS VARCHAR) like :updated_date ORDER BY branch_id ASC", nativeQuery = true)
    List<Users> getUsersList(@Param("user_name") String user_name, @Param("user_email") String user_email,
                             @Param("status") String status, @Param("user_full_name") String user_full_name, @Param("created_by") String created_by,
                             @Param("created_date") String created_date, @Param("updated_by") String updated_by,
                             @Param("updated_date") String updated_at, @Param("branch_id") String branch_id, @Param("region_id") String region_id, @Param("company_id") String company_id, @Param("role_id") String role_id);

    @Modifying
    @Query(value = "INSERT INTO cms_2.Users(user_name,user_password,user_email,status,user_full_name,created_by,created_date,updated_by,updated_date,user_token,branch_id,region_id,company_id,role_id) " +
            "VALUES(:user_name,crypt(:user_password, gen_salt('bf')),:user_email,'active',:user_full_name,:created_by,current_timestamp,:created_by,current_timestamp,:user_token,:branch_id,:region_id,:company_id,:role_id)", nativeQuery = true)
    void save(@Param("user_name") String user_name, @Param("user_password") String user_password,
              @Param("user_email") String user_email, @Param("user_full_name") String user_full_name, @Param("created_by") String created_by,
              @Param("user_token") String user_token, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("role_id") int role_id);

    @Query(value = "SELECT * from cms_2.Users WHERE user_id =:user_id", nativeQuery = true)
    List<Users> getUsersById(@Param("user_id") int user_id);

    @Query(value = "SELECT * from cms_2.Users WHERE role_id =:role_id and status <>'deleted'", nativeQuery = true)
    List<Users> getUserByRoleId(@Param("role_id") int role_id);


    @Query(value = "SELECT * from cms_2.Users WHERE branch_id =:branch_id AND status not in('deleted')", nativeQuery = true)
    List<Users> getUsersByBranchId(@Param("branch_id") int branch_id);


    @Query(value = "SELECT * from cms_2.Users WHERE lower(user_name) = lower(:user_name) AND status not in('deleted')", nativeQuery = true)
    List<Users> getUsersByName(@Param("user_name") String user_name);

    @Query(value = "SELECT * from cms_2.Users WHERE lower(user_name) = lower(:user_name) AND user_id not in(:user_id) AND status not in('deleted')", nativeQuery = true)
    List<Users> getUsersByNameExceptId(@Param("user_name") String user_name, @Param("user_id") int user_id);

    @Query(value = "SELECT * from cms_2.Users WHERE user_token =:user_token", nativeQuery = true)
    List<Users> tokenAuth(@Param("user_token") String user_token);

    @Modifying
    @Query(value = "UPDATE cms_2.Users SET user_email=:user_email," +
            "status=:status,user_full_name=:user_full_name,branch_id=:branch_id,region_id=:region_id,company_id=:company_id,role_id=:role_id,updated_by=:updated_by,updated_date=current_timestamp WHERE user_id =:user_id ", nativeQuery = true)
    void updateUser(@Param("user_email") String user_email,
                    @Param("status") String status, @Param("user_full_name") String user_full_name,
                    @Param("updated_by") String updated_by, @Param("branch_id") int branch_id,
                    @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("role_id") int role_id, @Param("user_id") int user_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Users SET status = 'deleted',updated_by=:updated_by,updated_date=current_timestamp WHERE user_id=:user_id", nativeQuery = true)
    void deleteUser(@Param("user_id") int user_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * from cms_2.Users " +
            "where (user_name =:user_name OR user_email=:user_email) AND user_password = crypt(:user_password, user_password) and status = 'active'", nativeQuery = true)
    List<Users> loginUser(@Param("user_name") String user_name, @Param("user_email") String user_email, @Param("user_password") String user_password);

    @Modifying
    @Query(value = "UPDATE cms_2.Users SET user_password = crypt(:user_password, gen_salt('bf')) WHERE user_id = :user_id ", nativeQuery = true)
    void changeUsersPassword(@Param("user_id") int user_id, @Param("user_password") String newPassword);

    @Modifying
    @Query(value = "UPDATE cms_2.Users SET user_password = crypt('password', gen_salt('bf')) WHERE user_id = :user_id ", nativeQuery = true)
    void forgetUsersPassword(@Param("user_id") int user_id);

}
