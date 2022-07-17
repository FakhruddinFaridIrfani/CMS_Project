package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Region;
import com.project.CmsApplication.repository.RegionRepository;
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
@RequestMapping("/region")
public class RegionController {
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(RegionController.class);

    @PostMapping("/getRegion")
    public BaseResponse<List<Map<String, Object>>> getRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getRegion - " + input);
        return cmsServices.getRegionList(input);
    }

    @PostMapping("/addNewRegion")
    public BaseResponse<String> addNewRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewRegion - " + input);
        return cmsServices.addNewRegion(input);
    }

    @PostMapping("/updateRegion")
    public BaseResponse<Region> updateRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateRegion - " + input);
        return cmsServices.updateRegion(input);
    }

    @PostMapping("/deleteRegion")
    public BaseResponse<Region> deleteRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteRegion - " + input);
        return cmsServices.deleteRegion(input);
    }
}
