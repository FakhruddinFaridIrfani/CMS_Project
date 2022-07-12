package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Role;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.RoleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;


@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    CmsServices cmsServices;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/addNewRole")
    public BaseResponse<String> addNewRole(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.addNewRole(input);
    }

    @PostMapping("/getRoleList")
    public BaseResponse<List<Role>> getRoleList(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.getRoleList(input);
    }

    @PostMapping("/updateRole")
    public BaseResponse<Role> updateRole(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.updateRole(input);
    }

    @PostMapping("/deleteRole")
    public BaseResponse<Role> deleteRole(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.deleteRole(input);
    }

    @PostMapping("/getRoleById")
    public BaseResponse<List<Users>> getRoleById(@RequestBody String input) throws Exception, SQLException, ParseException {
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(roleRepository.getRoleById(jsonInput.optInt("role_id")));
            baseResponse.setStatus("2000");
            baseResponse.setSuccess(true);
            baseResponse.setMessage("Role get By Id");
        } catch (Exception e) {
            baseResponse.setStatus("0");
            baseResponse.setSuccess(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/getRoleByName")
    public BaseResponse<List<Users>> getRoleByName(@RequestBody String input) throws Exception, SQLException, ParseException {
        BaseResponse baseResponse = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            baseResponse.setData(roleRepository.getRoleByName("%" + jsonInput.optString("role_name") + "%"));
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
