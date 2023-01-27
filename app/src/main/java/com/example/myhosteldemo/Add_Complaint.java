package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.myhosteldemo.Utility.GlobalData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Add_Complaint extends AppCompatActivity {

    Toolbar toolbar ;
    Spinner spinner ;
    TextInputLayout topic_layout ;

    ArrayList<String> problems ;
    ArrayAdapter adapter ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        toolbar = findViewById(R.id.com_add_toolbar) ;
        spinner = findViewById(R.id.com_add_spinner) ;
        topic_layout = findViewById(R.id.com_add_topic_layout) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        problems = new ArrayList<>() ;
        problems.add("<--Select-->") ; problems.add("Electrician") ; problems.add("Plumber") ; problems.add("Mess Food") ; problems.add("Change Room") ; problems.add("Ragging") ;  problems.add("Others") ;

        adapter = new ArrayAdapter(this , R.layout.drop_down_item , problems) ;
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("Others")){
                    topic_layout.setVisibility(View.VISIBLE);
                }
                else{
                    topic_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

}