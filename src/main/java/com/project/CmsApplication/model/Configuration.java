package com.project.CmsApplication.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "Configuration", schema = "cms")
public class Configuration {
    @Id
    @Column(name = "configuration_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int configuration_id;


    @NotNull
    @Column(name = "configuration_name")
    private String configuration_name;


    @NotNull
    @Column(name = "configuration_value")
    private String configuration_value;

    public int getConfiguration_id() {
        return configuration_id;
    }

    public void setConfiguration_id(int configuration_id) {
        this.configuration_id = configuration_id;
    }

    public String getConfiguration_name() {
        return configuration_name;
    }

    public void setConfiguration_name(String configuration_name) {
        this.configuration_name = configuration_name;
    }

    public String getConfiguration_value() {
        return configuration_value;
    }

    public void setConfiguration_value(String configuration_value) {
        this.configuration_value = configuration_value;
    }
}
