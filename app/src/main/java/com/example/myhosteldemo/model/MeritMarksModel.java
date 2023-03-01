package com.example.myhosteldemo.model;

import java.io.Serializable;
import java.util.HashMap;

public class MeritMarksModel implements Serializable{
    User user ;
    String approval ;
    Object result ;
    String resultOf ;

    public MeritMarksModel() {
    }

    public MeritMarksModel(User user, String approval, Object result, String resultOf) {
        this.user = user;
        this.approval = approval;
        this.result = result;
        this.resultOf = resultOf;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getResultOf() {
        return resultOf;
    }

    public void setResultOf(String resultOf) {
        this.resultOf = resultOf;
    }
}
