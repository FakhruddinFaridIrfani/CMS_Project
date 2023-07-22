package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

//    @Modifying
//    @Query(value = "INSERT INTO cms_2.Configuration(configuration_name,configuration_value) " +
//            "VALUES(:configuration_name,:configuration_value)", nativeQuery = true)
//    void save(@Param("configuration_name") String configuration_name, @Param("configuration_value") String configuration_value);

    @Modifying
    @Query(value = "UPDATE cms_2.Configuration SET configuration_value=:configuration_value" +
            " WHERE configuration_id =:configuration_id ", nativeQuery = true)
    void updateConfiguration(@Param("configuration_value") String configuration_value,
                     @Param("configuration_id") int configuration_id);


}
