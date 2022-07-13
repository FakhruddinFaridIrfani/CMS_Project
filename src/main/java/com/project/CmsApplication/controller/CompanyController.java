package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Company;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    CmsServices cmsServices;

    @PostMapping("/getCompany")
    public BaseResponse<List<Company>> getUserWithParams(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.getCompanyList(input);
    }

    @PostMapping("/addNewCompany")
    public BaseResponse<String> addNewUsers(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.addNewCompany(input);
    }

    @PostMapping("/updateCompany")
    public BaseResponse<Company> updateCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.updateCompany(input);
    }

    @PostMapping("/deleteCompany")
    public BaseResponse<Company> deleteCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        return cmsServices.deleteCompany(input);
    }
}
