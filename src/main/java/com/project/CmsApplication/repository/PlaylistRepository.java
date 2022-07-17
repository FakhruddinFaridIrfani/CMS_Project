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
            "playlist_name like :playlist_name AND " +
            "CAST(branch_id AS VARCHAR) like :branch_id AND " +
            "CAST(position_id AS VARCHAR) like :position_id AND " +
            "CAST(resource_id AS VARCHAR) like :resource_id AND " +
            "CAST(start_date AS VARCHAR) like :start_date  AND " +
            "CAST(end_date AS VARCHAR) like :end_date  AND " +
            "CAST(\"sort\" AS VARCHAR) like :sort AND " +
            "status like :status AND " +
            "created_by like :created_by AND " +
            "CAST(created_date AS VARCHAR) like :created_date AND " +
            "updated_by like :updated_by AND " +
            "CAST(updated_date AS VARCHAR) like :updated_date ORDER BY created_date DESC", nativeQuery = true)
    List<Playlist> getPlaylistList(@Param("playlist_name") String playlist_name, @Param("branch_id") String branch_id,
                                   @Param("position_id") String position_id, @Param("resource_id") String resource_id,
                                   @Param("start_date") String start_date, @Param("end_date") String end_date,
                                   @Param("sort") String sort, @Param("status") String status,
                                   @Param("created_by") String created_by, @Param("created_date") String created_date,
                                   @Param("updated_by") String updated_by, @Param("updated_date") String updated_at);

    @Modifying
    @Query(value = "INSERT INTO cms.Playlist(status,playlist_name,branch_id,position_id,resource_id,start_date,end_date,\"sort\",created_by,created_date,updated_by,updated_date) " +
            "VALUES('active':playlist_name,:branch_id,:position_id,:resource_id,CAST(:start_date AS date),CAST(:end_date AS date),:sort,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
              @Param("position_id") int position_id, @Param("resource_id") int resource_id,
              @Param("start_date") String start_date, @Param("end_date") String end_date,
              @Param("sort") int sort, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Playlist WHERE playlist_id =:playlist_id", nativeQuery = true)
    List<Playlist> getPlaylistById(@Param("playlist_id") int playlist_id);

    @Query(value = "SELECT * FROM cms.Playlist WHERE playlist_name like :playlist_name", nativeQuery = true)
    List<Playlist> getPlaylistByName(@Param("playlist_name") String playlist_name);

    @Modifying
    @Query(value = "UPDATE cms.Playlist SET playlist_name=:playlist_name,branch_id=:branch_id,position_id=:position_id,resource_id=:resource_id" +
            "start_date = CAST(:start_date AS date),end_date=CAST(:end_date AS date),status=:status," +
            "updated_by=:updated_by,updated_date=current_timestamp WHERE playlist_id =:playlist_id ", nativeQuery = true)
    void updatePlaylist(@Param("playlist_name") String playlist_name, @Param("branch_id") int branch_id,
                        @Param("position_id") int position_id, @Param("resource_id") int resource_id,
                        @Param("start_date") String start_date, @Param("end_date") String end_date,
                        @Param("status") String status,
                        @Param("updated_by") String updated_by, @Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms.Playlist SET status = 'inactive',updated_by=:updated_by," +
            "updated_date=current_timestamp WHERE playlist_id=:playlist_id", nativeQuery = true)
    void deletePlaylist(@Param("playlist_id") int playlist_id, @Param("updated_by") String updated_by);

    @Query(value = "SELECT * FROM cms.Playlist WHERE end_date < current_timestamp AND status = 'active'", nativeQuery = true)
    List<Playlist> getExpiredPlaylistId();

    @Query(value = "SELECT * FROM cmd.Playlist WHERE branch_id=:branch_id AND position_id=:position_id AND status= 'active' ORDER BY \"sort\" DESC LIMIT 1 ", nativeQuery = true)
    List<Playlist> getSortOrder(@Param("position_id") int position_id, @Param("branch_id") int branch_id);


}
