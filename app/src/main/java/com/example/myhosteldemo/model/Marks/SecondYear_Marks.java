package com.example.myhosteldemo.model.Marks;

import java.io.Serializable;

public class SecondYear_Marks implements Serializable {
    String percentege_of_3rd_sem , percentege_of_4rd_sem , average_percentage ;
    String marksheet_3rd_sem , marksheet_4th_sem , allotment , aadhar ;
    long time ;

    public SecondYear_Marks() {
    }

    public SecondYear_Marks(String percentege_of_3rd_sem, String percentege_of_4rd_sem, String marksheet_3rd_sem, String marksheet_4th_sem, String allotment, String aadhar , long time) {
        this.percentege_of_3rd_sem = percentege_of_3rd_sem;
        this.percentege_of_4rd_sem = percentege_of_4rd_sem;
        this.marksheet_3rd_sem = marksheet_3rd_sem;
        this.marksheet_4th_sem = marksheet_4th_sem;
        this.allotment = allotment;
        this.aadhar = aadhar;
        this.time = time ;
    }

    public String getPercentege_of_3rd_sem() {
        return percentege_of_3rd_sem;
    }

    public void setPercentege_of_3rd_sem(String percentege_of_3rd_sem) {
        this.percentege_of_3rd_sem = percentege_of_3rd_sem;
    }

    public String getPercentege_of_4rd_sem() {
        return percentege_of_4rd_sem;
    }

    public void setPercentege_of_4rd_sem(String percentege_of_4rd_sem) {
        this.percentege_of_4rd_sem = percentege_of_4rd_sem;
    }

    public String getAverage_percentage() {
        return Float.toString((Float.parseFloat(percentege_of_3rd_sem) + Float.parseFloat(percentege_of_4rd_sem)) / 2) ;
    }

    public void setAverage_percentage(String average_percentage) {
        this.average_percentage = average_percentage;
    }

    public String getMarksheet_3rd_sem() {
        return marksheet_3rd_sem;
    }

    public void setMarksheet_3rd_sem(String marksheet_3rd_sem) {
        this.marksheet_3rd_sem = marksheet_3rd_sem;
    }

    public String getMarksheet_4th_sem() {
        return marksheet_4th_sem;
    }

    public void setMarksheet_4th_sem(String marksheet_4th_sem) {
        this.marksheet_4th_sem = marksheet_4th_sem;
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
