package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Marks.SecondYear_Marks;
import com.example.myhosteldemo.model.Marks.Tenth_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.example.myhosteldemo.model.User;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Result_2nd_year extends AppCompatActivity {

    Chip  marksheet3 , marksheet4 , allotment , aadhar ;

    ImageView profile , kebab ;
    TextView username , filled ;
    TextView phone , enroll , branch , year , gender , birth ;
    TextView percent3 , percent4 , rank ;
    Spinner approval ;
    Toolbar toolbar ;
    TextView save ;
    EditText reject ;

    Intent prevIntent ;
    MeritListModel listModel ;
    MeritMarksModel meritMarksModel ;
    User user ;
    SecondYear_Marks secondYear_marks;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy  hh:mm aa") ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2nd_year);

        toolbar = findViewById(R.id.result_2nd_toolbar) ;
        profile = findViewById(R.id.result_2nd_profile) ;
        kebab = findViewById(R.id.result_2nd_kebab) ;
        username = findViewById(R.id.result_2nd_username) ;
        filled = findViewById(R.id.result_2nd_formfilled) ;
        marksheet3 = findViewById(R.id.result_2nd_3markshhet) ;
        marksheet4 = findViewById(R.id.result_2nd_4marksheet) ;
        allotment  = findViewById(R.id.result_2nd_allotment) ;
        aadhar  = findViewById(R.id.result_2nd_aadhar) ;
        phone = findViewById(R.id.result_2nd_phone) ;
        enroll = findViewById(R.id.result_2nd_enroll) ;
        branch = findViewById(R.id.result_2nd_branch) ;
        year = findViewById(R.id.result_2nd_year) ;
        gender = findViewById(R.id.result_2nd_gender) ;
        birth = findViewById(R.id.result_2nd_birth) ;
        percent3 = findViewById(R.id.result_2nd_3percent) ;
        percent4 = findViewById(R.id.result_2nd_4percent) ;
        approval = findViewById(R.id.result_2nd_approval) ;
        rank = findViewById(R.id.result_2nd_rank) ;
        save = findViewById(R.id.result_2nd_save) ;
        reject = findViewById(R.id.result_2nd_rejection) ;

        prevIntent = getIntent() ;
        listModel = (MeritListModel) prevIntent.getSerializableExtra("MeritListModel");
        meritMarksModel = (MeritMarksModel) prevIntent.getSerializableExtra("MeritMarksModel");
        user = (User) prevIntent.getSerializableExtra("User");
        secondYear_marks = (SecondYear_Marks) prevIntent.getSerializableExtra("2nd year");

        fillAllInfo() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void fillAllInfo(){
        username.setText(user.getUsername());
        filled.setText(sdf.format(secondYear_marks.getTime()));
        phone.setText(user.getPhone());
        enroll.setText(user.getEnroll());
        branch.setText(user.getBranch());
        year.setText(user.getYear());
        gender.setText(user.getGender());
        birth.setText(user.getDob());
        percent3.setText(secondYear_marks.getPercentege_of_3rd_sem() + " %");
        percent4.setText(secondYear_marks.getPercentege_of_4rd_sem() + " %");
        rank.setText("N/A");

        String[] str = {"Unapproved" , "Approved" , "Rejected"} ;
        ArrayAdapter adapter = new ArrayAdapter(this , R.layout.drop_down_item , str) ;
        approval.setAdapter(adapter) ;
        int pos = 0 ;

        if(meritMarksModel.getApproval().equals("Unapproved")){
            pos = 0 ;
        }
        else if(meritMarksModel.getApproval().equals("Approved")){
            pos = 1 ;
        }
        else{
            pos = 2 ;
        }

        approval.setSelection(pos);

        if(GlobalData.isAdmin){
            approval.setEnabled(true);
            marksheet3.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , secondYear_marks.getMarksheet_3rd_sem()) ;
                startActivity(intent);
            });

            marksheet4.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , secondYear_marks.getMarksheet_4th_sem()) ;
                startActivity(intent);
            });

            allotment.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , secondYear_marks.getAllotment()) ;
                startActivity(intent);
            });

            aadhar.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , secondYear_marks.getAadhar()) ;
                startActivity(intent);
            });

            kebab.setOnClickListener(v -> {
                showPopup(kebab) ;
            });

            profile.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , user.getProfile()) ;
                startActivity(intent);
            });

            save.setVisibility(View.VISIBLE);
        }
        else{
            approval.setEnabled(false);
            save.setVisibility(View.GONE);
        }

        loadProfileImage() ;

        approval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(approval.getSelectedItem().toString().equals("Rejected")){
                    reject.setVisibility(View.VISIBLE);
                }
                else{
                    reject.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save.setOnClickListener(v -> {
            saveUpdatedData() ;
        });
    }

    private void loadProfileImage(){
        Glide.with(this)
                .load(user.getProfile())
                .into(profile) ;
    }

    private void saveUpdatedData(){
        HashMap<String , Object> hash = new HashMap<>() ;
        hash.put("approval" , approval.getSelectedItem().toString()) ;
        hash.put("rejection" , reject.getText().toString()) ;
    }

    private void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this , v) ;
        popupMenu.inflate(R.menu.result_options_menu);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.result_options_copy:
                    copyKey() ;
                    break;
                case R.id.result_options_delete:
                    deleteForm() ;
                default:
                    return false ;
            }
            return false;
        });

        popupMenu.show();
    }

    private void copyKey(){
        String key = "MeritListData/" ;
        key += listModel.getTitle() + "/" ;
        key += user.getYear() + "/" ;

        String gender = user.getGender().equals("male") ? "Boys" : "Girls" ;

        key += gender + "/" ;
        key += user.getBranch() + "/" ;
    }

    private void deleteForm(){

    }
}