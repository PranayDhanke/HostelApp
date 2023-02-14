package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.myhosteldemo.Utility.GlobalData;

public class FeedBack extends AppCompatActivity {

    Toolbar toolbar ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back) ;

        toolbar = findViewById(R.id.feed_toolbar) ;

        setSupportActionBar(toolbar) ;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark) ;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }


}