package com.example.myhosteldemo.model.Marks;

public class SecondYear_Marks {
    String percentege_of_3rd_sem , percentege_of_4rd_sem ;
    String marksheet_3rd_sem , marksheet_4th_sem , allotment , aadhar ;

    public SecondYear_Marks() {
    }

    public SecondYear_Marks(String percentege_of_3rd_sem, String percentege_of_4rd_sem, String marksheet_3rd_sem, String marksheet_4th_sem, String allotment, String aadhar) {
        this.percentege_of_3rd_sem = percentege_of_3rd_sem;
        this.percentege_of_4rd_sem = percentege_of_4rd_sem;
        this.marksheet_3rd_sem = marksheet_3rd_sem;
        this.marksheet_4th_sem = marksheet_4th_sem;
        this.allotment = allotment;
        this.aadhar = aadhar;
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
}
