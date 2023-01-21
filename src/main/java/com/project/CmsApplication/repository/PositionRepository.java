package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PositionRepository extends JpaRepository<Position, Integer> {

    @Query(value = "SELECT * from cms_2.Position WHERE " +
            "CAST(profile_id AS VARCHAR) like :profile_id AND " +
            "lower(box) like lower(:box) AND " +
            "x_pos like :x_pos AND " +
            "y_pos like :y_pos AND " +
            "width like :width AND " +
            "height like :height AND " +
            "lower(measurement) like lower(:measurement) AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Position> getPositionList(
            @Param("profile_id") String profile_id,
            @Param("box") String box,
            @Param("x_pos") String x_pos,
            @Param("y_pos") String y_pos,
            @Param("width") String width,
            @Param("height") String height,
            @Param("measurement") String measurement,
            @Param("status") String status,
            @Param("created_by") String created_by,
            @Param("created_date") String created_date,
            @Param("updated_by") String updated_by,
            @Param("updated_date") String updated_at);

//    @Modifying
//    @Query(value = "INSERT INTO cms_2.Position(status,profile_id,box,x_pos,y_pos,width,height,measurement,created_by,created_date,updated_by,updated_date) " +
//            "VALUES('active',:profile_id,:box,:x_pos,:y_pos,:width,:height,:measurement,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
//    void save(@Param("profile_id") int profile_id, @Param("box") String box,
//              @Param("x_pos") String x_pos, @Param("y_pos") String y_pos,
//              @Param("width") String width, @Param("height") String height,
//              @Param("measurement") String measurement, @Param("created_by") String created_by);

    @Query(value = "SELECT * from cms_2.Position WHERE position_id =:position_id", nativeQuery = true)
    List<Position> getPositionById(@Param("position_id") int position_id);

    @Query(value = "SELECT * from cms_2.Position WHERE profile_id =:profile_id AND status <> 'deleted'", nativeQuery = true)
    List<Position> getPositionByProfileId(@Param("profile_id") int profile_id);

    @Query(value = "SELECT * from cms_2.Position WHERE position_name like :position_name", nativeQuery = true)
    List<Position> getPositionByName(@Param("position_name") String position_name);

    @Modifying
    @Query(value = "UPDATE cms_2.Position SET box=:box,x_pos=:x_pos,y_pos=:y_pos," +
            "width=:width,height=:height,measurement=:measurement,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE position_id =:position_id ", nativeQuery = true)
    void updatePosition(@Param("box") String box, @Param("x_pos") String x_pos, @Param("y_pos") String y_pos,
                        @Param("width") String width, @Param("height") String height, @Param("status") String status,
                        @Param("measurement") String measurement, @Param("updated_by") String updated_by, @Param("position_id") int position_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Position SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE position_id=:position_id", nativeQuery = true)
    void deletePosition(@Param("position_id") int position_id, @Param("updated_by") String updated_by);


    @Query(value = "SELECT distinct(po.*) " +
            "from cms_2.position po " +
            "INNER JOIN cms_2.playlist pl ON po.profile_id = pl.profile_id  " +
            "WHERE po.profile_id = :profile_id AND pl.status <> 'deleted'", nativeQuery = true)
    List<Position> getPositionByProfileIdBasedOnPlaylist(@Param("profile_id") int profile_id);
}
