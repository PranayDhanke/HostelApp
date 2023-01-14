package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Utility.GlobalData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Resource_Courses extends AppCompatActivity {

    Toolbar toolbar ;

    FirebaseDatabase database ;
    FirebaseStorage storage ;
    StorageReference ref ;

    ShimmerRecyclerView recyclerView ;
    TextView nodata ;
    FloatingActionButton add ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_courses);

        toolbar = findViewById(R.id.res_course_toolbar) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;
        ref = storage.getReference() ;

        recyclerView = findViewById(R.id.course_recycle) ;
        nodata = findViewById(R.id.course_nodata) ;
        add = findViewById(R.id.add_course) ;

        recyclerView.showShimmerAdapter();

        preLoadCourses() ;

        add.setOnClickListener(v -> addCourse());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void addCourse(){

    }

    private void preLoadCourses(){

    }

}