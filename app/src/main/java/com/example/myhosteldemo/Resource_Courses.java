package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.Course_Adapter;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Resource_Courses extends AppCompatActivity {

    Toolbar toolbar ;

    DatabaseReference database ;
    FirebaseStorage storage ;
    StorageReference ref ;

    ShimmerRecyclerView recyclerView ;
    TextView nodata ;
    FloatingActionButton add ;

    ArrayList<Course> courses ;

    Intent last ;
    String branch ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_courses);

        toolbar = findViewById(R.id.res_course_toolbar) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        database = FirebaseDatabase.getInstance().getReference() ;
        storage = FirebaseStorage.getInstance() ;
        ref = storage.getReference() ;

        recyclerView = findViewById(R.id.course_recycle) ;
        nodata = findViewById(R.id.course_nodata) ;
        add = findViewById(R.id.add_course) ;
        courses = new ArrayList<>() ;
        last = getIntent() ;
        branch = last.getStringExtra("Branch") ;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Dialog dialog = new Dialog(this) ;
        dialog.setContentView(R.layout.course_custom_dialog);

        dialog.findViewById(R.id.course_dialog_ok).setOnClickListener(v -> {
            EditText et_id = dialog.findViewById(R.id.course_dialog_id) ;
            EditText et_name = dialog.findViewById(R.id.course_dialog_name) ;
            String id = et_id.getText().toString() ;
            String name = et_name.getText().toString() ;

            Course course = new Course(id , name) ;

            database.child("Resources/" + branch + "/Courses/").child(name).setValue(course)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ProgressDialog pd = new ProgressDialog(Resource_Courses.this) ;
                            pd.setTitle("Adding Course");
                            pd.show();
                            pd.setCancelable(false);

                            if(task.isSuccessful()){
                                Toast.makeText(Resource_Courses.this, "Course added Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                preLoadCourses();
                            }
                            else{
                                Toast.makeText(Resource_Courses.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    }) ;

            dialog.dismiss();
        });

        dialog.findViewById(R.id.course_dialog_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void preLoadCourses(){
        database.child("Resources/" + branch + "/Courses/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    courses.clear();
                    nodata.setVisibility(View.GONE);
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Course course = dataSnapshot.getValue(Course.class) ;
                        courses.add(course) ;
                    }
                    Course_Adapter adapter = new Course_Adapter(Resource_Courses.this , courses , getIdForImage() , branch) ;
                    recyclerView.setAdapter(adapter);
                    recyclerView.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                    //Toast.makeText(Resource_Courses.this, "Size = " + courses.size(), Toast.LENGTH_SHORT).show();
                }
                else{
                    recyclerView.hideShimmerAdapter();
                    nodata.setVisibility(View.VISIBLE);
                    //Toast.makeText(Resource_Courses.this, "No data avalable", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recyclerView.hideShimmerAdapter();
                nodata.setText("Failed to load data");
                nodata.setVisibility(View.VISIBLE);
            }
        });
    }

    private int getIdForImage(){
        if(branch.equals("Computer")){
            return R.drawable.computer_svg ;
        }
        if(branch.equals("Mechanical")){
            return R.drawable.mechanical_svg ;
        }
        if(branch.equals("Chemical")){
            return R.drawable.chemical_svg ;
        }
        if(branch.equals("Electrical")){
            return R.drawable.electrical_svg ;
        }
        if(branch.equals("Electronics")){
            return R.drawable.electronics_svg ;
        }
        if(branch.equals("Civil")){
            return R.drawable.civil_svg ;
        }
        if(branch.equals("Common")){
            return R.drawable.civil_svg ;
        }
        else{
            return R.drawable.computer_svg ;
        }
    }

}