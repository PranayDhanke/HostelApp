package com.example.myhosteldemo.model;

import java.util.Map;

public class File_model {
    String topic , desc ;
    String uploaded_By , email , user_name ;
    String type ;
    int size ;
    Map<String , String> time ;
    long TIME ;
    String url ;

    public File_model() {
    }

    public File_model(String topic,String desc , String uploaded_By, String email, String user_name, String type, int size, Map<String,String> time , String url) {
        this.topic = topic;
        this.desc = desc ;
        this.uploaded_By = uploaded_By;
        this.email = email;
        this.user_name = user_name;
        this.type = type;
        this.size = size;
        this.time = time;
        this.url = url ;
    }

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }

    public String getUploaded_By() {
        return uploaded_By;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public Map getTime() {
        return time;
    }

    public long giveMeTime(){
        return TIME ;
    }

    public String getUrl() {
        return url;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUploaded_By(String uploaded_By) {
        this.uploaded_By = uploaded_By;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTime(long time) {
        this.TIME = time;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
