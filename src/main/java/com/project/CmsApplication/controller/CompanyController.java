package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Company;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @PostMapping("/getCompany")
    public BaseResponse<List<Company>> getCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getCompany - " + input);
        return cmsServices.getCompanyList(input);
    }

    @PostMapping("/addNewCompany")
    public BaseResponse<String> addNewCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewCompany - " + input);
        return cmsServices.addNewCompany(input);
    }

    @PostMapping("/updateCompany")
    public BaseResponse<Company> updateCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateCompany - " + input);
        return cmsServices.updateCompany(input);
    }

    @PostMapping("/deleteCompany")
    public BaseResponse<Company> deleteCompany(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteCompany - " + input);
        return cmsServices.deleteCompany(input);
    }
}
