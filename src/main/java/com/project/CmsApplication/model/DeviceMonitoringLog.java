package com.project.CmsApplication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "device_monitoring_log", schema = "cms")
public class DeviceMonitoringLog {
    @Id
    @Column(name = "device_monitoring_log_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int device_monitoring_log_id;

    @NotNull
    @Column(name = "device_id")
    private int device_id;

    @NotNull
    @Column(name = "log_message")
    private String log_message;

    @NotNull
    @Column(name = "log_screenshot_path")
    private String log_screenshot_path;


    @Column(name = "status")
    private String status;


    @Column(name = "created_by")
    private String created_by;


    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "created_date")
    private Date created_date;


    @Column(name = "updated_by")
    private String updated_by;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "updated_date")
    private Date updated_date;

    public int getDevice_monitoring_log_id() {
        return device_monitoring_log_id;
    }

    public void setDevice_monitoring_log_id(int device_monitoring_log_id) {
        this.device_monitoring_log_id = device_monitoring_log_id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getLog_message() {
        return log_message;
    }

    public void setLog_message(String log_message) {
        this.log_message = log_message;
    }

    public String getLog_screenshot_path() {
        return log_screenshot_path;
    }

    public void setLog_screenshot_path(String log_screenshot_path) {
        this.log_screenshot_path = log_screenshot_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public Date getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Date updated_date) {
        this.updated_date = updated_date;
    }

    @Override
    public String toString() {
        return "DeviceMonitoringLog{" +
                "device_monitoring_log_id=" + device_monitoring_log_id +
                ", device_id=" + device_id +
                ", log_message='" + log_message + '\'' +
                ", log_screenshot_path='" + log_screenshot_path + '\'' +
                ", status='" + status + '\'' +
                ", created_by='" + created_by + '\'' +
                ", created_date=" + created_date +
                ", updated_by='" + updated_by + '\'' +
                ", updated_date=" + updated_date +
                '}';
    }
}

