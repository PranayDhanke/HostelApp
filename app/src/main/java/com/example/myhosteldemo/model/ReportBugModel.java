package com.example.myhosteldemo.model;

public class ReportBugModel {
    String uploadBy ;
    String handset , screen , url , details , contribute ;
    long time ;

    public ReportBugModel(){}

    public ReportBugModel(String uploadBy, String handset, String screen, String url, String details, String contribute, long time) {
        this.uploadBy = uploadBy;
        this.handset = handset;
        this.screen = screen;
        this.url = url;
        this.details = details;
        this.contribute = contribute;
        this.time = time;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getHandset() {
        return handset;
    }

    public void setHandset(String handset) {
        this.handset = handset;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getContribute() {
        return contribute;
    }

    public void setContribute(String contribute) {
        this.contribute = contribute;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
