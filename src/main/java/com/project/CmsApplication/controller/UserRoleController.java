package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.UserRole;
import com.project.CmsApplication.repository.UserRoleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequestMapping("userRole")
public class UserRoleController {
    DateFormatter dateFormatter;

    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    CmsServices cmsServices;


    @PostMapping("/getUserRole")
    public BaseResponse<List<UserRole>> getUserRole(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.getUserRole(input);
    }

    @PostMapping("/addNewUserRole")
    public BaseResponse<String> addNewUserRole(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.addNewUserRole(input);
    }


    @PostMapping("/getUserRoleByName")
    public BaseResponse<List<UserRole>> getUserRoleByName(@RequestBody String input) throws Exception, SQLException, ParseException {
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(userRoleRepository.getUserRoleByName("%" + jsonInput.optString("user_role_name") + "%"));
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
}
