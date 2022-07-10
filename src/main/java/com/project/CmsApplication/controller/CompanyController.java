package com.project.CmsApplication.controller;

import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Company;
import com.project.CmsApplication.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;

    @GetMapping("/getCompanies")
    public BaseResponse<List<Company>>getAllCompanies() throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setData(companyRepository.findAll());
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

    @PostMapping("/addCompanies")
    public BaseResponse<String> addCompany(@RequestBody Company company) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            companyRepository.save(company);
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
}
