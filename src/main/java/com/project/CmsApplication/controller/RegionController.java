package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Region;
import com.project.CmsApplication.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/region")
public class RegionController {
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    CmsServices cmsServices;

    @PostMapping("/getRegion")
    public BaseResponse<List<Map<String, Object>>> getUserWithParams(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.getRegionList(input);
    }

    @PostMapping("/addNewRegion")
    public BaseResponse<String> addNewUsers(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.addNewRegion(input);
    }

    @PostMapping("/updateRegion")
    public BaseResponse<Region> updateRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.updateRegion(input);
    }

    @PostMapping("/deleteRegion")
    public BaseResponse<Region> deleteRegion(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.deleteRegion(input);
    }
}
