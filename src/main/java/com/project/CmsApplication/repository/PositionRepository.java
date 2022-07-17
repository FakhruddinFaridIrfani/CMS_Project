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

    @Query(value = "SELECT * FROM cms.Position WHERE " +
            "CAST(device_id AS VARCHAR) like :device_id AND " +
            "box like :box AND " +
            "x_pos like :x_pos AND " +
            "y_pos like :y_pos AND " +
            "width like :width AND " +
            "height like :height AND " +
            "measurement like :measurement AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Position> getPositionList(
            @Param("device_id") String device_id,
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

    @Modifying
    @Query(value = "INSERT INTO cms.Position(status,device_id,box,x_pos,y_pos,width,height,measurement,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:device_id,:box,:x_pos,:y_pos,:width,:height,:measurement,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("device_id") int device_id, @Param("box") String box,
              @Param("x_pos") String x_pos, @Param("y_pos") String y_pos,
              @Param("width") String width, @Param("height") String height,
              @Param("measurement") String measurement, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Position WHERE position_id =:position_id", nativeQuery = true)
    List<Position> getPositionById(@Param("position_id") int position_id);

    @Query(value = "SELECT * FROM cms.Position WHERE position_name like :position_name", nativeQuery = true)
    List<Position> getPositionByName(@Param("position_name") String position_name);

    @Modifying
    @Query(value = "UPDATE cms.Position SET device_id=:device_id,box=:box,x_pos=:x_pos,y_pos=:y_pos," +
            "width=:width,height=:height,measurement=:measurement,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE position_id =:position_id ", nativeQuery = true)
    void updatePosition(@Param("device_id") int device_id, @Param("box") String box, @Param("x_pos") String x_pos, @Param("y_pos") String y_pos,
                        @Param("width") String width, @Param("height") String height, @Param("status") String status,
                        @Param("measurement") String measurement, @Param("updated_by") String updated_by, @Param("position_id") int position_id);

    @Modifying
    @Query(value = "UPDATE cms.Position SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE position_id=:position_id", nativeQuery = true)
    void deletePosition(@Param("position_id") int position_id, @Param("updated_by") String updated_by);


}
