package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.RunningText;
import com.project.CmsApplication.repository.RunningTextRepository;
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
@RequestMapping("/runningText")
public class RunningTextController {
    @Autowired
    RunningTextRepository runningTextRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(RunningTextController.class);

    @PostMapping("/getRunningText")
    public BaseResponse<List<Map<String, Object>>> getRunningText(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getRunningText - " + input);
        return cmsServices.getRunningTextList(input);
    }
    @PostMapping("/getRunningTextAndroid")
    public BaseResponse getRunningTextAndroid(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getRunningTextAndroid - " + input);
        return cmsServices.getRunningTextAndroid(input);
    }

    @PostMapping("/addNewRunningText")
    public BaseResponse<String> addNewRunningText(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewRunningText - " + input);
        return cmsServices.addNewRunningText(input);
    }

    @PostMapping("/updateRunningText")
    public BaseResponse<RunningText> updateRunningText(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateRunningText - " + input);
        return cmsServices.updateRunningText(input);
    }

    @PostMapping("/deleteRunningText")
    public BaseResponse<RunningText> deleteRunningText(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteRunningText - " + input);
        return cmsServices.deleteRunningText(input);
    }
}
