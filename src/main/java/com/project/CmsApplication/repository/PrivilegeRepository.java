package com.project.CmsApplication.repository;


import com.project.CmsApplication.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {

    @Query(value = "SELECT * from cms_2.privilege where role_id = :role_id", nativeQuery = true)
    List<Privilege> getPrivilegeByRoleId(@Param("role_id") int role_id);

    @Modifying
    @Query(value = "UPDATE cms_2.privilege SET menu_name = :menu_name,updated_by=:updated_by,updated_date = current_timestamp where role_id = :role_id", nativeQuery = true)
    void updatePrivilegeMenuName(@Param("menu_name") String menu_name, @Param("updated_by") String updated_by, @Param("role_id") int role_id);

    @Modifying
    @Query(value = "INSERT INTO cms_2.privilege (status,role_id,menu_name,created_by,created_date,updated_by,updated_date) VALUES ('active',:role_id,'[]','SYSTEM',current_timestamp,'SYSTEM',current_timestamp)", nativeQuery = true)
    void insertGeneralMenuName(@Param("role_id") int role_id);
}
