package com.example.myhosteldemo.model;

import java.util.ArrayList;

public class Complaint_Model {
    String com_by , topic , desc ;
    ArrayList<String> images ;
    int up_votes ;
    String status ;
    boolean pri ;
    long time ;
    String key ;

    public Complaint_Model() {
    }

    public Complaint_Model(String com_by, String topic, String desc, ArrayList<String> images, int up_votes, String status , boolean pri , long time , String key) {
        this.com_by = com_by;
        this.topic = topic;
        this.desc = desc;
        this.images = images;
        this.up_votes = up_votes;
        this.status = status;
        this.pri = pri ;
        this.time = time ;
        this.key = key ;
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

    public boolean getPri(){return pri ;}

    public long getTime(){ return time ;}

    public String getKey(){ return key ;}

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

    public void setPri(boolean pri) {this.pri = pri ;}

    public  void setTime(long time){ this.time = time ;}

    public void setKey(String key) { this.key = key ;}

    @Override
    public String toString() {
        return "Complaint_Model{" +
                "com_by='" + com_by + '\'' +
                ", topic='" + topic + '\'' +
                ", desc='" + desc + '\'' +
                ", images=" + images +
                ", up_votes=" + up_votes +
                ", status='" + status + '\'' +
                ", pri=" + pri +
                ", time=" + time +
                ", key='" + key + '\'' +
                '}';
    }
}
