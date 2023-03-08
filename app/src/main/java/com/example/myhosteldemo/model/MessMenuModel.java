package com.example.myhosteldemo.model;

public class MessMenuModel {
    String Breakfast , Lunch , Dinner ,key , uploadBy ;

    public MessMenuModel() {
    }

    public MessMenuModel(String type , String key, String menus, String uploadBy) {
        if(type.equals("Breakfast")){
            Breakfast = menus ;
        }
        else if(type.equals("Lunch")){
             Lunch = menus ;
        }
        else{
            Dinner = menus ;
        }
        this.key = key;
        this.uploadBy = uploadBy;
    }

    public String getBreakfast() {
        return Breakfast;
    }

    public void setBreackfast(String breackfast) {
        this.Breakfast = breackfast;
    }

    public String getLunch() {
        return Lunch;
    }

    public void setLunch(String lunch) {
        this.Lunch = lunch;
    }

    public String getDinner() {
        return Dinner;
    }

    public void setDinner(String dinner) {
        this.Dinner = dinner;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }
}
