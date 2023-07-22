package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Profile;
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
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    CmsServices cmsServices;

    Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @PostMapping("/getProfile")
    public BaseResponse<List<Map<String, Object>>> getProfile(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : Get Profile List - " + input);
        return cmsServices.getProfileList(input);
    }
    @PostMapping("/getProfileForDevice")
    public BaseResponse<List<Map<String, Object>>> getProfileForDevice(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : Get Profile List For Device - " + input);
        return cmsServices.getProfileForDevice(input);
    }

    @PostMapping("/addNewProfile")
    public BaseResponse<String> addNewProfile(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewProfile - " + input);
        return cmsServices.addNewProfile(input);
    }

    @PostMapping("/updateProfile")
    public BaseResponse<Profile> updateProfile(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateProfile - " + input);
        return cmsServices.updateProfile(input);
    }

    @PostMapping("/deleteProfile")
    public BaseResponse<Profile> deleteProfile(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteProfile - " + input);
        return cmsServices.deleteProfile(input);
    }

}
