package com.project.CmsApplication.controller;

import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.UserRole;
import com.project.CmsApplication.repository.UserRoleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("userRole")
public class UserRoleController {
    @Autowired
    UserRoleRepository userRoleRepository;

    @GetMapping("/getUserRole")
    public BaseResponse<List<UserRole>> getAllUserRole() throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setData(userRoleRepository.findAll());
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

    @PostMapping("/getUserRoleDynamic")
    public BaseResponse<List<UserRole>> getUserWithParams(String input){
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(userRoleRepository.getUserRoleList(jsonInput.optString("user_role_id"),jsonInput.optString("user_role_name"),jsonInput.optString("user_role_desc")));
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

    @PostMapping("/addUserRole")
    public BaseResponse<String> addUserRole(@RequestBody UserRole userRole) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            userRoleRepository.save(userRole);
            baseResponse.setStatus("2000");
            baseResponse.setSuccess(true);
            baseResponse.setMessage("User Role successfully Added");

        } catch (Exception e) {
            baseResponse.setStatus("0");
            baseResponse.setSuccess(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }
}
