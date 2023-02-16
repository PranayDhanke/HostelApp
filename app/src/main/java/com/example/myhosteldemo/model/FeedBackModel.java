package com.example.myhosteldemo.model;

public class FeedBackModel {
    String uploadedBy ;
    String details , url ;
    long time ;

    public FeedBackModel(){}

    public FeedBackModel(String uploadedBy, String details, String url, long time) {
        this.uploadedBy = uploadedBy;
        this.details = details;
        this.url = url;
        this.time = time;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
