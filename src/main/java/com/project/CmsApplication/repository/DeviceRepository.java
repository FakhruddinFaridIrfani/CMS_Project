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
            "lower(device_name) like lower(:device_name) AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
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

//    @Query(value = "SELECT * FROM cms.Device WHERE license_key =:license_key", nativeQuery = true)
//    List<Device> getDeviceByLicenseKey(@Param("license_key") String license_key);

    @Query(value = "SELECT * FROM cms.Device WHERE license_key =:license_key AND device_unique_id = '' and status <> 'deleted'", nativeQuery = true)
    List<Device> checkLicenseKeyUsed(@Param("license_key") String license_key);

    @Query(value = "SELECT * FROM cms.Device WHERE license_key =:license_key and status <> 'deleted'", nativeQuery = true)
    List<Device> checkLicenseKeyCount(@Param("license_key") String license_key);

    @Query(value = "SELECT * FROM cms.Device WHERE lower(device_name) = lower(:device_name) AND status not in ('deleted')", nativeQuery = true)
    List<Device> getDeviceByName(@Param("device_name") String device_name);

    @Query(value = "SELECT * FROM cms.Device WHERE lower(device_name) = lower(:device_name) AND device_id not in (:device_id) AND status not in ('deleted')", nativeQuery = true)
    List<Device> getDeviceByNameExceptId(@Param("device_name") String device_name, @Param("device_id") int device_id);

    @Modifying
    @Query(value = "UPDATE cms.Device SET device_name=:device_name,status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE device_id =:device_id ", nativeQuery = true)
    void updateDevice(@Param("device_name") String device_name, @Param("status") String status,
                      @Param("updated_by") String updated_by, @Param("device_id") int device_id);

    @Modifying
    @Query(value = "UPDATE cms.Device SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE device_id=:device_id", nativeQuery = true)
    void deleteDevice(@Param("device_id") int device_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * FROM cms.device WHERE device_id in (" +
            "SELECT distinct(device_id) FROM cms.position " +
            "WHERE position_id in " +
            "    (SELECT distinct(position_id) FROM cms.playlist WHERE CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND status <> 'deleted') AND status <> 'deleted') " +
            "AND status <> 'deleted' ORDER BY device_name ASC", nativeQuery = true)
    List<Device> getDeviceListFromPlaylist(@Param("branch_id") String branch_id,
                                           @Param("region_id") String region_id, @Param("company_id") String company_id);


    @Query(value = "SELECT * FROM cms.device where status <> 'deleted' AND device_unique_id =:device_unique_id", nativeQuery = true)
    List<Device> checkDeviceUniqueId(@Param("device_unique_id") String device_unique_id);

    @Modifying
    @Query(value = "UPDATE cms.device SET license_key =:license_key,updated_by=:updated_by,updated_date=current_timestamp WHERE device_id =:device_id", nativeQuery = true)
    void updateLicenseKey(@Param("device_id") int device_id, @Param("license_key") String license_key, @Param("updated_by") String updated_by);

    @Modifying
    @Query(value = "UPDATE cms.device SET device_unique_id =:device_unique_id,updated_by='SYSTEM',updated_date=current_timestamp WHERE device_id =:device_id", nativeQuery = true)
    void updateLicenseKeyDeviceUniqueIdPair(@Param("device_id") int device_id, @Param("device_unique_id") String device_unique_id);




}
