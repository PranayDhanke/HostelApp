package com.example.myhosteldemo.Utility;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;
import android.widget.Toast;

import com.example.myhosteldemo.model.User;

public class ValidateUser {
    User user ;
    Context context ;

    public ValidateUser(User user , Context context){
        this.user = user ;
        this.context = context ;
    }

    public static boolean validateUsername(String username){
        if(username.isEmpty()){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateMobile(String mobile){
        if(mobile.isEmpty() || mobile.length() < 10){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateImage(Uri uri){
        if(uri == null){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateImage(String url){
        if(url == null){
            return false ;
        }
        else{
            return true ;
        }
    }


    public static boolean validateEnroll(String enroll){
        if(enroll == null){
            return false ;
        }
        else if(enroll.length() < 10){
            return false ;
        }
        else{
            return true ;
        }
    }   //<--Select-->


    public static boolean validateBranch(String brach){
        if(brach.equals("<--Select-->")){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateYear(String year){
        if(year.equals("<--Select-->")){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateGender(String gender){
        if(gender.equals("<--Select-->")){
            return false ;
        }
        else{
            return true ;
        }
    }

    public static boolean validateDob(String dob){
        if(dob.equals("No date selected")){
            return false ;
        }
        else{
            return true ;
        }
    }


    public boolean validateUser(){
        if(!validateUsername(user.getUsername())){
            Toast.makeText(context, "Invalid Username", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateMobile(user.getPhone())){
            Toast.makeText(context, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateEnroll(user.getEnroll())){
            Toast.makeText(context, "Invalid Enrollment Number", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateBranch(user.getBranch())){
            Toast.makeText(context, "Please select your branch", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateYear(user.getYear())){
            Toast.makeText(context, "Please select your year of study", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateGender(user.getGender())){
            Toast.makeText(context, "Please select your Gender", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!validateDob(user.getDob())){
            Toast.makeText(context, "Please select your Birthdate", Toast.LENGTH_SHORT).show();
            return false ;
        }

        return true ;
    }



}
