package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;


public class Merit_Confirm extends AppCompatActivity {

    Toolbar toolbar ;

    EditText username , phone , enroll ;
    TextView branch , yos , gender ;
    Button changes , next , dob ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_confirm);

        toolbar = findViewById(R.id.confirm_toolbar) ;
        username = findViewById(R.id.confirm_username) ;
        phone = findViewById(R.id.confirm_phone) ;
        enroll = findViewById(R.id.confirm_enroll) ;
        branch = findViewById(R.id.confirm_branch) ;
        yos = findViewById(R.id.confirm_year) ;
        gender = findViewById(R.id.confirm_gender) ;
        changes = findViewById(R.id.confirm_makechanges) ;
        next = findViewById(R.id.confirm_next) ;
        dob = findViewById(R.id.confirm_dob) ;

        changes.setOnClickListener(v -> {
            Intent intent =  new Intent(this , SetupAccount.class) ;
            intent.putExtra("MainActivity" , true) ;
            startActivity(intent);
        });

        next.setOnClickListener(v -> {

        });

        getAllData() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    protected void onResume() {
        getAllData() ;
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void getAllData(){
        User user = GlobalData.user ;
        username.setText(user.getUsername());
        phone.setText(user.getPhone());
        enroll.setText(user.getEnroll());
        branch.setText(user.getBranch());
        dob.setText(user.getDob());
        gender.setText(user.getGender());
        yos.setText(user.getYear());
    }
}