package com.example.myhosteldemo.model.Marks;

public class FirstYear_Marks {
    float first_sem_percentage , second_sem_percentage , average_percentage ;
    String marksheet_first_sem , marksheet_second_sem , allotment , aadhar ;
    long time ;

    public FirstYear_Marks() {
    }

    public FirstYear_Marks(float first_sem_percentage, float second_sem_percentage, String marksheet_first_sem, String marksheet_second_sem, String allotment, String aadhar, String approved, String resultOf) {
        this.first_sem_percentage = first_sem_percentage;
        this.second_sem_percentage = second_sem_percentage;
        this.marksheet_first_sem = marksheet_first_sem;
        this.marksheet_second_sem = marksheet_second_sem;
        this.allotment = allotment;
        this.aadhar = aadhar;
    }

    public float getFirst_sem_percentage() {
        return first_sem_percentage;
    }

    public void setFirst_sem_percentage(float first_sem_percentage) {
        this.first_sem_percentage = first_sem_percentage;
    }

    public float getSecond_sem_percentage() {
        return second_sem_percentage;
    }

    public void setSecond_sem_percentage(float second_sem_percentage) {
        this.second_sem_percentage = second_sem_percentage;
    }

    public float getAverage_percentage() {
        return (first_sem_percentage+second_sem_percentage) / 2 ;
    }

    public void setAverage_percentage(float average_percentage) {
        this.average_percentage = average_percentage;
    }

    public String getMarksheet_first_sem() {
        return marksheet_first_sem;
    }

    public void setMarksheet_first_sem(String marksheet_first_sem) {
        this.marksheet_first_sem = marksheet_first_sem;
    }

    public String getMarksheet_second_sem() {
        return marksheet_second_sem;
    }

    public void setMarksheet_second_sem(String marksheet_second_sem) {
        this.marksheet_second_sem = marksheet_second_sem;
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
