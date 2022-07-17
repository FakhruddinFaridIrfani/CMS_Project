package com.project.CmsApplication.controller;

import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("test")
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/service")
    public BaseResponse<String> testService() throws Exception {
        logger.info(new Date().getTime() + " : Test Service");
        BaseResponse baseResponse = new BaseResponse<>();

        baseResponse.setStatus("2000");
        baseResponse.setSuccess(true);
        baseResponse.setMessage("Service on");
        return baseResponse;

    }
}
