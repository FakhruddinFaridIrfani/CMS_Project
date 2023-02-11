package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Playlist;
import com.project.CmsApplication.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {

    @Query(value = "SELECT * from cms_2.Playlist WHERE " +
            "lower(playlist_name) like lower(:playlist_name) AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "CAST(profile_id AS VARCHAR) like :profile_id AND " +
            "CAST(start_date AS VARCHAR) like :start_date  AND " +
            "CAST(end_date AS VARCHAR) like :end_date  AND " +
            "CAST(\"sort\" AS VARCHAR) like :sort AND " +
            "status like :status AND " +
            "status not in('deleted') AND " +
            "CAST(is_default AS VARCHAR) like :is_default AND " +
            "lower(created_by) like lower(:created_by) AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "lower(updated_by) like lower(:updated_by) AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Playlist> getPlaylistList(@Param("playlist_name") String playlist_name, @Param("branch_id") String branch_id,
                                   @Param("region_id") String region_id, @Param("company_id") String company_id,
                                   @Param("profile_id") String profile_id,
                                   @Param("start_date") String start_date, @Param("end_date") String end_date,
                                   @Param("sort") String sort, @Param("status") String status, @Param("is_default") String is_default,
                                   @Param("created_by") String created_by, @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by, @Param("updated_date") String updated_at);

//    @Modifying
//    @Query(value = "INSERT INTO cms_2.Playlist(status,playlist_name,branch_id,region_id,company_id,position_id,start_date,end_date,\"sort\",is_default,created_by,created_date,updated_by,updated_date) " +
//            "VALUES('active',:playlist_name,:branch_id,:region_id,:company_id,:position_id,CAST(:start_date AS timestamp),CAST(:end_date AS timestamp),:sort,:is_default,:created_by,current_timestamp,:created_by,current_timestamp) ", nativeQuery = true)
//    void save(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
//              @Param("region_id") int region_id, @Param("company_id") int company_id,
//              @Param("position_id") int position_id,
//              @Param("start_date") String start_date, @Param("end_date") String end_date,
//              @Param("sort") int sort, @Param("is_default") boolean is_default, @Param("created_by") String created_by);

    @Query(value = "SELECT * from cms_2.Playlist WHERE playlist_id =:playlist_id", nativeQuery = true)
    List<Playlist> getPlaylistById(@Param("playlist_id") int playlist_id);

    @Query(value = "SELECT * from cms_2.Playlist WHERE branch_id =:branch_id AND status not in ('deleted')", nativeQuery = true)
    List<Playlist> getPlaylistByBranchId(@Param("branch_id") int branch_id);


//    @Query(value = "SELECT * from cms_2.Playlist WHERE resource_id =:resource_id AND status not in ('deleted')", nativeQuery = true)
//    List<Playlist> getPlaylistByResourceId(@Param("resource_id") int resource_id);

    @Query(value = "SELECT * from cms_2.Playlist WHERE lower(playlist_name) = lower(:playlist_name) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id=:company_id", nativeQuery = true)
    List<Playlist> getPlaylistByName(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                                     @Param("region_id") int region_id, @Param("company_id") int company_id);

    @Query(value = "SELECT * from cms_2.Playlist WHERE lower(playlist_name) = lower(:playlist_name) AND playlist_id not in (:playlist_id) AND status not in ('deleted')  AND branch_id =:branch_id AND region_id=:region_id AND company_id=:company_id", nativeQuery = true)
    List<Playlist> getPlaylistByNameExceptId(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                                             @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Playlist SET playlist_name=:playlist_name," +
            "start_date = CAST(:start_date AS timestamp),end_date=CAST(:end_date AS timestamp),status=:status,is_default=:is_default," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE playlist_id =:playlist_id ", nativeQuery = true)
    void updatePlaylist(@Param("playlist_name") String playlist_name,
                        @Param("start_date") String start_date, @Param("end_date") String end_date,
                        @Param("status") String status, @Param("is_default") boolean is_default,
                        @Param("updated_by") String updated_by, @Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms_2.Playlist SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE playlist_id=:playlist_id", nativeQuery = true)
    void deletePlaylist(@Param("playlist_id") int playlist_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * from cms_2.Playlist WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Playlist> getExpiredPlaylistId();

    @Query(value = "SELECT * from cms_2.Playlist WHERE branch_id=:branch_id AND position_id=:position_id AND status= 'active' ORDER BY \"sort\" DESC LIMIT 1 ", nativeQuery = true)
    List<Playlist> getSortOrder(@Param("position_id") int position_id, @Param("branch_id") int branch_id);


    @Query(value = "SELECT * from cms_2.Playlist WHERE lower(playlist_name) = lower(:playlist_name)", nativeQuery = true)
    List<Playlist> getPlaylistByNameInsertedValues(@Param("playlist_name") String playlist_name);

    @Query(value = "SELECT * from cms_2.Playlist WHERE end_date < current_timestamp AND status = 'active' AND is_default <> true", nativeQuery = true)
    List<Playlist> getExpiredPlaylist();

    @Query(value = "SELECT * from cms_2.playlist where profile_id = :profile_id AND status <> 'deleted' AND status <> 'inactive' ORDER BY start_date DESC  limit 1", nativeQuery = true)
    List<Playlist> getPlaylistByProfileId(@Param("profile_id") int profile_id);

    @Query(value = "SELECT " +
            "    CASE " +
            "        when CAST(:start_date_input AS DATE) between CAST(start_date AS DATE) and CAST(end_date AS DATE) then '0' " +
            "        when CAST(:end_date_input AS DATE) between CAST(start_date AS DATE) and CAST(end_date AS DATE) then '0' " +
            "        when CAST(start_date AS DATE) between CAST(:start_date_input AS DATE) and CAST(:end_date_input AS DATE) then '0' " +
            "        when CAST(end_date AS DATE) between CAST(:start_date_input AS DATE) and CAST(:end_date_input AS DATE) then '0' " +
            "        else '1' " +
            "        END AS availability " +
            "  FROM cms_2.playlist where profile_id = :profile_id AND status ='active' AND is_default = false ", nativeQuery = true)
    String checkAddNewPlaylistAvailability(@Param("profile_id") int profile_id, @Param("start_date_input") String start_date_input, @Param("end_date_input") String end_date_input);

    @Query(value = "SELECT * from CMS_2.playlist where CAST(:start_date_input AS DATE) BETWEEN CAST(start_date AS DATE) and CAST(end_date AS DATE) " +
            "and CAST(:end_date_input AS DATE) between CAST(start_date AS DATE) and CAST(end_date AS DATE) " +
            "AND CAST(start_date AS DATE) between CAST(:start_date_input AS DATE) and CAST(:end_date_input AS DATE) " +
            "AND CAST(end_date AS DATE) between CAST(:start_date_input AS DATE) and CAST(:end_date_input AS DATE) " +
            "AND profile_id = :profile_id " +
            "AND status ='active' " +
            "AND is_default = false " +
            "AND playlist_id <> :playlist_id ", nativeQuery = true)
    List<Playlist> checkDateForPlaylist(@Param("profile_id") int profile_id, @Param("start_date_input") String start_date_input, @Param("end_date_input") String end_date_input, @Param("playlist_id") int playlist_id);

    @Query(value = "SELECT * FROM cms_2.playlist where profile_id = :profile_id AND is_default = true", nativeQuery = true)
    List<Playlist> checkAddNewDefaultPlaylistAvailability(@Param("profile_id") int profile_id);
}
