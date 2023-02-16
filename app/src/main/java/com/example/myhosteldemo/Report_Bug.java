package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.ReportBugModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;

public class Report_Bug extends AppCompatActivity {

    Toolbar toolbar ;

    DatabaseReference database ;
    StorageReference storage ;
    FirebaseUser fuser ;

    Spinner screen ;
    ArrayAdapter screenAdapter ;
    ArrayList<String> screenList ;

    EditText handset , details ;
    ImageView addImage ;
    RadioGroup contri ;
    RadioButton yes , no , maybe ;
    String radio_val = "" ;
    TextView imageselect , radioselect ;
    Button submit ;
    Uri uri ;
    String contribute ;
    static ProgressDialog dialog ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        toolbar = findViewById(R.id.report_toolbar) ;
        screen = findViewById(R.id.report_screen) ;
        handset = findViewById(R.id.report_handset) ;
        details = findViewById(R.id.report_details) ;
        addImage = findViewById(R.id.report_addimage) ;
        contri = findViewById(R.id.report_radiogroup) ;
        yes = findViewById(R.id.report_radioyes) ;
        no = findViewById(R.id.report_radiono) ;
        maybe = findViewById(R.id.report_radiomaybe) ;
        imageselect = findViewById(R.id.report_imageselect) ;
        radioselect = findViewById(R.id.report_radioselect) ;
        submit = findViewById(R.id.report_submit) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);


        screenList = new ArrayList() ;
        String arr[] = {"<--Select-->" ,"SignIn" , "Create Account" , "Home" , "Mess" , "Add Complaint" ,
                "My Complaints" , "All Complaints", "Recources" , "Courses" , "Files" , "Profile" ,
                "Update Password" , "Logout" , "Settings" , "FeedBack"} ;
        screenList.addAll(Arrays.asList(arr)) ;

        screenAdapter = new ArrayAdapter(this , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , screenList) ;
        screen.setAdapter(screenAdapter);

        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Please wait....");
        dialog.setMessage("Saving the bug....");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        submit.setOnClickListener(v -> {
            if(isValidated()){
                dialog.show();
                uploadBug() ;
            }
        });

        addImage.setOnClickListener(v -> getImage());

        contri.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioselect.setVisibility(View.VISIBLE);

                if(i == yes.getId()){
                    radioselect.setText("Great 🔥 we'll contact you later.");
                    radio_val = yes.getText().toString() ;
                }
                else if(i == no.getId()){
                    radioselect.setText("No problem! Thanks. 😉");
                    radio_val = no.getText().toString() ;
                }
                else{
                    radioselect.setText("Ok 😊");
                    radio_val = maybe.getText().toString() ;
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private boolean isValidated(){
        if(handset.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please enter the name of handset", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(screen.getSelectedItem().toString().equals("<--Select-->")){
            Toast.makeText(this, "Please select the screen on which you found bug", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(details.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please enter some details about bug", Toast.LENGTH_SHORT).show();
            return false ;
        }

        return true ;
    }

    private void getImage(){
        ImagePicker.Companion
                .with(this)
                .maxResultSize(1080,1080)
                .compress(1024)
                .crop()
                .galleryOnly()
                .start(100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null)
            return ;

        if(resultCode != RESULT_OK)
            return;

        if(requestCode == 100){
            Toast.makeText(this, "Screenshot added", Toast.LENGTH_SHORT).show();
            uri = data.getData() ;
            String name = uri.toString() ;
            name = name.substring(name.lastIndexOf("/") + 1) ;
            imageselect.setVisibility(View.VISIBLE);
            imageselect.setText("Selected Image : " + name);
        }
    }

    private void uploadBug(){
        if(uri == null){
            uploadOnlyBug() ;
        }
        else{
            uploadImage() ;
        }
    }

    private void uploadImage(){
        String key = database.child("Bugs/").push().getKey() ;

        storage.child("Bugs/" + key)
                .putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage()
                                    .getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                uploadOnlyBug(key , task.getResult().toString());
                                            }
                                            else{
                                                dialog.cancel();
                                                AlertUtil.showAlertDialog(Report_Bug.this,
                                                        "",
                                                        "Failed to save bug...",
                                                        true,
                                                        R.drawable.error,
                                                        "Ok");
                                            }
                                        }
                                    }) ;
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(Report_Bug.this,
                                    "",
                                    "Failed to save bug...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private void uploadOnlyBug(){
        ReportBugModel bug = new ReportBugModel(fuser.getUid(),
                handset.getText().toString(),
                screen.getSelectedItem().toString(),
                "",
                details.getText().toString(),
                radio_val,
                System.currentTimeMillis()) ;

        database.child("Bugs/").push()
                .setValue(bug)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.cancel();
                            AlertUtil.showAlertDialog(Report_Bug.this,
                                    "Bug Saved",
                                    "Thanks for telling us about the bug...",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(Report_Bug.this,
                                    "",
                                    "Failed to save bug...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private void uploadOnlyBug(String key , String url){
        ReportBugModel bug = new ReportBugModel(fuser.getUid(),
                handset.getText().toString(),
                screen.getSelectedItem().toString(),
                url,
                details.getText().toString(),
                radio_val,
                System.currentTimeMillis()) ;

        database.child("Bugs/" + key)
                .setValue(bug)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.cancel();
                            AlertUtil.showAlertDialog(Report_Bug.this,
                                    "Bug Saved",
                                    "Thanks for telling us about the bug...",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(Report_Bug.this,
                                    "",
                                    "Failed to save bug...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

}