package com.project.CmsApplication.Services;

import com.project.CmsApplication.model.CmsLog;
import com.project.CmsApplication.repository.CmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CmsLogService {

    @Autowired
    CmsLogRepository cmsLogRepository;

    public void createNewLogEntry(String user_id,String company_id,String region_id,String branch_id,String action,String request_body,String result,String remarks){
        CmsLog log = new CmsLog();
        log.setUser_name(user_id);
        log.setCompany_id(company_id);
        log.setRegion_id(region_id);
        log.setBranch_id(branch_id);
        log.setAction_name(action);
        log.setAction_body(request_body);
        log.setResult(result);
        log.setRemarks(remarks);
        log.setCreated_date(new Date());
        cmsLogRepository.save(log);
    }

    public byte[] backupLogToFile(){


        return new byte[0];
    }

    public void clearLogEntry(){

    }

}
