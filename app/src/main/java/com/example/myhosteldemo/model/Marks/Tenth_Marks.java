package com.example.myhosteldemo.model.Marks;

import java.io.Serializable;

public class Tenth_Marks implements Serializable {
    String marks , percent ;

    String marksheet , allotment , aadhar ;

    long time ;

    public Tenth_Marks() {
    }

    public Tenth_Marks(String marks, String percent, String marksheet, String allotment, String aadhar, long time) {
        this.marks = marks;
        this.percent = percent;
        this.marksheet = marksheet;
        this.allotment = allotment;
        this.aadhar = aadhar;
        this.time = time;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
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
