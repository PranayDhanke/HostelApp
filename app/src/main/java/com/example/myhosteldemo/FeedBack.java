package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.FeedBackModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FeedBack extends AppCompatActivity {

    Toolbar toolbar ;

    TextInputEditText details ;
    TextView imageSelect ;
    Chip addImage ;
    Uri uri ;
    Button submit ;
    static ProgressDialog dialog ;

    DatabaseReference database ;
    StorageReference storage ;
    FirebaseUser fuser ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back) ;

        toolbar = findViewById(R.id.feed_toolbar) ;

        setSupportActionBar(toolbar) ;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark) ;

        details = findViewById(R.id.feed_details) ;
        imageSelect = findViewById(R.id.feed_selectedimage) ;
        addImage = findViewById(R.id.feed_addImage) ;
        submit = findViewById(R.id.feed_submit) ;

        database = FirebaseDatabase.getInstance().getReference() ;
        storage = FirebaseStorage.getInstance().getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        details.setMovementMethod(new ScrollingMovementMethod());

        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Please wait....");
        dialog.setMessage("Saving the feedback....");
        dialog.setCancelable(false);

        submit.setOnClickListener(v -> {
            if(validate()){
                dialog.show();
                if(uri == null){
                    uploadFeedBack() ;
                }
                else{
                    uploadImage() ;
                }
            }
        });

        addImage.setOnClickListener(v -> {
            ImagePicker.Companion
                    .with(this)
                    .maxResultSize(1080,1080)
                    .compress(1024)
                    .crop()
                    .galleryOnly()
                    .start(100);
        });

        imageSelect.setOnClickListener(v -> {
            Intent intent = new Intent(this , ShowImage.class) ;
            intent.putExtra("Image" , uri) ;
            startActivity(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
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
            imageSelect.setVisibility(View.VISIBLE);
            imageSelect.setText("Selected Image : " + name);
        }
    }

    private boolean validate(){
        if(details.getText().toString().equals("")){
            details.setError("Please enter some details...");
            details.requestFocus() ;
            return false ;
        }

        return true ;
    }

    private void uploadFeedBack(){
        FeedBackModel feedback = new FeedBackModel(fuser.getUid(),
                details.getText().toString(),
                "",
                System.currentTimeMillis()) ;

        database.child("FeedBacks/").push()
                .setValue(feedback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.cancel();
                            AlertUtil.showAlertDialog(FeedBack.this,
                                    "FeedBack Saved",
                                    "Thanks for telling us about your suggestions...",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(FeedBack.this,
                                    "",
                                    "Failed to save feedback...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private void uploadFeedBack(String key , String url){
        FeedBackModel feedback = new FeedBackModel(fuser.getUid(),
                details.getText().toString(),
                url,
                System.currentTimeMillis()) ;

        database.child("FeedBacks/" + key)
                .setValue(feedback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.cancel();
                            AlertUtil.showAlertDialog(FeedBack.this,
                                    "FeedBack Saved",
                                    "Thanks for telling us about your suggestions...",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(FeedBack.this,
                                    "",
                                    "Failed to save feedback...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private void uploadImage(){
        String key = database.child("FeedBacks/").push().getKey() ;

        storage.child("FeedBacks/" + key)
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
                                                uploadFeedBack(key , task.getResult().toString());
                                            }
                                            else{
                                                dialog.cancel();
                                                AlertUtil.showAlertDialog(FeedBack.this,
                                                        "",
                                                        "Failed to save feedback...",
                                                        true,
                                                        R.drawable.error,
                                                        "Ok");
                                            }
                                        }
                                    }) ;
                        }
                        else{
                            dialog.cancel();
                            AlertUtil.showAlertDialog(FeedBack.this,
                                    "",
                                    "Failed to save feedback...",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

}