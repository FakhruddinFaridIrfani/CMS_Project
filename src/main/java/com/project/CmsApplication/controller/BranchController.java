package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Branch;
import com.project.CmsApplication.repository.BranchRepository;
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
@RequestMapping("/branch")
public class BranchController {
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(BranchController.class);

    @PostMapping("/getBranch")
    public BaseResponse<List<Map<String, Object>>> getBranch(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getBranch - " + input);
        return cmsServices.getBranchList(input);
    }

    @PostMapping("/addNewBranch")
    public BaseResponse<String> addNewBranch(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewBranch - " + input);
        return cmsServices.addNewBranch(input);
    }

    @PostMapping("/updateBranch")
    public BaseResponse<Branch> updateBranch(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateBranch - " + input);
        return cmsServices.updateBranch(input);
    }

    @PostMapping("/deleteBranch")
    public BaseResponse<Branch> deleteBranch(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteBranch - " + input);
        return cmsServices.deleteBranch(input);
    }
}
