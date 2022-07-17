package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Resource;
import com.project.CmsApplication.repository.ResourceRepository;
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
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @PostMapping("/getResource")
    public BaseResponse<List<Resource>> getResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getResource - " + input);
        return cmsServices.getResourceList(input);
    }

    @PostMapping("/addNewResource")
    public BaseResponse<String> addNewResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewResource - " + input);
        return cmsServices.addNewResource(input);
    }

    @PostMapping("/updateResource")
    public BaseResponse<Resource> updateResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateResource - " + input);
        return cmsServices.updateResource(input);
    }

    @PostMapping("/deleteResource")
    public BaseResponse<Resource> deleteResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteResource - " + input);
        return cmsServices.deleteResource(input);
    }
}
