package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;

public class View_Form extends AppCompatActivity {
    TextView name , phone , enroll , branch , year , gender , birth ;

    Toolbar toolbar ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_form);

        name = findViewById(R.id.view_name) ;
        phone = findViewById(R.id.view_phone) ;
        enroll = findViewById(R.id.view_enroll) ;
        branch = findViewById(R.id.view_branch) ;
        year = findViewById(R.id.view_year) ;
        gender = findViewById(R.id.view_gender) ;
        birth = findViewById(R.id.view_birth) ;

        fillBasicInfo() ;

        toolbar = findViewById(R.id.view_toolbar) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void fillBasicInfo(){
        User user = GlobalData.user ;
        name.setText(user.getUsername());
        phone.setText(user.getPhone());
        enroll.setText(user.getEnroll());
        branch.setText(user.getBranch());
        year.setText(user.getYear());
        gender.setText(user.getGender());
        birth.setText(user.getDob());
    }

}