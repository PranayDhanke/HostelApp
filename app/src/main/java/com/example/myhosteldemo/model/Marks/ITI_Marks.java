package com.example.myhosteldemo.model.Marks;

public class ITI_Marks {
    float percentage ;
    String marksheet , allotment , aadhar ;
    long time ;

    public ITI_Marks() {
    }

    public ITI_Marks(float percentage, String marksheet, String allotment, String aadhar, String approved, String resultOf) {
        this.percentage = percentage;
        this.marksheet = marksheet;
        this.allotment = allotment;
        this.aadhar = aadhar;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getMarksheet() {
        return marksheet;
    }

    public void setMarksheet(String marksheet) {
        this.marksheet = marksheet;
    }

    public String getAllotment() {
        return allotment;
    }

    public void setAllotment(String allotment) {
        this.allotment = allotment;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
