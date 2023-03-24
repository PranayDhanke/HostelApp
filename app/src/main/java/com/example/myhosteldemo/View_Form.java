package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;

import java.util.ArrayList;
import java.util.List;

public class View_Form extends AppCompatActivity {
    TextView name , phone , enroll , branch , year , gender , birth ;
    TextView viewmarks1 ,viewmarks1n , viewmarks2 , viewmarks2n , remcol1 , remcoln ;
    TextView marksheet , marksheetn ;
    TextView allotment , aadhar ;

    TableRow viewmark2R , remcolr , marksheetr ;

    Toolbar toolbar ;

    Button back ;

    Intent intent ;

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

        viewmarks1 = findViewById(R.id.view_mark1) ;
        viewmarks1n = findViewById(R.id.view_mark1n) ;
        viewmarks2 = findViewById(R.id.view_mark2) ;
        viewmarks2n = findViewById(R.id.view_mark2n) ;
        remcol1 = findViewById(R.id.view_rem_col1) ;
        remcoln = findViewById(R.id.view_rem_col1n) ;
        marksheet = findViewById(R.id.view_marksheet) ;
        marksheetn = findViewById(R.id.view_marksheetn) ;
        allotment = findViewById(R.id.view_allotment) ;
        aadhar = findViewById(R.id.view_aadhar) ;

        viewmark2R = findViewById(R.id.view_mark2r) ;
        remcolr = findViewById(R.id.view_rem_col1r) ;
        marksheetr = findViewById(R.id.view_marksheetr) ;

        back = findViewById(R.id.view_back) ;

        intent = getIntent() ;

        fillBasicInfo() ;

        fillOtherInfo() ;

        back.setOnClickListener(v -> { finish(); });

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

    private void fillOtherInfo(){
        if(intent.getBooleanExtra("10th" , false)){
            String marks = intent.getStringExtra("marks_10") ;
            String percent = intent.getStringExtra("percentage_10") ;
            Bundle args = intent.getBundleExtra("BUNDLE") ;
            List<Uri> files = (List<Uri>) args.getSerializable("docs");

            if(marks.equals("")){
                viewmarks1.setText("not provided");
            }
            else{
                viewmarks1.setText(marks);
            }

            if(marks.equals("")){
                viewmarks2.setText("not provided");
            }
            else{
                viewmarks2.setText(percent.replace("Percentage :- " , ""));
            }

            remcoln.setText("Marsheet :- ");

            if(files.get(0) == null){
                remcol1.setText("not provider");
            }
            else{
                remcol1.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , files.get(0)) ;
                    startActivity(intent);
                });
            }

            if(files.get(1) == null){
                allotment.setText("not provider");
            }
            else{
                allotment.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , files.get(1)) ;
                    startActivity(intent);
                });
            }

            if(files.get(2) == null){
                aadhar.setText("not provider");
            }
            else{
                aadhar.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , files.get(2)) ;
                    startActivity(intent);
                });
            }

            marksheetr.setVisibility(View.GONE);
        }
        else if(intent.getBooleanExtra("12th" , false)){
            String marks = intent.getStringExtra("marks") ;
            String percent = intent.getStringExtra("percent") ;
            Bundle bundle = intent.getBundleExtra("BUNDLE") ;
            List<Uri> docs = (List<Uri>) bundle.getSerializable("docs");

            viewmarks1n.setText("Marks of 12th :-");
            if(marks.trim().equals("")){
                viewmarks1.setText("not provided");
            }
            else{
                viewmarks1.setText(marks);
            }

            viewmarks2n.setText("Percentage of 12th :-");
            if(percent.trim().equals("")){
                viewmarks2.setText("not provided");
            }
            else{
                viewmarks2.setText(percent);
            }

            remcoln.setText("Marksheet :-");
            if(docs.get(0) == null){
                remcol1.setText("not provided");
            }
            else{
                remcol1.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(0)) ;
                    startActivity(intent);
                });
            }

            marksheetr.setVisibility(View.GONE);

            if(docs.get(1) == null){
                allotment.setText("not provider");
            }
            else{
                allotment.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(1)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(2) == null){
                aadhar.setText("not provider");
            }
            else{
                aadhar.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(2)) ;
                    startActivity(intent);
                });
            }


        }
        else if(intent.getBooleanExtra("1st" , false)){
            String semone = intent.getStringExtra("semone") ;
            String semtwo = intent.getStringExtra("semtwo") ;
            Bundle bundle = intent.getBundleExtra("BUNDLE") ;
            List<Uri> docs = (List<Uri>) bundle.getSerializable("docs");

            viewmarks1n.setText("1st sem Percentage :-");
            if(semone.equals("")){
                viewmarks1.setText("not provided");
            }
            else{
                viewmarks1.setText(semone);
            }

            viewmarks2n.setText("2nd sem Percentage :-");
            if(semtwo.equals("")){
                viewmarks2.setText("not provided");
            }
            else{
                viewmarks2.setText(semtwo);
            }

            remcoln.setText("1st sem Marksheet :-");
            if(docs.get(0) == null){
                remcol1.setText("not provided");
            }
            else{
                remcol1.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(0)) ;
                    startActivity(intent);
                });
            }

            marksheetn.setText("2nd sem Marksheet :-");
            if(docs.get(1) == null){
                marksheet.setText("not provided");
            }
            else{
                marksheet.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(1)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(2) == null){
                allotment.setText("not provided");
            }
            else{
                allotment.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(2)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(3) == null){
                aadhar.setText("not provided");
            }
            else{
                aadhar.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(3)) ;
                    startActivity(intent);
                });
            }


        }
        else if(intent.getBooleanExtra("ITI" , false)){
            String percent = intent.getStringExtra("percent") ;
            Bundle bundle = intent.getBundleExtra("BUNDLE") ;
            List<Uri> docs = (List<Uri>) bundle.getSerializable("docs");

            viewmarks1n.setText("Percentage :-");
            if(percent.equals("")){
                viewmarks1.setText("not provided");
            }
            else{
                viewmarks1.setText(percent);
            }

            viewmarks2n.setText("Marksheet :-");
            if(docs.get(0) == null){
                viewmarks2.setText("not provided");
            }
            else{
                viewmarks2.setText("view");
                viewmarks2.setTextColor(getResources().getColor(R.color.cyandark));

                viewmarks2.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(0)) ;
                    startActivity(intent);
                });
            }

            remcolr.setVisibility(View.GONE);
            marksheetr.setVisibility(View.GONE);

            if(docs.get(1) == null){
                allotment.setText("not provided");
            }
            else{
                allotment.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(1)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(2) == null){
                aadhar.setText("not provided");
            }
            else{
                aadhar.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(2)) ;
                    startActivity(intent);
                });
            }
        }
        else {
            String semthree = intent.getStringExtra("semthree") ;
            String semfore = intent.getStringExtra("semfore") ;
            Bundle bundle = intent.getBundleExtra("BUNDLE") ;
            List<Uri> docs = (List<Uri>) bundle.getSerializable("docs");

            viewmarks1n.setText("3rd sem Percentage :-");
            if(semthree.equals("")){
                viewmarks1.setText("not provided");
            }
            else{
                viewmarks1.setText(semthree);
            }

            viewmarks2n.setText("4th sem Percentage :-");
            if(semfore.equals("")){
                viewmarks2.setText("not provided");
            }
            else{
                viewmarks2.setText(semfore);
            }

            remcoln.setText("3rd sem Marksheet :-");
            if(docs.get(0) == null){
                remcol1.setText("not provided");
            }
            else{
                remcol1.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(0)) ;
                    startActivity(intent);
                });
            }

            marksheetn.setText("4th sem Marksheet :-");
            if(docs.get(1) == null){
                marksheet.setText("not provided");
            }
            else{
                marksheet.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(1)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(2) == null){
                allotment.setText("not provided");
            }
            else{
                allotment.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(2)) ;
                    startActivity(intent);
                });
            }

            if(docs.get(3) == null){
                aadhar.setText("not provided");
            }
            else{
                aadhar.setOnClickListener(v -> {
                    Intent intent = new Intent(this , ShowImage.class) ;
                    intent.putExtra("Image" , docs.get(3)) ;
                    startActivity(intent);
                });
            }

        }
    }

}