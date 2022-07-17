package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Promo;
import com.project.CmsApplication.repository.PromoRepository;
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
@RequestMapping("/promo")
public class PromoController {
    @Autowired
    PromoRepository promoRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(PromoController.class);

    @PostMapping("/getPromo")
    public BaseResponse<List<Map<String, Object>>> getPromo(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getPromo - " + input);
        return cmsServices.getPromoList(input);
    }

    @PostMapping("/addNewPromo")
    public BaseResponse<String> addNewPromo(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewPromo - " + input);
        return cmsServices.addNewPromo(input);
    }

    @PostMapping("/updatePromo")
    public BaseResponse<Promo> updatePromo(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updatePromo - " + input);
        return cmsServices.updatePromo(input);
    }

    @PostMapping("/deletePromo")
    public BaseResponse<Promo> deletePromo(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deletePromo - " + input);
        return cmsServices.deletePromo(input);
    }
}
