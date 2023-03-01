package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.myhosteldemo.Utility.GlobalData;

public class Merit_2nd_Year extends AppCompatActivity {

    Toolbar toolbar ;

    RadioGroup type , complete ;
    RadioButton regular , direct , th12 , iti ;

    CardView study ;
    LinearLayout ll1st , ll12th , llITI ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit2nd_year);

        toolbar = findViewById(R.id.merit_2nd_toolbar) ;
        type = findViewById(R.id.merit_2nd_type) ;
        complete = findViewById(R.id.merit_2nd_study) ;
        regular = findViewById(R.id.merit_2nd_regular) ;
        direct = findViewById(R.id.merit_2nd_direct) ;
        th12 = findViewById(R.id.merit_2nd_12th) ;
        iti = findViewById(R.id.merit_2nd_ITI) ;
        study = findViewById(R.id.merit_2nd_card_study) ;
        ll1st = findViewById(R.id.merit_2nd_ll_1styear) ;
        ll12th = findViewById(R.id.merit_2nd_ll_12th) ;
        llITI = findViewById(R.id.merit_2nd_ll_ITI) ;

        ll12th.setVisibility(View.GONE);
        llITI.setVisibility(View.GONE);

        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == regular.getId()){
                    study.setVisibility(View.GONE);
                    ll1st.setVisibility(View.VISIBLE);
                    ll12th.setVisibility(View.GONE);
                    llITI.setVisibility(View.GONE);
                }
                else{
                    study.setVisibility(View.VISIBLE);
                    ll1st.setVisibility(View.GONE);
                    ll12th.setVisibility(View.VISIBLE);
                }
            }
        });

        complete.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == th12.getId()){
                    ll12th.setVisibility(View.VISIBLE);
                    llITI.setVisibility(View.GONE);
                }
                else{
                    llITI.setVisibility(View.VISIBLE);
                    ll12th.setVisibility(View.GONE);
                }
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