package com.example.myhosteldemo.model;

public class MainActivity_ImageModel {
    String url ;
    long uploadTime ;

    public MainActivity_ImageModel() {
    }

    public MainActivity_ImageModel(String url, long uploadTime) {
        this.url = url;
        this.uploadTime = uploadTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
