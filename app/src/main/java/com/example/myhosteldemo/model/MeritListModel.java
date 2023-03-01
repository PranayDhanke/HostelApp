package com.example.myhosteldemo.model;

import java.io.Serializable;

public class MeritListModel implements Serializable {
    String createdBy ;
    String startDate , endDate ;
    long startLong , endLong ;
    String title , description ;
    boolean opened ;
    long createDate ;

    public MeritListModel() {
    }

    public MeritListModel(String createdBy, String startDate, String endDate, long startLong, long endLong, String title, String description, boolean opened, long createDate) {
        this.createdBy = createdBy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startLong = startLong;
        this.endLong = endLong;
        this.title = title;
        this.description = description;
        this.opened = opened;
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getStartLong() {
        return startLong;
    }

    public void setStartLong(long startLong) {
        this.startLong = startLong;
    }

    public long getEndLong() {
        return endLong;
    }

    public void setEndLong(long endLong) {
        this.endLong = endLong;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
