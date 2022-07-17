package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.UsersRepository;
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
@RequestMapping("Users")
public class UsersController {
    DateFormatter dateFormatter;

    @Autowired
    UsersRepository UsersRepository;

    @Autowired
    CmsServices cmsServices;

    Logger logger = LoggerFactory.getLogger(UsersController.class);

    @GetMapping("/getUsers")
    public BaseResponse<List<Users>> getAllUsers() throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setData(UsersRepository.findAll());
            baseResponse.setStatus("2000");
            baseResponse.setSuccess(true);
            baseResponse.setMessage("Company successfully Added");
        } catch (Exception e) {
            baseResponse.setStatus("0");
            baseResponse.setSuccess(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/getUsers")
    public BaseResponse<List<Users>> getUserWithParams(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getUsers - " + input);
        return cmsServices.getUsers(input);
    }

    @PostMapping("/addNewUsers")
    public BaseResponse<String> addNewUsers(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewUsers - " + input);
        return cmsServices.addNewUsers(input);
    }

    @PostMapping("/getUsersById")
    public BaseResponse<List<Users>> getUsersById(@RequestBody String input) throws Exception, SQLException, ParseException {
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(UsersRepository.getUsersById(jsonInput.optInt("user_id")));
            baseResponse.setStatus("2000");
            baseResponse.setSuccess(true);
            baseResponse.setMessage("User Role get By Id");
        } catch (Exception e) {
            baseResponse.setStatus("0");
            baseResponse.setSuccess(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/getUsersByName")
    public BaseResponse<List<Users>> getUsersByName(@RequestBody String input) throws Exception, SQLException, ParseException {
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(UsersRepository.getUsersByName("%" + jsonInput.optString("user_full_name") + "%"));
            baseResponse.setStatus("2000");
            baseResponse.setSuccess(true);
            baseResponse.setMessage("User Role get By Id");
        } catch (Exception e) {
            baseResponse.setStatus("0");
            baseResponse.setSuccess(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/updateUsers")
    public BaseResponse<Users> updateUsers(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateUsers - " + input);
        return cmsServices.updateUsers(input);
    }

    @PostMapping("/deleteUsers")
    public BaseResponse<Users> deleteUsersById(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteUsers - " + input);
        return cmsServices.deleteUsers(input);
    }

    @PostMapping("/loginUsers")
    public BaseResponse<Map<String, Object>> loginUser(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : loginUsers - ");
        return cmsServices.loginUser(input);
    }
}
