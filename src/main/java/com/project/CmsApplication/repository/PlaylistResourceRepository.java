package com.project.CmsApplication.repository;

import com.project.CmsApplication.model.PlaylistResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PlaylistResourceRepository extends JpaRepository<PlaylistResource, Integer> {


    @Modifying
    @Query(value = "INSERT INTO cms.Playlist_Resource(status,playlist_id,resource_id,order,created_by,created_date,updated_by,updated_date) " +
            "VALUES('active',:playlist_id,:resource_id,:order,:created_by,current_timestamp,:created_by,current_timestamp)", nativeQuery = true)
    void save(@Param("playlist_id") int playlist_id, @Param("resource_id") int resource_id, @Param("order") int order, @Param("created_by") String created_by);

    @Query(value = "SELECT * FROM cms.Playlist_Resource WHERE playlist_id =:playlist_id", nativeQuery = true)
    List<PlaylistResource> getPlaylistResourceByPlaylist_id(@Param("playlist_id") int playlist_id);

    @Modifying
    @Query(value = "UPDATE cms.Playlist_Resource SET order=:order,updated_by=:updated_by,updated_date=current_timestamp WHERE playlist_resource_id =:playlist_resource_id ", nativeQuery = true)
    void updatePlaylistResource(@Param("order") int order,@Param("playlist_resource_id") int playlist_resource_id,@Param("updated_by")String updated_by);

    @Modifying
    @Query(value = "DELETE FROM cms.Playlist_Resource WHERE playlist_resource_id=:playlist_resource_id", nativeQuery = true)
    void deletePlaylistResource(@Param("playlist_resource_id") int playlist_resource_id);


}
