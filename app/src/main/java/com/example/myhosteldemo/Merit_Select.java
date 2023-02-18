package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.GlobalData;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Merit_Select extends AppCompatActivity {

    Toolbar toolbar ;

    FloatingActionButton fab ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_select);

        toolbar = findViewById(R.id.select_toolbar) ;
        fab = findViewById(R.id.select_fab) ;

        fab.setOnClickListener(v -> {
            if(GlobalData.isAdmin){

            }
            else{
                Toast.makeText(this, "Please login as admin first", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

}