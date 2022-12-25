package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.Playlist;
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
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {

    @Query(value = "SELECT * FROM cms.Playlist WHERE " +
            "lower(playlist_name) like lower(:playlist_name) AND " +
            "CAST(company_id AS VARCHAR) like :company_id AND ((CAST(branch_id AS VARCHAR) like :branch_id AND CAST(region_id AS VARCHAR) like :region_id) OR region_id = 0 OR branch_id=0) AND " +
            "CAST(position_id AS VARCHAR) like :position_id AND " +
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
                                   @Param("position_id") String position_id,
                                   @Param("start_date") String start_date, @Param("end_date") String end_date,
                                   @Param("sort") String sort, @Param("status") String status, @Param("is_default") String is_default,
                                   @Param("created_by") String created_by, @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by, @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Playlist(status,playlist_name,branch_id,region_id,company_id,position_id,start_date,end_date,\"sort\",is_default,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:playlist_name,:branch_id,:region_id,:company_id,:position_id,CAST(:start_date AS timestamp),CAST(:end_date AS timestamp),:sort,:is_default,:created_by,current_timestamp,:created_by,current_timestamp) ", nativeQuery = true)
    void save(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
              @Param("region_id") int region_id, @Param("company_id") int company_id,
              @Param("position_id") int position_id,
              @Param("start_date") String start_date, @Param("end_date") String end_date,
              @Param("sort") int sort, @Param("is_default") boolean is_default, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Playlist WHERE playlist_id =:playlist_id", nativeQuery = true)
    List<Playlist> getPlaylistById(@Param("playlist_id") int playlist_id);

    @Query(value = "SELECT * FROM cms.Playlist WHERE branch_id =:branch_id AND status not in ('deleted')", nativeQuery = true)
    List<Playlist> getPlaylistByBranchId(@Param("branch_id") int branch_id);


//    @Query(value = "SELECT * FROM cms.Playlist WHERE resource_id =:resource_id AND status not in ('deleted')", nativeQuery = true)
//    List<Playlist> getPlaylistByResourceId(@Param("resource_id") int resource_id);

    @Query(value = "SELECT * FROM cms.Playlist WHERE lower(playlist_name) = lower(:playlist_name) AND status not in ('deleted') AND branch_id =:branch_id AND region_id=:region_id AND company_id=:company_id", nativeQuery = true)
    List<Playlist> getPlaylistByName(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                                     @Param("region_id") int region_id, @Param("company_id") int company_id);

    @Query(value = "SELECT * FROM cms.Playlist WHERE lower(playlist_name) = lower(:playlist_name) AND playlist_id not in (:playlist_id) AND status not in ('deleted')  AND branch_id =:branch_id AND region_id=:region_id AND company_id=:company_id", nativeQuery = true)
    List<Playlist> getPlaylistByNameExceptId(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                                             @Param("region_id") int region_id, @Param("company_id") int company_id, @Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms.Playlist SET playlist_name=:playlist_name,branch_id=:branch_id,region_id=:region_id,company_id=:company_id,position_id=:position_id," +
            "start_date = CAST(:start_date AS timestamp),end_date=CAST(:end_date AS timestamp),status=:status,is_default=:is_default," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE playlist_id =:playlist_id ", nativeQuery = true)
    void updatePlaylist(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                        @Param("region_id") int region_id, @Param("company_id") int company_id,
                        @Param("position_id") int position_id,
                        @Param("start_date") String start_date, @Param("end_date") String end_date,
                        @Param("status") String status, @Param("is_default") boolean is_default,
                        @Param("updated_by") String updated_by, @Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms.Playlist SET status = 'deleted',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE playlist_id=:playlist_id", nativeQuery = true)
    void deletePlaylist(@Param("playlist_id") int playlist_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * FROM cms.Playlist WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Playlist> getExpiredPlaylistId();

    @Query(value = "SELECT * FROM cms.Playlist WHERE branch_id=:branch_id AND position_id=:position_id AND status= 'active' ORDER BY \"sort\" DESC LIMIT 1 ", nativeQuery = true)
    List<Playlist> getSortOrder(@Param("position_id") int position_id, @Param("branch_id") int branch_id);


    @Query(value = "SELECT * FROM cms.Playlist WHERE lower(playlist_name) = lower(:playlist_name)", nativeQuery = true)
    List<Playlist> getPlaylistByNameInsertedValues(@Param("playlist_name") String playlist_name);

    @Query(value = "SELECT * FROM cms.Playlist WHERE end_date < current_timestamp AND status = 'active' AND is_default <> true", nativeQuery = true)
    List<Playlist> getExpiredPlaylist();

    @Query(value = "SELECT * FROM cms.playlist where position_id = :position_id AND status <> 'deleted'", nativeQuery = true)
    List<Playlist> getPlaylistByPositionId(@Param("position_id") int position_id);


}
