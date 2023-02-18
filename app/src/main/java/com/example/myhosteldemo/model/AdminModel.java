package com.example.myhosteldemo.model;

public class AdminModel {
    User admin;
    String password;

    public AdminModel() {
    }

    public AdminModel(User admin, String password) {
        this.admin = admin;
        this.password = password;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
