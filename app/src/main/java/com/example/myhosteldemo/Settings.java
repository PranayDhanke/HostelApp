package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.GlobalData;
import com.google.firebase.messaging.FirebaseMessaging;

public class Settings extends AppCompatActivity {

    Toolbar toolbar ;

    Switch mess_menu , college_events ;
    TextView feedback , report_bug ;

    FirebaseMessaging messaging ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.set_toolbar) ;
        mess_menu = findViewById(R.id.set_messmenu) ;
        college_events = findViewById(R.id.set_collegeevents) ;
        feedback = findViewById(R.id.set_feedback) ;
        report_bug = findViewById(R.id.set_reportbug) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        messaging = FirebaseMessaging.getInstance() ;

        messaging.subscribeToTopic("All") ;

        mess_menu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    messaging.subscribeToTopic("Mess_Menu") ;
                    Toast.makeText(Settings.this, "You will receive notifications about mess updates", Toast.LENGTH_SHORT).show();
                }
                else{
                    messaging.unsubscribeFromTopic("Mess_Menu") ;
                    Toast.makeText(Settings.this, "You will not receive any mess updates", Toast.LENGTH_SHORT).show();
                }
            }
        });

        college_events.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    messaging.subscribeToTopic("College_Events") ;
                    Toast.makeText(Settings.this, "You will receive notifications about college events", Toast.LENGTH_SHORT).show();
                }
                else{
                    messaging.unsubscribeFromTopic("College_Events") ;
                    Toast.makeText(Settings.this, "You will not receive any college updates", Toast.LENGTH_SHORT).show();
                }
            }
        });


        feedback.setOnClickListener(v -> {

        });

        report_bug.setOnClickListener(v -> {

        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }
}