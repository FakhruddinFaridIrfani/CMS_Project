package com.project.CmsApplication.Services;

import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.repository.CompanyRepository;
import com.google.gson.Gson;
import com.project.CmsApplication.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmsServices {

    Gson gson = new Gson();

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    public BaseResponse<String> addCompany() {

        return new BaseResponse<>();

    }

    public BaseResponse tokenAuthentication(String token,String user_name){
        BaseResponse response = new BaseResponse();




        return response;
    }

}
