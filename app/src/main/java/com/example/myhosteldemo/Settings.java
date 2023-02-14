package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

    //sharedPreferences
    SharedPreferences settingpref ;
    SharedPreferences.Editor set_editor ;

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

        settingpref = getSharedPreferences("settings_pref" , MODE_PRIVATE) ;
        set_editor = settingpref.edit() ;

        setUpSettings() ;

        mess_menu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    messaging.subscribeToTopic("Mess_Menu") ;
                    set_editor.putBoolean("Mess_Menu" , true) ;
                    set_editor.apply();
                    Toast.makeText(Settings.this, "You will receive notifications about mess updates", Toast.LENGTH_SHORT).show();
                }
                else{
                    messaging.unsubscribeFromTopic("Mess_Menu") ;
                    set_editor.putBoolean("Mess_Menu" , false) ;
                    set_editor.apply();
                    Toast.makeText(Settings.this, "You will not receive any mess updates", Toast.LENGTH_SHORT).show();
                }
            }
        });

        college_events.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    messaging.subscribeToTopic("College_Events") ;
                    set_editor.putBoolean("College_Events" , true) ;
                    set_editor.apply();
                    Toast.makeText(Settings.this, "You will receive notifications about college events", Toast.LENGTH_SHORT).show();
                }
                else{
                    messaging.unsubscribeFromTopic("College_Events") ;
                    set_editor.putBoolean("College_Events" , false) ;
                    set_editor.apply();
                    Toast.makeText(Settings.this, "You will not receive any college updates", Toast.LENGTH_SHORT).show();
                }
            }
        });


        feedback.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this , FeedBack.class));
        });

        report_bug.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this , Report_Bug.class));
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void setUpSettings(){
        if(settingpref.getBoolean("Mess_Menu" , false)){
            mess_menu.setChecked(true);
        }
        else{
            mess_menu.setChecked(false);
        }

        if(settingpref.getBoolean("College_Events" , false)){
            college_events.setChecked(true);
        }
        else{
            college_events.setChecked(false);
        }
    }

}