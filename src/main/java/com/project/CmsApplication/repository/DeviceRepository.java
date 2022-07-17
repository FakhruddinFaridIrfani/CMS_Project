package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    @Query(value = "SELECT * FROM cms.Device WHERE " +
            "device_name like :device_name AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Device> getDeviceList(@Param("device_name") String device_name,
                               @Param("status") String status,
                               @Param("created_by") String created_by,
                               @Param("created_date") String created_date,
                               @Param("updated_by") String updated_by,
                               @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Device(status,device_name,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:device_name,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("device_name") String device_name, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Device WHERE device_id =:device_id", nativeQuery = true)
    List<Device> getDeviceById(@Param("device_id") int device_id);

    @Query(value = "SELECT * FROM cms.Device WHERE device_name like :device_name", nativeQuery = true)
    List<Device> getDeviceByName(@Param("device_name") String device_name);

    @Modifying
    @Query(value = "UPDATE cms.Device SET device_name=:device_name,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE device_id =:device_id ", nativeQuery = true)
    void updateDevice(@Param("device_name") String device_name, @Param("status") String status,
                      @Param("updated_by") String updated_by, @Param("device_id") int device_id);

    @Modifying
    @Query(value = "UPDATE cms.Device SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE device_id=:device_id", nativeQuery = true)
    void deleteDevice(@Param("device_id") int device_id, @Param("updated_by") String updated_by);


}
