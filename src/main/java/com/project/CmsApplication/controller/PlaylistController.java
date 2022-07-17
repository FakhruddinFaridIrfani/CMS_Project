package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Playlist;
import com.project.CmsApplication.repository.PlaylistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/playlist")
public class PlaylistController {
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    @PostMapping("/getPlaylist")
    public BaseResponse<List<Playlist>> getPlaylist(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getPlaylist - " + input);
        return cmsServices.getPlaylistList(input);
    }

    @PostMapping("/addNewPlaylist")
    public BaseResponse<String> addNewPlaylist(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewPlaylist - " + input);
        return cmsServices.addNewPlaylist(input);
    }

    @PostMapping("/updatePlaylist")
    public BaseResponse<Playlist> updatePlaylist(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updatePlaylist - " + input);
        return cmsServices.updatePlaylist(input);
    }

    @PostMapping("/deletePlaylist")
    public BaseResponse<Playlist> deletePlaylist(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deletePlaylist - " + input);
        return cmsServices.deletePlaylist(input);
    }
}
