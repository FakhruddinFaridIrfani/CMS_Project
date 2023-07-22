package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Role;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.RoleRepository;
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
@RequestMapping("log")
public class LogController {

    @Autowired
    CmsServices cmsServices;


    Logger logger = LoggerFactory.getLogger(LogController.class);

    @PostMapping("/getCmsLog")
    public BaseResponse<List<Role>> getCmsLog(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getCmsLog - " + input);
        return cmsServices.getAuditrailList(input);
    }

}
