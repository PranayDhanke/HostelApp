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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Marks.SecondYear_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.Arrays;

public class Merit_3rd_Year extends AppCompatActivity {

    Toolbar toolbar ;

    EditText thirdsempercent , forthsempercent ;
    ImageView images[] = new ImageView[4] ;
    Chip chipthird , chipforth , chipallot , chipaadhar ;
    Button view , submit ;

    StorageReference storage ;
    FirebaseFirestore firestore ;
    FirebaseUser firebaseUser ;

    static ProgressDialog progressDialog ;
    String fileNames[] = {"3rd sem marksheet" , "4th sem marksheet" , "Allotment" , "Aadhar"} ;
    Uri files[] = new Uri[4] ;
    String urls[] = {"" , "" , "" , ""} ;

    MeritListModel meritModel ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit3rd_year);

        toolbar = findViewById(R.id.merit_3rd_toolbar) ;
        thirdsempercent = findViewById(R.id.merit_3rd_3percent) ;
        forthsempercent = findViewById(R.id.merit_3rd_4percent) ;
        images[0] = findViewById(R.id.merit_3rd_3marksheet) ;
        images[1] = findViewById(R.id.merit_3rd_4marksheet) ;
        images[2] = findViewById(R.id.merit_3rd_allotmentimage) ;
        images[3] = findViewById(R.id.merit_3rd_aadharimage) ;
        chipthird = findViewById(R.id.merit_3rd_3chip) ;
        chipforth = findViewById(R.id.merit_3rd_4chip) ;
        chipallot = findViewById(R.id.merit_3rd_allotmentchip) ;
        chipaadhar = findViewById(R.id.merit_3rd_aadharchip) ;
        view = findViewById(R.id.merit_3rd_view) ;
        submit = findViewById(R.id.merit_3rd_submit) ;

        storage = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance() ;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this) ;

        chipthird.setOnClickListener(v -> getImage(chipthird));
        chipforth.setOnClickListener(v -> getImage(chipforth));
        chipallot.setOnClickListener(v -> getImage(chipallot));
        chipaadhar.setOnClickListener(v -> getImage(chipaadhar));
        images[0].setOnClickListener(v -> {
            if(images[0].getScaleType() == ImageView.ScaleType.FIT_CENTER){
                images[0].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                images[0].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
        images[1].setOnClickListener(v -> {
            if(images[1].getScaleType() == ImageView.ScaleType.FIT_CENTER){
                images[1].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                images[1].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
        images[2].setOnClickListener(v -> {
            if(images[2].getScaleType() == ImageView.ScaleType.FIT_CENTER){
                images[2].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                images[2].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
        images[3].setOnClickListener(v -> {
            if(images[3].getScaleType() == ImageView.ScaleType.FIT_CENTER){
                images[3].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                images[3].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(this , View_Form.class) ;
            intent.putExtra("3rd" , true) ;
            intent.putExtra("semthree" , thirdsempercent.getText().toString().trim()) ;
            intent.putExtra("semfore" , forthsempercent.getText().toString().trim()) ;
            Bundle bundle = new Bundle() ;
            bundle.putSerializable("docs" , (Serializable) Arrays.asList(files));
            intent.putExtra("BUNDLE" , bundle) ;
            startActivity(intent);
        });

        submit.setOnClickListener(v -> {
            progressDialog.dismiss();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Submitting....");
            progressDialog.setMessage("Please wait....");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIcon(R.drawable.information);
            progressDialog.show();

            if(isValidated()){
                submitForm() ;
            }
            else{
                progressDialog.dismiss();
            }
        });

        meritModel = (MeritListModel) getIntent().getSerializableExtra("MeritListModel");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
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

        int index = 0 ;

        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait....");
        progressDialog.setIcon(R.drawable.information);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

        if(requestCode == 100){
            files[0] = data.getData() ;
            images[0].setImageURI(files[0]);
            images[0].setVisibility(View.VISIBLE);
            index = 0 ;
        }
        else if(requestCode == 150){
            files[1] = data.getData() ;
            images[1].setImageURI(files[1]);
            images[1].setVisibility(View.VISIBLE);
            index = 1 ;
        }
        else if(requestCode == 200){
            files[2] = data.getData() ;
            images[2].setImageURI(files[2]);
            images[2].setVisibility(View.VISIBLE);
            index = 2 ;
        }
        else{
            files[3] = data.getData() ;
            images[3].setImageURI(files[3]);
            images[3].setVisibility(View.VISIBLE);
            index = 3 ;
        }

        Toast.makeText(this, "Uploading Image of " + fileNames[index], Toast.LENGTH_SHORT).show();
        int finalIndex = index;

        UploadTask task = storage.child("MeritListDocs")
                .child(meritModel.getTitle())
                .child(firebaseUser.getUid())
                .child(fileNames[index])
                .putFile(files[index]) ;

        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int progress = (int) (snapshot.getBytesTransferred() / snapshot.getTotalByteCount() * 100);
                        progressDialog.setProgress(progress);
                    }
                }) ;

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        urls[finalIndex] = uri.toString() ;
                                        Toast.makeText(Merit_3rd_Year.this, "Successfully uploaded " + fileNames[finalIndex], Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Merit_3rd_Year.this, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                        AlertUtil.showAlertDialog(Merit_3rd_Year.this,
                                                "Failure",
                                                "Failed to upload Image",
                                                true,
                                                R.drawable.error,
                                                "Ok");
                                        progressDialog.dismiss();
                                        urls[finalIndex] = "" ;
                                        images[finalIndex].setImageURI(null);
                                        files[finalIndex] = null ;
                                    }
                                }) ;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Merit_3rd_Year.this, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                        AlertUtil.showAlertDialog(Merit_3rd_Year.this,
                                "Failure",
                                "Failed to upload Image",
                                true,
                                R.drawable.error,
                                "Ok");
                        progressDialog.dismiss();
                        urls[finalIndex] = "" ;
                        images[finalIndex].setImageURI(null);
                        files[finalIndex] = null ;
                    }
                }) ;
    }

    private void getImage(Chip chip){
        int req = 0 ;
        if(chip.getId() == chipthird.getId()){
            req = 100 ;
        }
        else if(chip.getId() == chipforth.getId()){
            req = 150 ;
        }
        else if(chip.getId() == chipallot.getId()){
            req = 200 ;
        }
        else{
            req = 250 ;
        }
        ImagePicker.Companion
                .with(this)
                .crop()
                .maxResultSize(1080,1080)
                .compress(1024)
                .start(req);
    }

    private boolean isValidated(){
        String p3 = thirdsempercent.getText().toString().trim() ;
        String p4 = forthsempercent.getText().toString().trim() ;

        if(p3.equals("")){
            thirdsempercent.setError("Plaese enter percentage of third semester" , null);
            thirdsempercent.requestFocus() ;
            return false ;
        }
        if(p4.equals("")){
            forthsempercent.setError("Plaese enter percentage of forth semester" , null);
            forthsempercent.requestFocus() ;
            return false ;
        }

        float f3p = Float.parseFloat(p3) ;
        float f4p = Float.parseFloat(p4) ;

        if(f3p > 100.0f){
            thirdsempercent.setError("Percentage can't be greater than 100" , null);
            thirdsempercent.requestFocus() ;
            return false ;
        }

        if(f4p > 100.0f){
            forthsempercent.setError("Percentage can't be greater than 100" , null);
            forthsempercent.requestFocus() ;
            return false ;
        }

        for(int i = 0 ; i < 4 ; i++){
            if(urls[i].equals("")){
                Toast.makeText(this, "Please upload image of " + fileNames[i], Toast.LENGTH_SHORT).show();
                return false ;
            }
        }

        return true ;
    }

    private void submitForm(){
        MeritMarksModel model = new MeritMarksModel() ;
        model.setUser(GlobalData.user);
        model.setApproval("Unapproved");
        model.setResultOf("Second Year");
        model.setRejection("no rejection");
        model.setResult(getSecondYear());
        model.setKey(getMarksKey());

        WriteBatch batch = firestore.batch() ;

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

        DocumentReference doc1 = firestore.collection("MeritListData")
                .document(meritModel.getTitle())
                .collection("All Forms")
                .document(firebaseUser.getUid());

        DocumentReference doc2 = firestore.collection("MeritListData")
                .document(meritModel.getTitle())
                .collection(GlobalData.user.getYear())
                .document(gender)
                .collection(GlobalData.user.getBranch())
                .document(firebaseUser.getUid()) ;

        batch.set(doc1 , model) ;
        batch.set(doc2 , model) ;

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Merit_3rd_Year.this, "Successfully submitted form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_3rd_Year.this,
                        "Success",
                        "Form submitted successfully",
                        true,
                        R.drawable.information,
                        "Ok");
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Merit_3rd_Year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_3rd_Year.this,
                        "Failure",
                        "Failed to submit form",
                        true,
                        R.drawable.error,
                        "Ok");
                progressDialog.dismiss();
            }
        }) ;

    }


    private SecondYear_Marks getSecondYear(){
        SecondYear_Marks marks = new SecondYear_Marks() ;
        marks.setPercentege_of_3rd_sem(thirdsempercent.getText().toString().trim());
        marks.setPercentege_of_4rd_sem(forthsempercent.getText().toString().trim());
        marks.setMarksheet_3rd_sem(urls[0]);
        marks.setMarksheet_4th_sem(urls[1]);
        marks.setAllotment(urls[2]);
        marks.setAadhar(urls[3]);
        marks.setTime(System.currentTimeMillis());
        return marks ;
    }

    private String getMarksKey(){
        String key = "MeritListData/" ;

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

//        DocumentReference doc2 = firestore.collection("MeritListData")
//                .document(model.getTitle())
//                .collection(GlobalData.user.getYear())
//                .document(gender)
//                .collection(GlobalData.user.getBranch())
//                .document(firebaseUser.getUid()) ;
        key += meritModel.getTitle() + "/" ;
        key += GlobalData.user.getYear() + "/" ;
        key += gender + "/" ;
        key += GlobalData.user.getBranch() + "/" ;
        key += firebaseUser.getUid() ;

        return key ;
    }
}