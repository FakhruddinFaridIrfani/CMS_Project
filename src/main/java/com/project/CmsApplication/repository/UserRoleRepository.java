package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    @Query(value = "DECLARE @sql nvarchar(max)" +
            "set @sql = '' " +
            "set @sql = 'SELECT * FROM cms.user_role WHERE 1=1'" +
            "if(len(@user_role_id)>1)" +
            "begin" +
            "set @sql = @sql + 'and user_role_id = @user_role_id'" +
            "end" +
            "if(len(@user_role_name)>1)" +
            "begin" +
            "set @sql = @sql +' and user_role name like %@user_role_name%'" +
            "end" +
            "if(len(@user_role_desc)>1)" +
            "begin" +
            "set @sql = @sql +' and user_role_desc like %@user_role_desc%'" +
            "end" +
            "exec(@sql)",nativeQuery = true)
    List<UserRole> getUserRoleList(@Param("user_role_id") String user_role_id,@Param("user_role_name") String user_role_name,@Param("user_role_desc") String desc);


}
