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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Marks.Tenth_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Merit_1st_year extends AppCompatActivity {

    Toolbar toolbar ;

    EditText marks , percent ;
    ImageView marksheet , allotment , aadhar ;
    Chip markchip , allotchip , aachip ;
    Button view , submit ;

    FirebaseFirestore firestore ;
    FirebaseUser firebaseUser ;
    StorageReference storage ;

    int req = 0 ;
    Uri markImage , allotImage , aadharImage ;
    Intent prevIntent ;
    MeritListModel model ;
    String[] fileNames = {"10th Marksheet" , "Allotment Letter" , "Aadhar Card"} ;
    Uri[] files = new Uri[3] ;
    String urls[] = new String[3] ;

    static ProgressDialog progressDialog ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit1st_year);

        toolbar = findViewById(R.id.merit_1st_toolbar) ;
        marks = findViewById(R.id.merit_1st_tenmark) ;
        percent = findViewById(R.id.merit_1st_percentage) ;
        marksheet = findViewById(R.id.merit_1st_marksheetimage) ;
        allotment = findViewById(R.id.merit_1st_allotmentimage) ;
        aadhar = findViewById(R.id.merit_1st_aadharimage) ;
        markchip = findViewById(R.id.merit_1st_marksheetchip) ;
        allotchip = findViewById(R.id.merit_1st_allotmentchip) ;
        aachip = findViewById(R.id.merit_1st_aadharchip) ;
        view = findViewById(R.id.merit_1st_view) ;
        submit = findViewById(R.id.merit_1st_submit) ;

        firestore = FirebaseFirestore.getInstance() ;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference() ;

        markchip.setOnClickListener(v -> {
            getImage(markchip) ;
        });

        allotchip.setOnClickListener(v -> {
            getImage(allotchip) ;
        });

        aachip.setOnClickListener(v -> {
            getImage(aachip);
        });

        marksheet.setOnClickListener(v -> {
            if(marksheet.getScaleType() == ImageView.ScaleType.FIT_CENTER){
                marksheet.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                marksheet.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });

        allotment.setOnClickListener(v -> {
            if(allotment.getScaleType() == ImageView.ScaleType.FIT_CENTER){
                allotment.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                allotment.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });

        aadhar.setOnClickListener(v -> {
            if(aadhar.getScaleType() == ImageView.ScaleType.FIT_CENTER){
                aadhar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                aadhar.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });

        view.setOnClickListener(v -> {
            //Toast.makeText(this, "Yet to implement", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this , View_Form.class) ;
            intent.putExtra("10th" , true) ;
            intent.putExtra("marks_10" , marks.getText().toString()) ;
            intent.putExtra("percentage_10" , percent.getText().toString()) ;
            Bundle args = new Bundle() ;
            args.putSerializable("docs" , (Serializable) Arrays.asList(files));
            intent.putExtra("BUNDLE" , args) ;
            startActivity(intent);
        });

        submit.setOnClickListener(v -> {
            if(isValidated()){
                submitForm() ;
            }
        });

        prevIntent = getIntent() ;
        model = (MeritListModel) prevIntent.getSerializableExtra("MeritListModel");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void getImage(Chip chip){
        if(chip.getId() == markchip.getId()){
            req = 100 ;
        }
        else if(chip.getId() == allotchip.getId()){
            req = 150 ;
        }
        else{
            req = 200 ;
        }

        ImagePicker.Companion
                .with(this)
                .crop()
                .maxResultSize(1080,1080)
                .compress(1024)
                .start(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null)
            return ;

        if(resultCode != RESULT_OK)
            return;

        if(requestCode == 100){
            markImage = data.getData() ;
            marksheet.setImageURI(markImage);
            marksheet.setVisibility(View.VISIBLE);
            files[0] = markImage ;
        }
        else if(requestCode == 150){
            allotImage = data.getData() ;
            allotment.setImageURI(allotImage);
            allotment.setVisibility(View.VISIBLE);
            files[1] = allotImage ;
        }
        else{
            aadharImage = data.getData() ;
            aadhar.setImageURI(aadharImage);
            aadhar.setVisibility(View.VISIBLE);
            files[2] = aadharImage ;
        }
    }

    private boolean isValidateMarks(){
        if(marks.getText().toString().trim().equals("")){
            marks.setError("Invalid marks");
            marks.requestFocus() ;
            return false;
        }
        if(percent.getText().toString().trim().equals("")){
            percent.setError("Invalid marks");
            percent.requestFocus() ;
            return false;
        }

        float mark = Float.parseFloat(marks.getText().toString().trim()) ;
        float perc = Float.parseFloat(percent.getText().toString().trim()) ;

        if(mark <= 0.0){
            marks.setError("Invalid marks");
            marks.requestFocus() ;
            return false;
        }

        if(perc <= 0.0 || perc > 100.0){
            percent.setError("Invalid marks");
            percent.requestFocus() ;
            return false;
        }

       return true ;
    }

    private boolean isValidated(){
        if(!isValidateMarks()){
            return false;
        }

        if(markImage == null){
            Toast.makeText(this, "Please select Image of Marksheet", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(allotImage == null){
            Toast.makeText(this, "Please select Image of Allotment Letter", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(aadharImage == null){
            Toast.makeText(this, "Please select Image of Aadhar Card", Toast.LENGTH_SHORT).show();
            return false ;
        }

        return true ;
    }

    private void submitForm(){
        progressDialog = new ProgressDialog(this) ;
        progressDialog.setTitle("Submitting your form...");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WriteBatch batch = firestore.batch() ;

        DocumentReference doc1 = firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection("All Forms")
                .document(firebaseUser.getUid());

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

        DocumentReference doc2 = firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection(GlobalData.user.getYear())
                .document(gender)
                .collection(GlobalData.user.getBranch())
                .document(firebaseUser.getUid()) ;

        Task[] allUploadTasks = new Task[3] ;
        Task[] allGetTasks = new Task[3] ;

        for(int i = 0 ; i < 3 ; i++){
            int finalI = i;
            Task upload =  storage.child("MeritListDocs")
                    .child(model.getTitle())
                    .child(firebaseUser.getUid())
                    .child(fileNames[i])
                    .putFile(files[i])
                   .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if(task.isSuccessful()){
                               Task gt =  task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Uri> task) {
                                       if(task.isSuccessful()){
                                           urls[finalI] = task.getResult().toString() ;
                                       }
                                   }
                               }) ;
                               allGetTasks[finalI] = gt ;
                           }
                           else{
                               Toast.makeText(Merit_1st_year.this, "Unable to upload " + fileNames[finalI], Toast.LENGTH_SHORT).show();
                           }
                       }
                   }) ;

            allUploadTasks[i] = upload ;
        }

        Tasks.whenAllComplete(allUploadTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                            @Override
                            public void onSuccess(List<Task<?>> tasks) {
                                Tasks.whenAllComplete(allGetTasks)
                                        .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                                            @Override
                                            public void onSuccess(List<Task<?>> tasks) {
                                                MeritMarksModel marksModel = new MeritMarksModel() ;
                                                marksModel.setApproval("Unapproved");
                                                marksModel.setUser(GlobalData.user);

                                                Tenth_Marks tenth_marks = getTenthMarks() ;
                                                marksModel.setResult(tenth_marks);
                                                marksModel.setResultOf("10th");

                                                marksModel.setRejection("no rejection");
                                                marksModel.setKey(getMarksKey());

                                                batch.set(doc1 , marksModel) ;
                                                batch.set(doc2 , marksModel) ;

                                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(Merit_1st_year.this, "Successfully submitted form", Toast.LENGTH_SHORT).show();
                                                            AlertUtil.showAlertDialog(Merit_1st_year.this,
                                                                    "Success",
                                                                    "Form submitted successfully",
                                                                    true,
                                                                    R.drawable.information,
                                                                    "Ok");
                                                            progressDialog.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(Merit_1st_year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                                                            AlertUtil.showAlertDialog(Merit_1st_year.this,
                                                                    "Failure",
                                                                    "Failed to submit form",
                                                                    true,
                                                                    R.drawable.error,
                                                                    "Ok");
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                }) ;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Merit_1st_year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                                                AlertUtil.showAlertDialog(Merit_1st_year.this,
                                                        "Failure",
                                                        "Failed to submit form",
                                                        true,
                                                        R.drawable.error,
                                                        "Ok");
                                                progressDialog.dismiss();
                                            }
                                        }) ;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Merit_1st_year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                        AlertUtil.showAlertDialog(Merit_1st_year.this,
                                "Failure",
                                "Failed to submit form",
                                true,
                                R.drawable.error,
                                "Ok");
                        progressDialog.dismiss();
                    }
                }) ;


    }

    private Tenth_Marks getTenthMarks(){
        Tenth_Marks tenth_marks = new Tenth_Marks() ;
        tenth_marks.setMarksheet(urls[0]);
        tenth_marks.setAllotment(urls[1]);
        tenth_marks.setAadhar(urls[2]);

        tenth_marks.setMarks(marks.getText().toString().trim());
        tenth_marks.setPercent(percent.getText().toString().trim());

        tenth_marks.setTime(System.currentTimeMillis());

        return tenth_marks ;
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
        key += model.getTitle() + "/" ;
        key += GlobalData.user.getYear() + "/" ;
        key += gender + "/" ;
        key += GlobalData.user.getBranch() + "/" ;
        key += firebaseUser.getUid() ;

        return key ;
    }

}