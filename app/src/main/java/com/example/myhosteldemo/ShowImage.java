package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myhosteldemo.Utility.GlobalData;

public class ShowImage extends AppCompatActivity {

    Toolbar toolbar ;

    ImageView image ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        toolbar = findViewById(R.id.show_toolbar) ;
        image = findViewById(R.id.show_image) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.black);

        Intent intent = getIntent() ;

        Uri uri = intent.getParcelableExtra("Image") ;
        String url = intent.getStringExtra("url") ;
        int id = intent.getIntExtra("id" , 0) ;

        if(uri != null){
            image.setImageURI(uri);
        }
        else if(url != null){
            Glide.with(this)
                    .load(url)
                    .into(image) ;
        }
        else if(id != 0){
            image.setImageResource(id) ;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }
}