package com.example.myhosteldemo.model;

import java.util.ArrayList;

public class Complaint_Model {
    String com_by , topic , desc ;
    ArrayList<String> images ;
    int up_votes ;
    String status ;

    public Complaint_Model() {
    }

    public Complaint_Model(String com_by, String topic, String desc, ArrayList<String> images, int up_votes, String status) {
        this.com_by = com_by;
        this.topic = topic;
        this.desc = desc;
        this.images = images;
        this.up_votes = up_votes;
        this.status = status;
    }

    public String getCom_by() {
        return com_by;
    }

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public int getUp_votes() {
        return up_votes;
    }

    public String getStatus() {
        return status;
    }

    public void setCom_by(String com_by) {
        this.com_by = com_by;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setUp_votes(int up_votes) {
        this.up_votes = up_votes;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
