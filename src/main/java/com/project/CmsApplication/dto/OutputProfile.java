package com.project.CmsApplication.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class OutputProfile {
    private int profile_id;
    private int company_id;
    private int region_id;
    private int branch_id;
    private String profile_name;
    private String description;
    private String status;
    private String status_profile;
    private String created_by;
    private Date created_date;
    private String updated_by;
    private Date updated_date;

}
