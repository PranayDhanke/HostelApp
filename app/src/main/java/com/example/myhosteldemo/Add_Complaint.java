package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Adapter.Complaint_Add_Viewpager_Adapter;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Complaint_Model;
import com.example.myhosteldemo.model.Complaint_Viewpager;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Add_Complaint extends AppCompatActivity {

    Toolbar toolbar ;
    Spinner spinner ;
    TextInputLayout topic_layout ;
    EditText topic , desc ;
    ConstraintLayout add_image ;
    ViewPager2 viewPager2 ;
    TextView more ;
    CheckBox pri ;
    WormDotsIndicator indicator ;
    Button submit ;

    ArrayList<String> problems ;
    ArrayList<Complaint_Viewpager> images ;
    ArrayAdapter adapter ;
    Complaint_Add_Viewpager_Adapter image_adapter ;

    static final int GALLERY_REQUEST = 100 ;

    LayoutInflater factory ;
    View textEntryView  ;

    FirebaseUser user ;
    DatabaseReference database ;
    StorageReference storage ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        toolbar = findViewById(R.id.com_add_toolbar) ;
        spinner = findViewById(R.id.com_add_spinner) ;
        topic_layout = findViewById(R.id.com_add_topic_layout) ;
        topic = findViewById(R.id.com_add_topic) ;
        desc = findViewById(R.id.com_add_desc) ;
        add_image = findViewById(R.id.com_add_addImage) ;
        viewPager2 = findViewById(R.id.com_add_viewpager) ;
        indicator = findViewById(R.id.com_add_view_dot) ;
        pri = findViewById(R.id.com_add_private) ;
        submit = findViewById(R.id.com_add_submit) ;

        factory = getLayoutInflater();
        textEntryView = factory.inflate(R.layout.complaint_add_image, null);
        more = textEntryView.findViewById(R.id.com_add_view_more) ;


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

        images = new ArrayList<>() ;
        image_adapter = new Complaint_Add_Viewpager_Adapter(images , this) ;
        viewPager2.setAdapter(image_adapter);
        indicator.attachTo(viewPager2);

        add_image.setOnClickListener(v -> getImage());
        //more.setOnClickListener(v -> getImage());
        submit.setOnClickListener(v -> uploadComplaint());

        user = FirebaseAuth.getInstance().getCurrentUser() ;
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private  void getImage(){
        ImagePicker.Companion.with(Add_Complaint.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start(GALLERY_REQUEST) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                if(data != null){
                    add_image.setVisibility(View.GONE);
                    viewPager2.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.VISIBLE);
                    updateViewPager(data.getData()) ;
                }
                else{
                    Toast.makeText(Add_Complaint.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void updateViewPager(Uri uri){
        images.add(new Complaint_Viewpager(uri)) ;
        image_adapter.notifyDataSetChanged();
        Toast.makeText(this, "Image added", Toast.LENGTH_SHORT).show();
    }

    private void uploadComplaint(){
        if(validateInput()){
            ProgressDialog dialog = new ProgressDialog(this) ;
            dialog.setTitle("Please wait...");
            dialog.setMessage("Adding your complaint");
            dialog.setCancelable(true);
            dialog.show();

            ArrayList<String> str = new ArrayList<>() ;

            String key = database.child("Complaints/" + user.getUid() + "/").push().getKey() ;
            String fullKey = "Complaints/" + user.getUid() + "/" + key ;

            if(images.size() > 0){
                Task[] uploadTask = new Task[images.size()] ;
                Task[] getTask = new Task[images.size()] ;

                for(int i = 0 ; i < images.size() ; i++){
                    String path = images.get(i).getUri().getPath().toString() ;
                    String fname = path.substring(path.lastIndexOf("/"));
                    int finalI = i;
                    Task ut = storage.child("Complaints/" + user.getUid() + "/" + key + "/" + fname).putFile(images.get(i).getUri())
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Task gt =  task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(task.isSuccessful()){
                                                    str.add(task.getResult().toString()) ;
                                                }
                                            }
                                        }) ;
                                        getTask[finalI] = gt ;
                                    }
                                    else{
                                        Toast.makeText(Add_Complaint.this, "Unable to upload Image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }) ;
                    uploadTask[i] = ut ;
                }
                Tasks.whenAllComplete(uploadTask).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                    @Override
                    public void onSuccess(List<Task<?>> tasks) {
                        Tasks.whenAllComplete(getTask).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                            @Override
                            public void onSuccess(List<Task<?>> tasks) {
                                String top = spinner.getSelectedItem().toString().equals("Others") ? topic.getText().toString() : spinner.getSelectedItem().toString();
                                Complaint_Model model = new Complaint_Model(
                                        user.getUid() ,
                                        top ,
                                        desc.getText().toString() ,
                                        str ,
                                        0 ,
                                        "Pending" ,
                                        pri.isChecked() ,
                                        System.currentTimeMillis() ,
                                        fullKey
                                ) ;
                                database.child("Complaints/" + user.getUid() + "/" + key).setValue(model)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Add_Complaint.this, "Complaint Added successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    GlobalData.complaintAdded = true ;
                                                }
                                                else{
                                                    Toast.makeText(Add_Complaint.this, "Failed to add complaint", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Add_Complaint.this, "Failed to add complaint", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }) ;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Complaint.this, "Failed to add complaint", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }) ;
            }
            else{
                String top = spinner.getSelectedItem().toString().equals("Others") ? topic.getText().toString() : spinner.getSelectedItem().toString();
                Complaint_Model model = new Complaint_Model(
                        user.getUid() ,
                        top ,
                        desc.getText().toString() ,
                        str ,
                        0 ,
                        "Pending" ,
                        pri.isChecked() ,
                        System.currentTimeMillis() ,
                        fullKey
                ) ;
                database.child("Complaints/" + user.getUid() + "/" + key).setValue(model)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Add_Complaint.this, "Complaint Added successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(Add_Complaint.this, "Failed to add complaint", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }
                        });
            }

        }
    }

    private boolean validateInput(){
        if(spinner.getSelectedItem().toString().equals("<--Select-->")){
            Toast.makeText(this, "Please select complaint topic from dropdown", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(spinner.getSelectedItem().toString().equals("Others")){
            if(topic.getText().toString().trim().equals("")){
                topic.setError("Heading should not be empty");
                topic.requestFocus() ;
                return false ;
            }
        }
        if(desc.getText().toString().trim().equals("")){
            desc.setError("Complaint should not be empty");
            desc.requestFocus() ;
            return false ;
        }

        return true ;
    }

}