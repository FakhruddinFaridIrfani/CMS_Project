package com.project.CmsApplication.repository;


import com.project.CmsApplication.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    @Query(value = "SELECT * FROM cms.company WHERE " +
            "lower(company_name) like lower(:company_name) " +
            "AND lower(company_address) like lower(:company_address) " +
            "AND company_phone like :company_phone " +
            "AND lower(company_email) like lower(:company_email) " +
            "AND status like :status " +
            "AND status not in('deleted') " +
            "AND lower(created_by) like lower(:created_by) " +
            "AND CAST(created_date AS VARCHAR) like :created_date " +
            "AND lower(updated_by) like lower(:updated_by) " +
            "AND CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Company> getCompanyList(@Param("company_name") String company_name, @Param("company_address") String company_address,
                                 @Param("company_phone") String company_phone, @Param("company_email") String company_email,
                                 @Param("status") String status, @Param("created_by") String created_by,
                                 @Param("created_date") String created_date, @Param("updated_by") String updated_by,
                                 @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.company(company_name,company_address,company_phone,company_email,status,created_by,created_date,updated_by,updated_date) " +
            "VALUES(:company_name,:company_address,:company_phone,:company_email,'active',:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("company_name") String company_name, @Param("company_address") String company_address, @Param("company_phone") String company_phone,
              @Param("company_email") String company_email, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.company WHERE company_id =:company_id", nativeQuery = true)
    List<Company> getCompanyById(@Param("company_id") int company_id);

    @Query(value = "SELECT * FROM cms.company WHERE lower(company_name) = lower(:company_name) AND status not in ('deleted')", nativeQuery = true)
    List<Company> getCompanyByName(@Param("company_name") String company_name);

    @Query(value = "SELECT * FROM cms.company WHERE lower(company_name) = lower(:company_name) AND company_id not in (:company_id) AND status not in ('deleted')", nativeQuery = true)
    List<Company> getCompanyByNameExceptId(@Param("company_name") String company_name,@Param("company_id")int company_id);

    @Modifying
    @Query(value = "UPDATE cms.company SET company_name=:company_name,company_address=:company_address,company_phone=:company_phone ,company_email=:company_email," +
            "status=:status,updated_by=:updated_by,updated_date=current_timestamp WHERE company_id =:company_id ", nativeQuery = true)
    void updateCompany(@Param("company_name") String company_name,@Param("company_address") String company_address,
                       @Param("company_phone") String company_phone, @Param("company_email") String company_email,
                       @Param("status") String status, @Param("updated_by") String updated_by, @Param("company_id") int company_id);

    @Modifying
    @Query(value = "UPDATE cms.company SET status = 'deleted',updated_by=:updated_by,updated_date=current_timestamp WHERE company_id=:company_id", nativeQuery = true)
    void deleteCompany(@Param("company_id") int company_id, @Param("updated_by") String updated_by);
}
