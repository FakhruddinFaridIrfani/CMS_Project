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

    @Query(value = "SELECT * FROM cms.Promo WHERE " +
            "CAST(branch_id AS VARCHAR) like :branch_id AND " +
            "title like :title AND " +
            "file like :file AND " +
            "description like :description AND " +
            "popup like :popup AND " +
            "popup_description like :popup_description AND " +
            "CAST(start_date AS VARCHAR) like :start_date AND " +
            "CAST(end_date AS VARCHAR) like :end_date AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Promo> getPromoList(
            @Param("branch_id") String branch_id,
            @Param("title") String title,
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
    @Query(value = "INSERT INTO cms.Promo(status,branch_id,tittle,file,description,popup,popup_description,start_date,end_date,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:branch_id,:tittle,:file,:description,:popup,:popup_description,CAST(:start_date,date),CAST(:end_date,date),:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("branch_id") int branch_id, @Param("tittle") String tittle,
              @Param("file") String file, @Param("description") String description,
              @Param("popup") String popup, @Param("popup_description") String popup_description,
              @Param("start_date") String start_date, @Param("end_date") String end_date,
              @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Promo WHERE promo_id =:promo_id", nativeQuery = true)
    List<Promo> getPromoById(@Param("promo_id") int promo_id);


    @Modifying
    @Query(value = "UPDATE cms.Promo SET branch_id=:branch_id,tittle=:tittle,file=:file,description=:description,popup=:popup,popup_description=:popup_description" +
            "start_date=CAST(:start_date,date),end_date =CAST(:end_date,date) ,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE promo_id =:promo_id ", nativeQuery = true)
    void updatePromo(@Param("branch_id") int branch_id, @Param("tittle") String tittle, @Param("file") String file,
                     @Param("description") String description, @Param("popup") String popup,
                     @Param("popup_description") String popup_description,
                     @Param("start_date") String start_date, @Param("end_date") String end_date, @Param("status") String status,
                     @Param("updated_by") String updated_by, @Param("promo_id") int promo_id);

    @Modifying
    @Query(value = "UPDATE cms.Promo SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE promo_id=:promo_id", nativeQuery = true)
    void deletePromo(@Param("promo_id") int promo_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * FROM cms.Promo WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Promo> getExpiredPromoId();


}
