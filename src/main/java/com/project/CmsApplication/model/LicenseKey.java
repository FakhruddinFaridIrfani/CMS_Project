package com.project.CmsApplication.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "license", schema = "cms")
public class LicenseKey {
    @Id
    @Column(name = "license_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int license_id;


    @NotNull
    @Column(name = "license_key")
    private String license_key;

    public int getLicense_id() {
        return license_id;
    }

    public void setLicense_id(int license_id) {
        this.license_id = license_id;
    }

    public String getLicense_key() {
        return license_key;
    }

    public void setLicense_key(String license_key) {
        this.license_key = license_key;
    }

    @Override
    public String toString() {
        return "LicenseKey{" +
                "license_id=" + license_id +
                ", license_key='" + license_key + '\'' +
                '}';
    }
}
