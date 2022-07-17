package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Position;
import com.project.CmsApplication.repository.PositionRepository;
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
@RequestMapping("/position")
public class PositionController {

    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(PositionController.class);

    @PostMapping("/getPosition")
    public BaseResponse<List<Map<String, Object>>> getPosition(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getPosition - " + input);
        return cmsServices.getPositionList(input);
    }

    @PostMapping("/addNewPosition")
    public BaseResponse<String> addNewPosition(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewPosition - " + input);
        return cmsServices.addNewPosition(input);
    }

    @PostMapping("/updatePosition")
    public BaseResponse<Position> updatePosition(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updatePosition - " + input);
        return cmsServices.updatePosition(input);
    }

    @PostMapping("/deletePosition")
    public BaseResponse<Position> deletePosition(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deletePosition - " + input);
        return cmsServices.deletePosition(input);
    }
}
