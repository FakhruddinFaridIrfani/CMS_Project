package com.project.CmsApplication.dto;

import lombok.Data;

@Data
public class OutputDesktopPlaylist {

    int resource_id;
    String type;
    String file;
    String url_resource;
    int duration;
    String stretch;
    int sequence;

    public OutputDesktopPlaylist(int resource_id, String type, String file, String url_resource, int duration, String stretch, int sequence) {
        this.resource_id = resource_id;
        this.type = type;
        this.file = file;
        this.url_resource = url_resource;
        this.duration = duration;
        this.stretch = stretch;
        this.sequence = sequence;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getUrl_resource() {
        return url_resource;
    }

    public void setUrl_resource(String url_resource) {
        this.url_resource = url_resource;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStretch() {
        return stretch;
    }

    public void setStretch(String stretch) {
        this.stretch = stretch;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
