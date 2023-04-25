package com.project.CmsApplication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "cms_log", schema = "cms_2")
public class CmsLog {

    @Id
    @Column(name = "cms_log_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int cms_log_id;

    @NotNull
    @Column(name = "user_name")
    public String user_name;
    @NotNull
    @Column(name = "company_id")
    public String company_id;
    @NotNull
    @Column(name = "region_id")
    public String region_id;
    @NotNull
    @Column(name = "branch_id")
    public String branch_id;


    @NotNull
    @Column(name = "action_name")
    public String action_name;

    @NotNull
    @Column(name = "action_body")
    public String action_body;


    @NotNull
    @Column(name = "result")
    public String result;

    @NotNull
    @Column(name = "remarks")
    public String remarks;



    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "created_date")
    public Date created_date;

    public int getCms_log_id() {
        return cms_log_id;
    }

    public void setCms_log_id(int cms_log_id) {
        this.cms_log_id = cms_log_id;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public String getAction_body() {
        return action_body;
    }

    public void setAction_body(String action_body) {
        this.action_body = action_body;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
