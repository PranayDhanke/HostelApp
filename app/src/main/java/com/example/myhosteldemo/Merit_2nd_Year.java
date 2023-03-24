package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Marks.FirstYear_Marks;
import com.example.myhosteldemo.model.Marks.ITI_Marks;
import com.example.myhosteldemo.model.Marks.Twelth_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Merit_2nd_Year extends AppCompatActivity {

    Toolbar toolbar ;

    RadioGroup type , complete ;
    RadioButton regular , direct , th12 , iti ;

    CardView study ;
    LinearLayout ll1st , ll12th , llITI ;

    Button submit , view ;

    //1st year data
    EditText sem_one , sem_two ;
    ImageView img_semone , img_semtwo ;
    Chip chip_semone , chip_semtwo ;
    String[] regfiles = {"" , "" , "" , ""} ;
    Uri[] regUriFiles = new Uri[4] ;
    String[] regfilenames = {"Sem 1 marksheet" , "Sem 2 marksheet" , "Allotment" , "Aadhar"} ;

    //12th class data
    EditText twemarks , twepercent ;
    Chip twemarksheet ;
    ImageView tweimgmark ;
    String[] twefiles = {"" , "" , ""} ;
    Uri[] tweUriFiles = new Uri[4] ;
    String[] twefilenames = {"Marksheet" , "Allotment" , "Aadhar"} ;

    //ITI data
    EditText itipercent ;
    Chip itimarkchip ;
    ImageView itiimg ;
    String[] itifiles = {"" , "" , ""} ;
    Uri[] itiUriFiles = new Uri[4] ;
    String[] itifilenames = {"Marksheet" , "Allotment" , "Aadhar"} ;

    //allotment and aadhar
    Chip chip_allot , chip_aadhar ;
    ImageView img_allot , img_aadhar ;

    boolean bool_regular , bool_12th , bool_iti ;

    private static ProgressDialog progressDialog ;

    FirebaseUser user ;
    StorageReference storage ;
    FirebaseFirestore firestore ;

    Intent getIntent ;
    MeritListModel merit_model ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit2nd_year);

        toolbar = findViewById(R.id.merit_2nd_toolbar) ;
        type = findViewById(R.id.merit_2nd_type) ;
        complete = findViewById(R.id.merit_2nd_study) ;
        regular = findViewById(R.id.merit_2nd_regular) ;
        direct = findViewById(R.id.merit_2nd_direct) ;
        th12 = findViewById(R.id.merit_2nd_12th) ;
        iti = findViewById(R.id.merit_2nd_ITI) ;
        study = findViewById(R.id.merit_2nd_card_study) ;
        ll1st = findViewById(R.id.merit_2nd_ll_1styear) ;
        ll12th = findViewById(R.id.merit_2nd_ll_12th) ;
        llITI = findViewById(R.id.merit_2nd_ll_ITI) ;
        submit = findViewById(R.id.merit_2nd_submit) ;
        view = findViewById(R.id.merit_2nd_view) ;

        ll12th.setVisibility(View.GONE);
        llITI.setVisibility(View.GONE);

        bool_12th = bool_iti = false ;
        bool_regular = true ;

        //1st year data
        sem_one = findViewById(R.id.merit_2nd_1percent) ;
        sem_two = findViewById(R.id.merit_2nd_2percent) ;
        chip_semone = findViewById(R.id.merit_2nd_1chip) ;
        chip_semtwo = findViewById(R.id.merit_2nd_2chip) ;
        img_semone = findViewById(R.id.merit_2nd_1marksheet) ;
        img_semtwo = findViewById(R.id.merit_2nd_2marksheet) ;

        //12th class data
        twemarks = findViewById(R.id.merit_2nd_12mark) ;
        twepercent = findViewById(R.id.merit_2nd_12percent) ;
        twemarksheet = findViewById(R.id.merit_2nd_12marksheetchip) ;
        tweimgmark = findViewById(R.id.merit_2nd_12marksheetimage) ;

        //iti data
        itipercent = findViewById(R.id.merit_2nd_itipercent) ;
        itimarkchip = findViewById(R.id.merit_2nd_itimarkvggsheetchip) ;
        itiimg = findViewById(R.id.merit_2nd_itimarksheetiddmage) ;

        //allotment and aadhar
        chip_allot = findViewById(R.id.merit_2nd_allotmentchip) ;
        chip_aadhar = findViewById(R.id.merit_2nd_aadharchip) ;
        img_allot = findViewById(R.id.merit_2nd_allotmentimage) ;
        img_aadhar = findViewById(R.id.merit_2nd_aadharimage) ;

        progressDialog = new ProgressDialog(this) ;

        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == regular.getId()){
                    study.setVisibility(View.GONE);
                    ll1st.setVisibility(View.VISIBLE);
                    ll12th.setVisibility(View.GONE);
                    llITI.setVisibility(View.GONE);
                    Toast.makeText(Merit_2nd_Year.this, "Student type = Regular\nSelected", Toast.LENGTH_SHORT).show();
                    bool_12th = bool_iti = false ;
                    bool_regular = true ;
                }
                else{
                    study.setVisibility(View.VISIBLE);
                    ll1st.setVisibility(View.GONE);
                    ll12th.setVisibility(View.VISIBLE);
                    Toast.makeText(Merit_2nd_Year.this, "Student type = Direct Second Year\nSelected", Toast.LENGTH_SHORT).show();
                    bool_12th = true ; bool_iti = false ;
                    bool_regular = false ;
                    th12.setChecked(true);
                }
            }
        });

        complete.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == th12.getId()){
                    ll12th.setVisibility(View.VISIBLE);
                    llITI.setVisibility(View.GONE);
                    Toast.makeText(Merit_2nd_Year.this, "12th passed out DSY student\nSelected", Toast.LENGTH_SHORT).show();
                    bool_12th = true ; bool_iti = false ;
                    bool_regular = false ;
                }
                else{
                    llITI.setVisibility(View.VISIBLE);
                    ll12th.setVisibility(View.GONE);
                    Toast.makeText(Merit_2nd_Year.this, "ITI passed out DSY student\nSelected", Toast.LENGTH_SHORT).show();
                    bool_12th = false ; bool_iti = true ;
                    bool_regular = false ;
                }
            }
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(this , View_Form.class) ;

            if(bool_regular){
                intent.putExtra("1st" , true) ;
                intent.putExtra("semone" , sem_one.getText().toString().trim()) ;
                intent.putExtra("semtwo" , sem_two.getText().toString().trim()) ;
                Bundle bundle = new Bundle() ;
                bundle.putSerializable("docs" , (Serializable) Arrays.asList(regUriFiles));
                intent.putExtra("BUNDLE" , bundle) ;
                startActivity(intent);
            }
            else if(bool_12th){
                intent.putExtra("12th" , true) ;
                intent.putExtra("marks" , twemarks.getText().toString().trim()) ;
                intent.putExtra("percent" , twepercent.getText().toString().trim()) ;
                Bundle bundle = new Bundle() ;
                bundle.putSerializable("docs" , (Serializable) Arrays.asList(tweUriFiles));
                intent.putExtra("BUNDLE" , bundle) ;
                startActivity(intent);
            }
            else{
                intent.putExtra("ITI" , true) ;
                intent.putExtra("percent" , itipercent.getText().toString().trim()) ;
                Bundle bundle = new Bundle() ;
                bundle.putSerializable("docs" , (Serializable) Arrays.asList(itiUriFiles));
                intent.putExtra("BUNDLE" , bundle) ;
                startActivity(intent);
            }
        });

        submit.setOnClickListener(v -> {
            progressDialog.dismiss();
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Uploading your form");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            if(bool_regular){
                if(validateRegular()){
                    submitFormForRegular() ;
                }
                else{
                    progressDialog.dismiss();
                }
            }
            else if(bool_12th){
                if(validateTwelth()){
                    submitFormForTwelth() ;
                }
                else{
                    progressDialog.dismiss();
                }
            }
            else{
                if(validateITI()){
                    submitFormForITI() ;
                }
                else{
                    progressDialog.dismiss();
                }
            }
        });

        applyClickListeners() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference() ;
        firestore = FirebaseFirestore.getInstance() ;

        getIntent = getIntent() ;
        merit_model = (MeritListModel) getIntent.getSerializableExtra("MeritListModel");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private boolean validateRegular(){
        String sem1 = sem_one.getText().toString().trim() ;
        String sem2 = sem_two.getText().toString().trim() ;

        if(sem1.equals("")){
            sem_one.setError("Invalid Percentage" , null);
            sem_one.requestFocus() ;
            return false ;
        }
        if(Float.parseFloat(sem1) > 100.0f || Float.parseFloat(sem1) < 0.0f){
            sem_one.setError("Invalid Percentage" , null);
            sem_one.requestFocus() ;
            return false ;
        }
        if(sem2.equals("")){
            sem_two.setError("Invalid Percentage" , null);
            sem_two.requestFocus() ;
            return false ;
        }
        if(Float.parseFloat(sem2) > 100.0f || Float.parseFloat(sem2) < 0.0f){
            sem_two.setError("Invalid Percentage" , null);
            sem_two.requestFocus() ;
            return false ;
        }
        if(regfiles[0].equals("")){
            Toast.makeText(this, "Please select your 1st sem marksheet", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(regfiles[1].equals("")){
            Toast.makeText(this, "Please select your 2nd sem marksheet", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(regfiles[2].equals("")){
            Toast.makeText(this, "Please select your allotment letter", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(regfiles[3].equals("")){
            Toast.makeText(this, "Please select your aadhar card", Toast.LENGTH_SHORT).show();
            return false ;
        }
        return true ;
    }

    private boolean validateTwelth(){
        String marks = twemarks.getText().toString().trim() ;
        String percent = twepercent.getText().toString().trim() ;

        if(marks.equals("")){
            twemarks.setError("Invalid marks" , null);
            twemarks.requestFocus() ;
            return false ;
        }
        if(percent.equals("")){
            twepercent.setError("Invalid percentage" , null);
            twepercent.requestFocus() ;
            return false ;
        }
        if(Float.parseFloat(percent) > 100.0f || Float.parseFloat(percent) < 0.0f){
            twepercent.setError("Invalid Percentage" , null);
            twepercent.requestFocus() ;
            return false ;
        }
        if(twefiles[0].equals("")){
            Toast.makeText(this, "Please select marksheet", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(twefiles[1].equals("")){
            Toast.makeText(this, "Please select your allotment letter", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(twefiles[2].equals("")){
            Toast.makeText(this, "Please select your aadhar card", Toast.LENGTH_SHORT).show();
            return false ;
        }

        return true ;
    }

    private boolean validateITI(){
        String percent = itipercent.getText().toString().trim() ;

        if(percent.equals("")){
            itipercent.setError("Invalid percentage" , null);
            itipercent.requestFocus() ;
            return false ;
        }
        if(Float.parseFloat(percent) > 100.0f || Float.parseFloat(percent) < 0.0f){
            itipercent.setError("Invalid Percentage" , null);
            itipercent.requestFocus() ;
            return false ;
        }
        if(itifiles[0].equals("")){
            Toast.makeText(this, "Please select marksheet", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(itifiles[1].equals("")){
            Toast.makeText(this, "Please select your allotment letter", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(itifiles[2].equals("")){
            Toast.makeText(this, "Please select your aadhar card", Toast.LENGTH_SHORT).show();
            return false ;
        }

        return true ;
    }

    private void applyClickListeners(){
        chip_semone.setOnClickListener(v -> handleImage(110));
        chip_semtwo.setOnClickListener(v -> handleImage(120));
        twemarksheet.setOnClickListener(v -> handleImage(210));
        itimarkchip.setOnClickListener(v -> handleImage(310));
        chip_allot.setOnClickListener(v -> handleImage(400));
        chip_aadhar.setOnClickListener(v -> handleImage(500));

        img_semone.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        img_semtwo.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        img_aadhar.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        img_allot.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        tweimgmark.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        itiimg.setOnClickListener(v -> {
            if(((ImageView) v).getScaleType() == ImageView.ScaleType.CENTER_CROP){
                ((ImageView) v).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
    }

    private void handleImage(int req){
        ImagePicker.Companion
                .with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data == null)
            return;
        if(resultCode != RESULT_OK)
            return ;

        Uri uri = data.getData() ;
        Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();

        if(requestCode == 110){
            //regular
            uploadImage(regfilenames[0] , 0 , uri , 110) ;
        }
        else if(requestCode == 120){
            //regular
            uploadImage(regfilenames[1] , 1 , uri , 120) ;
        }
        else if(requestCode == 210){
            //12th
            uploadImage(twefilenames[0] , 0 , uri , 210) ;
        }
        else if(requestCode == 310){
            //iti
            uploadImage(itifilenames[0] , 0 , uri , 310) ;
        }
        else if(requestCode == 400){
            //allotment
            uploadImage("Allotment" , -1 , uri , 400) ;
        }
        else{
            //aadhar
            uploadImage("Aadhar" , -1 , uri , 500) ;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String name , int index , Uri uri , int req){
        progressDialog.dismiss();
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

        UploadTask task = storage.child("MeritListDocs")
                .child(merit_model.getTitle())
                .child(user.getUid())
                .child(name)
                .putFile(uri) ;

        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progress = (int) (snapshot.getBytesTransferred() / snapshot.getTotalByteCount() * 100);
                progressDialog.setProgress(progress);
            }
        }) ;

        final String[] url = {""};

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri urii) {
                                url[0] = urii.toString() ;

                                if(req == 110){
                                    //regular
                                    regfiles[0] = url[0] ;
                                    regUriFiles[0] = uri ;
                                    img_semone.setImageURI(uri);
                                    img_semone.setVisibility(View.VISIBLE);
                                }
                                else if(req == 120){
                                    //regular
                                    regfiles[1] = url[0] ;
                                    regUriFiles[1] = uri ;
                                    img_semtwo.setImageURI(uri);
                                    img_semtwo.setVisibility(View.VISIBLE);
                                }
                                else if(req == 210){
                                    //12th
                                    twefiles[0] = url[0] ;
                                    tweUriFiles[0] = uri ;
                                    tweimgmark.setImageURI(uri);
                                    tweimgmark.setVisibility(View.VISIBLE);
                                }
                                else if(req == 310){
                                    //iti
                                    itifiles[0] = url[0] ;
                                    itiUriFiles[0] = uri ;
                                    itiimg.setImageURI(uri);
                                    itiimg.setVisibility(View.VISIBLE);
                                }
                                else if(req == 400){
                                    //allotment
                                    regfiles[2] = url[0] ;
                                    regUriFiles[2] = uri ;
                                    twefiles[1] = url[0];
                                    tweUriFiles[1] = uri ;
                                    itifiles[1] = url[0] ;
                                    itiUriFiles[1] = uri ;
                                    img_allot.setImageURI(uri);
                                    img_allot.setVisibility(View.VISIBLE);
                                }
                                else{
                                    //aadhar
                                    regfiles[3] = url[0] ;
                                    regUriFiles[3] = uri ;
                                    twefiles[2] = url[0] ;
                                    tweUriFiles[2] = uri ;
                                    itifiles[2] = url[0] ;
                                    itiUriFiles[2] = uri ;
                                    img_aadhar.setImageURI(uri);
                                    img_aadhar.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(Merit_2nd_Year.this, "Successfully uploaded " + name, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Merit_2nd_Year.this, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
                                        "Failure",
                                        "Failed to upload Image",
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
                Toast.makeText(Merit_2nd_Year.this, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
                        "Failure",
                        "Failed to upload Image",
                        true,
                        R.drawable.error,
                        "Ok");
                progressDialog.dismiss();
            }
        }) ;
    }

    private void submitFormForRegular(){
        MeritMarksModel meritMarksModel = new MeritMarksModel() ;
        meritMarksModel.setRejection("no rejection");
        meritMarksModel.setResultOf("1st year");
        meritMarksModel.setApproval("Unapproved");

        FirstYear_Marks model = new FirstYear_Marks() ;
        model.setFirst_sem_percentage(Float.parseFloat(sem_one.getText().toString().trim()));
        model.setSecond_sem_percentage(Float.parseFloat(sem_two.getText().toString().trim()));
        model.setMarksheet_first_sem(regfiles[0]);
        model.setMarksheet_second_sem(regfiles[1]);
        model.setAllotment(regfiles[2]);
        model.setAadhar(regfiles[3]);
        model.setTime(System.currentTimeMillis());

        meritMarksModel.setUser(GlobalData.user);
        meritMarksModel.setResult(model);

        WriteBatch batch = firestore.batch() ;

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

        DocumentReference doc1 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection("All Forms")
                .document(user.getUid());

        DocumentReference doc2 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection(GlobalData.user.getYear())
                .document(gender)
                .collection(GlobalData.user.getBranch())
                .document(user.getUid()) ;

        batch.set(doc1 , meritMarksModel) ;
        batch.set(doc2 , meritMarksModel) ;

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Merit_2nd_Year.this, "Successfully submitted form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
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
                Toast.makeText(Merit_2nd_Year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
                        "Failure",
                        "Failed to submit form",
                        true,
                        R.drawable.error,
                        "Ok");
                progressDialog.dismiss();
            }
        }) ;
    }

    private void submitFormForTwelth(){
        MeritMarksModel meritMarksModel = new MeritMarksModel() ;
        meritMarksModel.setRejection("no rejection");
        meritMarksModel.setApproval("Unapproved");
        meritMarksModel.setResultOf("12th");

        Twelth_Marks model = new Twelth_Marks() ;
        model.setMarks(Float.parseFloat(twemarks.getText().toString().trim()));
        model.setPercent(Float.parseFloat(twepercent.getText().toString().trim()));
        model.setMarksheet(twefiles[0]);
        model.setAllotment(twefiles[1]);
        model.setAadhar(twefiles[2]);
        model.setTime(System.currentTimeMillis());

        meritMarksModel.setUser(GlobalData.user);
        meritMarksModel.setResult(model);

        WriteBatch batch = firestore.batch() ;

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

        DocumentReference doc1 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection("All Forms")
                .document(user.getUid());

        DocumentReference doc2 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection(GlobalData.user.getYear())
                .document(gender)
                .collection(GlobalData.user.getBranch())
                .document(user.getUid()) ;

        batch.set(doc1 , meritMarksModel) ;
        batch.set(doc2 , meritMarksModel) ;

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Merit_2nd_Year.this, "Successfully submitted form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
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
                Toast.makeText(Merit_2nd_Year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
                        "Failure",
                        "Failed to submit form",
                        true,
                        R.drawable.error,
                        "Ok");
                progressDialog.dismiss();
            }
        }) ;
    }

    private void submitFormForITI(){
        MeritMarksModel meritMarksModel = new MeritMarksModel() ;
        meritMarksModel.setResultOf("ITI");
        meritMarksModel.setRejection("no rejection");
        meritMarksModel.setApproval("Unapproved");

        ITI_Marks model = new ITI_Marks() ;
        model.setPercentage(Float.parseFloat(itipercent.getText().toString().trim()));
        model.setMarksheet(itifiles[0]);
        model.setAllotment(itifiles[1]);
        model.setAadhar(itifiles[2]);
        model.setTime(System.currentTimeMillis());

        meritMarksModel.setUser(GlobalData.user);
        meritMarksModel.setResult(model);

        WriteBatch batch = firestore.batch() ;

        String gender = "" ;

        if(GlobalData.user.getGender().equals("male")){
            gender = "Boys" ;
        }
        else{
            gender = "Girls" ;
        }

        DocumentReference doc1 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection("All Forms")
                .document(user.getUid());

        DocumentReference doc2 = firestore.collection("MeritListData")
                .document(merit_model.getTitle())
                .collection(GlobalData.user.getYear())
                .document(gender)
                .collection(GlobalData.user.getBranch())
                .document(user.getUid()) ;

        batch.set(doc1 , meritMarksModel) ;
        batch.set(doc2 , meritMarksModel) ;

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Merit_2nd_Year.this, "Successfully submitted form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
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
                Toast.makeText(Merit_2nd_Year.this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                AlertUtil.showAlertDialog(Merit_2nd_Year.this,
                        "Failure",
                        "Failed to submit form",
                        true,
                        R.drawable.error,
                        "Ok");
                progressDialog.dismiss();
            }
        }) ;
    }

}