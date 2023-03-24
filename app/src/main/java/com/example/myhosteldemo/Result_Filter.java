package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Result_Filter extends AppCompatActivity {

    Spinner branches ;

    String[] spinner_branches = {"Computer" , "Mechanical" , "Chemical" , "Electrical" , "Electronics" , "Civil"} ;

    ArrayAdapter spinner_adapter ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_filter);

        branches = findViewById(R.id.result_filter_spinner) ;

        spinner_adapter = new ArrayAdapter(this , R.layout.drop_down_item , spinner_branches) ;

        branches.setAdapter(spinner_adapter);

    }
}