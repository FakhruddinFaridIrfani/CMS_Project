package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Device;
import com.project.CmsApplication.repository.DeviceRepository;
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
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    CmsServices cmsServices;
    Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @PostMapping("/getDevice")
    public BaseResponse<List<Device>> getDevice(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getDevice - " + input);
        return cmsServices.getDeviceList(input);
    }

    @PostMapping("/addNewDevice")
    public BaseResponse<String> addNewDevice(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewDevice - " + input);
        return cmsServices.addNewDevice(input);
    }

    @PostMapping("/updateDevice")
    public BaseResponse<Device> updateDevice(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateDevice - " + input);
        return cmsServices.updateDevice(input);
    }

    @PostMapping("/deleteDevice")
    public BaseResponse<Device> deleteDevice(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteDevice - " + input);
        return cmsServices.deleteDevice(input);
    }
}
