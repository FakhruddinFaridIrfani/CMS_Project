package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Company;
import com.project.CmsApplication.model.PlaylistResource;
import com.project.CmsApplication.repository.CompanyRepository;
import com.project.CmsApplication.repository.PlaylistResourceRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/playlistResource")
public class PlaylistResourceController {
    @Autowired
    PlaylistResourceRepository playlistResourceRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(PlaylistResourceController.class);

    @PostMapping("/getPlaylistResource")
    public BaseResponse<List<Map<String, Object>>> getPlaylistResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getPlaylistResource - " + input);
        return cmsServices.getPlaylistResource(input);
    }

    @PostMapping("/addPlaylistResource")
    public BaseResponse<String> addPlaylistResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addPlaylistResource - " + input);
        JSONObject object = new JSONObject(input);
        return cmsServices.addPlaylistResource(object.optString("user_token"), object.optInt("resource_id"), object.optInt("playlist_id"), object.optInt("order"));
    }

    @PostMapping("/updatePlaylistResource")
    public BaseResponse<PlaylistResource> updatePlaylistResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updatePlaylistResource - " + input);
        return cmsServices.updatePlaylistResource(input);
    }

    @PostMapping("/deletePlaylistResource")
    public BaseResponse<PlaylistResource> deletePlaylistResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deletePlaylistResource - " + input);
        return cmsServices.deletePlaylistResource(input);
    }
}
