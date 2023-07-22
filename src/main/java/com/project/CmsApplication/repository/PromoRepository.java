package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PromoRepository extends JpaRepository<Promo, Integer> {

    @Query(value = "SELECT * from cms_2.Promo WHERE " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "lower(tittle) like lower(:tittle) AND " +
            "file like :file AND " +
            "lower(description) like lower(:description) AND " +
            "popup like :popup AND " +
            "popup_description like :popup_description AND " +
            "CAST(start_date AS VARCHAR) like :start_date AND " +
            "CAST(end_date AS VARCHAR) like :end_date AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Promo> getPromoList(
            @Param("branch_id") String branch_id,
            @Param("region_id") String region_id,
            @Param("company_id") String company_id,
            @Param("tittle") String tittle,
            @Param("file") String file,
            @Param("description") String description,
            @Param("popup") String popup,
            @Param("popup_description") String popup_description,
            @Param("start_date") String start_date,
            @Param("end_date") String end_date,
            @Param("status") String status,
            @Param("created_by") String created_by,
            @Param("created_date") String created_date,
            @Param("updated_by") String updated_by,
            @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms_2.Promo(status,branch_id,region_id,company_id,tittle,file,description,popup,popup_description,start_date,end_date,created_by,created_date,updated_by,updated_date,thumbnail) " +
            "VALUES('active',:branch_id,:region_id,:company_id,:tittle,:file,:description,:popup,:popup_description,CAST(:start_date AS timestamp),CAST(:end_date AS timestamp),:created_by,current_timestamp,:created_by,current_timestamp,:thumbnail)", nativeQuery = true)
    void save(@Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id,
              @Param("tittle") String tittle,
              @Param("file") String file, @Param("description") String description,
              @Param("popup") String popup, @Param("popup_description") String popup_description,
              @Param("start_date") String start_date, @Param("end_date") String end_date,
              @Param("created_by") String created_by, @Param("thumbnail") String thumbnail);

    @Query(value = "SELECT * from cms_2.Promo WHERE promo_id =:promo_id", nativeQuery = true)
    List<Promo> getPromoById(@Param("promo_id") int promo_id);

    @Query(value = "SELECT * from cms_2.Promo WHERE branch_id =:branch_id AND status not in ('deleted')", nativeQuery = true)
    List<Promo> getPromoByBranchId(@Param("branch_id") int branch_id);

    @Query(value = "SELECT * from cms_2.Promo WHERE lower(tittle) =lower(:tittle) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<Promo> getPromoByTittle(@Param("tittle") String tittle, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.Promo WHERE lower(tittle) =lower(:tittle) AND promo_id not in (:promo_id) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id =:company_id", nativeQuery = true)
    List<Promo> getPromoByTittleExceptId(@Param("tittle") String tittle, @Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("promo_id") int promo_id);


    @Modifying
    @Query(value = "UPDATE cms_2.Promo SET branch_id=:branch_id,region_id=:region_id,company_id=:company_id,tittle=:tittle,file=:file,description=:description,popup=:popup,popup_description=:popup_description," +
            "start_date=CAST(:start_date AS timestamp),end_date =CAST(:end_date AS timestamp) ,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp, thumbnail =:thumbnail WHERE promo_id =:promo_id ", nativeQuery = true)
    void updatePromo(@Param("branch_id") int branch_id, @Param("region_id") int region_id, @Param("company_id") int company_id,
                     @Param("tittle") String tittle, @Param("file") String file,
                     @Param("description") String description, @Param("popup") String popup,
                     @Param("popup_description") String popup_description,
                     @Param("start_date") String start_date, @Param("end_date") String end_date, @Param("status") String status,
                     @Param("updated_by") String updated_by, @Param("promo_id") int promo_id, @Param("thumbnail") String thumbnail);

    @Modifying
    @Query(value = "UPDATE cms_2.Promo SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE promo_id=:promo_id", nativeQuery = true)
    void deletePromo(@Param("promo_id") int promo_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * from cms_2.Promo WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Promo> getExpiredPromoId();


}
