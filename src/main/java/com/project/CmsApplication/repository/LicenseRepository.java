package com.project.CmsApplication.repository;


import com.project.CmsApplication.model.LicenseKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
@Transactional
public interface LicenseRepository extends JpaRepository<LicenseKey, Integer> {

    @Query(value = "SELECT license_key from cms_2.license where license_key not in (SELECT license_key from cms_2.device where status <> 'deleted' ) limit 1", nativeQuery = true)
    List<String> getAvailableLicense();

}
