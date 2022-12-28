package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

//import com.github.drjacky.imagepicker.ImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.Utility.SaveUserData;
import com.example.myhosteldemo.Utility.ValidateUser;
import com.example.myhosteldemo.model.User;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupAccount extends AppCompatActivity {

    Toolbar toolbar ;
    CollapsingToolbarLayout collapsingToolbarLayout ;

    //for user information
    CircleImageView image ;
    EditText username , mobile , enroll ;
    Spinner gender , branch , year ;
    FloatingActionButton fab ;
    ProgressDialog progress ;
    Button dob ;

    //for spinner
    String[] genders , branches , years ;
    boolean gflag = false , bflag = false , yflag = false ;

    //Array adapters
    ArrayAdapter ga , ba , ya ;

    //for save button
    Button save ;

    //request codes
    private final int GALLERY_REQUEST = 100 ;

    //firebase
    FirebaseAuth auth ;
    FirebaseUser fuser ;
    FirebaseStorage storage ;
    FirebaseDatabase database ;

    //datepicker dialog
    DatePickerDialog dobDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        image = findViewById(R.id.profile_image) ;
        username = findViewById(R.id.username) ;
        mobile = findViewById(R.id.phone) ;
        enroll = findViewById(R.id.enroll) ;
        save = findViewById(R.id.save) ;
        gender = findViewById(R.id.gender) ;
        branch = findViewById(R.id.branch) ;
        year = findViewById(R.id.year) ;
        dob = findViewById(R.id.dob) ;
        fab = findViewById(R.id.fab) ;

        storage = FirebaseStorage.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        fuser = auth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;

        progress = new ProgressDialog(SetupAccount.this) ;

        initDatePicker() ;

        getProfileToImage() ;

        genders = new String[]{"<--Select-->" , "male" , "female" , "other"};
        branches = new String[]{"<--Select-->" , "Computer" , "Mechanical" , "Chemical" , "Electrical" , "Electronics" , "Civil"};
        years = new String[]{"<--Select-->" , "1st year" , "2nd year" , "3rd year"};

        ga = new ArrayAdapter(this, R.layout.drop_down_item,genders) ;
        ga.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        ba = new ArrayAdapter(this, R.layout.drop_down_item,branches) ;
        ba.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        ya = new ArrayAdapter(this, R.layout.drop_down_item,years) ;
        ya.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        gender.setAdapter(ga);
        branch.setAdapter(ba);
        year.setAdapter(ya);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0){
                    if(gflag == true)
                        Toast.makeText(SetupAccount.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    gflag = true ;
                }
                else{
                    //Toast.makeText(SetupAccount.this, "Selected item = " + adapterView.getSelectedItem(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0){
                    if(bflag == true)
                        Toast.makeText(SetupAccount.this, "Please select your branch", Toast.LENGTH_SHORT).show();
                    bflag = true ;
                }
                else{
                    //Toast.makeText(SetupAccount.this, "Selected item = " + adapterView.getSelectedItem(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0){
                    if(yflag == true)
                        Toast.makeText(SetupAccount.this, "Please select study of year", Toast.LENGTH_SHORT).show();
                    yflag = true ;
                }
                else{
                    //Toast.makeText(SetupAccount.this, "Selected item = " + adapterView.getSelectedItem(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dob.setOnClickListener(v -> dobDialog.show());

        save.setOnClickListener(v -> saveUserData());

        image.setOnClickListener(v -> handleImage());

        fab.setOnClickListener(v -> handleImage());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                if(data != null){
                    uploadProfilePic(data.getData()) ;
                }
                else{
                    Toast.makeText(SetupAccount.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void saveUserData(){
        User user = createUser() ;
        ValidateUser validateUser = new ValidateUser(user , SetupAccount.this) ;
        if(!validateUser.validateUser()){
            return ;
        }
        else{
            ProgressDialog dialog = new ProgressDialog(SetupAccount.this) ;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Updating...");
            dialog.setMessage("Saving Profile Changes....");
            dialog.setIcon(R.drawable.information);
            dialog.show();

            try{
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SetupAccount.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                    showAlertDialog(SetupAccount.this
                                            , "Updation Result"
                                            , "Profile changes saved successfully"
                                            , true
                                            , R.drawable.success
                                            , "Ok") ;
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(SetupAccount.this, "Failed to store data\n" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    showAlertDialog(SetupAccount.this
                                            , "Updation Result"
                                            , "Failed to save profile changes"
                                            , true
                                            , R.drawable.error
                                            , "Ok") ;
                                    dialog.dismiss();
                                }
                            }
                        }) ;
            }catch (Exception e){
                dialog.dismiss();
                //Toast.makeText(context , "Cann't save data\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleImage(){
//        Intent intent = new Intent(Intent.ACTION_PICK) ;
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
//        startActivityForResult(intent , GALLERY_REQUEST) ;

        ImagePicker.Companion.with(SetupAccount.this)
                .crop()
                .cropOval()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start(GALLERY_REQUEST) ;
    }

    private void uploadProfilePic(Uri uri){
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setTitle("Updating your Profile Pic");
        progress.setMax(100);
        progress.setCanceledOnTouchOutside(false);

        StorageReference reference = storage.getReference()
                .child("Profile_Pictures")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) ;

        progress.show();

        UploadTask uploadTask = reference.putFile(uri) ;

        progress.setOnCancelListener(v -> uploadTask.cancel());
        progress.setOnDismissListener(v -> uploadTask.cancel());

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double counter = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progress.setProgress((int)counter);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }) ;
//                .addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Toast.makeText(SetupAccount.this, "Updation of Profile pic cancelled", Toast.LENGTH_SHORT).show();
//            }
//        }) ;


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    progress.dismiss();
                    image.setImageURI(uri);
                    Toast.makeText(SetupAccount.this, "Profile pic has been updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(SetupAccount.this, "Failed to save image\nError = " +task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++ ;
                String date = day + "/" + month + "/" + year ;
                dob.setText(date);
            }
        } ;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) ;
        int month = cal.get(Calendar.MONTH) + 1 ;
        int day = cal.get(Calendar.DAY_OF_MONTH) ;

        dobDialog = new DatePickerDialog(SetupAccount.this , AlertDialog.THEME_HOLO_LIGHT , dateSetListener , year , month, day) ;
        dobDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        try {
            dobDialog.getDatePicker().setMinDate(new Date(String.valueOf(new SimpleDateFormat("dd/MM/yyyy").parse(day + "/" + month + "/" + (year - 50)))).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private User createUser(){
        String email = fuser.getEmail() ;
        String profile_url = storage.getReference().child("Profile_Pictures").child(fuser.getUid()).getDownloadUrl().toString()  ;
        String name = username.getText().toString() ;
        String mob = mobile.getText().toString() ;
        String enrol = enroll.getText().toString() ;
        String bran = branch.getSelectedItem().toString() ;
        String yea = year.getSelectedItem().toString() ;
        String gen = gender.getSelectedItem().toString() ;
        String date = dob.getText().toString() ;

        final String[] password = {""};

        DatabaseReference reference = database.getReference() ;

        reference.child("Users").child(fuser.getUid()).child("password").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists())
                        password[0] = task.getResult().getValue().toString() ;
                }
                else{
                    password[0] = "" ;
                }
            }
        }) ;

        User user = new User(name , email , password[0] ,
                            profile_url , mob , enrol ,
                            bran , yea , gen , date ,
                            true , false) ;

        return user ;
    }

    private void getProfileToImage(){
        storage.getReference().child("Profile_Pictures").child(fuser.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(SetupAccount.this, "Task successful", Toast.LENGTH_SHORT).show();
                   Glide.with(SetupAccount.this)
                           .load(task.getResult().toString())
                           .placeholder(R.drawable.profile_pic)
                           //.onlyRetrieveFromCache(true)
                           .addListener(new RequestListener<Drawable>() {
                               @Override
                               public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                   Toast.makeText(SetupAccount.this, "Failed to load\n" + e, Toast.LENGTH_LONG).show();
                                   return false;
                               }

                               @Override
                               public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                   return false;
                               }
                           })
                           .into(image) ;
                }
                else{
                    //do nothing
                    //Toast.makeText(SetupAccount.this, "Task failed", Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }

}