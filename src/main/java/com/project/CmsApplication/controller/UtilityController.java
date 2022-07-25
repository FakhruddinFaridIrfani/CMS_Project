package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Users;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/utility")
public class UtilityController {

    Logger logger = LoggerFactory.getLogger(UtilityController.class);

    @Autowired
    CmsServices cmsServices;

    @GetMapping("/testService")
    public BaseResponse<String> testService() throws Exception {
        logger.info(new Date().getTime() + " : Test Service");
        BaseResponse baseResponse = new BaseResponse<>();

        baseResponse.setStatus("2000");
        baseResponse.setSuccess(true);
        baseResponse.setMessage("Service on");
        return baseResponse;
    }

    @PostMapping("/addFile")
    public BaseResponse<String> addFile(@RequestBody String input) throws Exception {
        logger.info(new Date().getTime() + " : Add File test");
        BaseResponse baseResponse = new BaseResponse<>();
        JSONObject jsonObject = new JSONObject(input);
        return cmsServices.addFile(jsonObject.optString("file_name"), jsonObject.optString("file_content"), jsonObject.optString("folder"));
    }

    @PostMapping("/getFile")
    public BaseResponse<Map<String, Object>> getFileByName(@RequestBody String input) throws Exception {
        logger.info(new Date().getTime() + " : Get File");
        JSONObject jsonObject = new JSONObject(input);
        return cmsServices.getFile(jsonObject.optString("file_name"), jsonObject.optString("folder"));
    }

//    @PostMapping("/queryBuilder")
//    public BaseResponse<List<Users>> queryBuilder(@RequestBody String input) throws Exception {
//        logger.info(new Date().getTime() + " : test query");
//        JSONObject jsonObject = new JSONObject(input);
//        return cmsServices.queryBuilder();
//    }
}
