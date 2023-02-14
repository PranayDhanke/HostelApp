package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.example.myhosteldemo.Utility.GlobalData;

import java.util.ArrayList;
import java.util.Arrays;

public class Report_Bug extends AppCompatActivity {

    Toolbar toolbar ;

    Spinner screen ;
    ArrayAdapter screenAdapter ;
    ArrayList<String> screenList ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        toolbar = findViewById(R.id.report_toolbar) ;
        screen = findViewById(R.id.report_screen) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);


        screenList = new ArrayList() ;
        String arr[] = {"<--Select-->" ,"SignIn" , "Create Account" , "Home" , "Mess" , "Add Complaint" ,
                "My Complaints" , "All Complaints", "Recources" , "Courses" , "Files" , "Profile" ,
                "Update Password" , "Logout" , "Settings" , "FeedBack"} ;
        screenList.addAll(Arrays.asList(arr)) ;

        screenAdapter = new ArrayAdapter(this , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , screenList) ;
        screen.setAdapter(screenAdapter);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }
}