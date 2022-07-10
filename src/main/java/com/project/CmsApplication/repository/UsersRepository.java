package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Users;
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

    @Query(value = "SELECT * FROM cms.Users WHERE " +
            "user_name like :user_name " +
            "AND user_email like :user_email " +
            "AND CAST(user_role_id AS VARCHAR) like :user_role_id " +
            "AND user_status like :user_status " +
            "AND created_by like :created_by " +
            "AND  CAST(created_date AS VARCHAR) like :created_date " +
            "AND updated_by like :updated_by " +
            "AND CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Users> getUsersList(@Param("user_name") String user_name, @Param("user_email") String user_email,
                             @Param("user_role_id") String user_role_id, @Param("user_status") String user_status, @Param("created_by") String created_by,
                             @Param("created_date") String created_date, @Param("updated_by") String updated_by,
                             @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Users(user_name,user_password,user_email,user_role_id,user_status,created_by,created_date,updated_by,updated_date,user_token) " +
            "VALUES(:user_name,crypt(:user_password, gen_salt('bf')),:user_email,:user_role_id,'active',:created_by,current_timestamp,:created_by,current_timestamp,:user_token)", nativeQuery = true)
    void save(@Param("user_name") String user_name, @Param("user_password") String user_password,
              @Param("user_email") String user_email, @Param("user_role_id") int user_role_id, @Param("created_by") String created_by, @Param("user_token") String user_token);

    @Query(value = "SELECT * FROM cms.Users WHERE user_id =:user_id", nativeQuery = true)
    List<Users> getUsersById(@Param("user_id") int user_id);

    @Query(value = "SELECT * FROM cms.Users WHERE user_name like :user_name", nativeQuery = true)
    List<Users> getUsersByName(@Param("user_name") String user_name);

    @Query(value = "SELECT * FROM cms.Users WHERE user_token =:user_token", nativeQuery = true)
    List<Users> tokenAuth(@Param("user_token") String user_token);

//    @Query(value = "UPDATE cms.Users SET user_name=:user_name,user_email=:user_email,user_role_id=:user_role_id," +
//            "user_status=:user_status,updated_by=:updated_by,updated_date=current_timestamp WHERE user_id =:user-id ", nativeQuery = true)
//    void updateUsers(@Param("user_name")String user_name,@Param("user_email")String user_email,
//                     @Param("user_role_id") int user_role_id,@Param("user_status")String user_status,
//                     @Param("updated_by")String updated_by,@Param("user_id")int user_id);


}
