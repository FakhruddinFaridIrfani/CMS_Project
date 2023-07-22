package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Device;
import com.project.CmsApplication.model.DeviceMonitoringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface DeviceMonitoringLogRepository extends JpaRepository<DeviceMonitoringLog, Integer> {

    @Query(value = "SELECT * from cms_2.device_monitoring_log WHERE " +
            "device_id=:device_id " +
            "ORDER BY created_date DESC", nativeQuery = true)
    List<DeviceMonitoringLog> getDeviceMonitoringLogList(@Param("device_id") int device_id);

    @Query(value = "SELECT * from cms_2.device_monitoring_log WHERE " +
            "device_id=:device_id " +
            "AND " +
            "case when :log_start_date is NULL AND :log_end_date is NULL then created_date IS NOT NULL else CAST(created_date AS DATE) BETWEEN CAST(:log_start_date AS DATE) AND CAST (:log_end_date AS DATE) end " +
            "ORDER BY created_date DESC", nativeQuery = true)
    List<DeviceMonitoringLog> getDeviceMonitoringLogListWithDate(@Param("device_id") int device_id,@Param("log_start_date") String log_start_date,
                                                         @Param("log_end_date")String log_end_date);


}
