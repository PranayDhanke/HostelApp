package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;
import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

//import com.github.drjacky.imagepicker.ImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.Utility.SaveUserData;
import com.example.myhosteldemo.Utility.ValidateUser;
import com.example.myhosteldemo.model.User;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupAccount extends AppCompatActivity {

    Toolbar toolbar ;
    AppBarLayout appBarLayout ;
    CollapsingToolbarLayout collapsingToolbarLayout ;
    FloatingActionButton back ;

    //for user information
    CircleImageView image ;
    EditText username , mobile , enroll ;
    Spinner gender , branch , year ;
    FloatingActionButton fab ;
    ProgressDialog progress ;
    Button dob ;

    //for spinner
    ArrayList<String> genders = new ArrayList<String>() ;
    ArrayList<String> branches = new ArrayList<String>() ;
    ArrayList<String> years = new ArrayList<String>() ;

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

    //sharedpreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;


    public SetupAccount(){
        genders.add("<--Select-->") ; genders.add("male") ; genders.add("female") ; genders.add("others") ;
        branches.add("<--Select-->") ; branches.add("Computer") ; branches.add("Mechanical") ; branches.add("Chemical") ; branches.add("Electrical") ; branches.add("Electronics") ; branches.add("Civil") ;
        years.add("<--Select-->") ; years.add("1st year") ; years.add("2nd year") ; years.add("3rd year") ;
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        changeColorOfStatusBar(this , R.color.black) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

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
        collapsingToolbarLayout = findViewById(R.id.collapse) ;
        appBarLayout = findViewById(R.id.appbar) ;
        toolbar = findViewById(R.id.toolbar) ;


        storage = FirebaseStorage.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        fuser = auth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;

        setSupportActionBar(toolbar);
        progress = new ProgressDialog(SetupAccount.this) ;

        initDatePicker() ;

        //getProfileToImage() ;

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

        appBarLayout.addOnOffsetChangedListener((a,b) -> offsetChanged(a,b));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
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
        ProgressDialog dialog = new ProgressDialog(SetupAccount.this) ;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Updating...");
        dialog.setMessage("Saving Profile Changes....");
        dialog.setIcon(R.drawable.information);
        dialog.show();

        final User[] user = new User[1];
        String email = fuser.getEmail() ;

        String name = username.getText().toString() ;
        String mob = mobile.getText().toString() ;
        String enrol = enroll.getText().toString() ;
        String bran = branch.getSelectedItem().toString() ;
        String yea = year.getSelectedItem().toString() ;
        String gen = gender.getSelectedItem().toString() ;
        String date = dob.getText().toString() ;
        final String[] pass = {""};
        final String[] url = {""};

        user[0] = createUser() ;

        ValidateUser validateUser = new ValidateUser(user[0], SetupAccount.this) ;
        if(!validateUser.validateUser()){
            dialog.dismiss();
            return ;
        }

        database.getReference().child("Users/" + fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            pass[0] = snapshot.getValue(User.class).getPassword() ;
                        }
                        else{
                            pass[0] = "" ;
                        }
                        //getting image url
                        storage.getReference().child("Profile_Pictures/").child(fuser.getUid())
                                .getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()) {
                                            if (task.getResult() == null) {
                                                url[0] = "";
                                            } else {
                                                url[0] = task.getResult().toString();

                                            }
                                        }
                                        else if(task.getException().getLocalizedMessage().equals("Object does not exist at location.")) {
                                            List list = fuser.getProviderData() ;
                                            UserInfo info = (UserInfo) list.get(list.size() - 1) ;
                                            String provider = info.getProviderId() ;
                                            if(provider.contains("google.com") || provider.contains("facebook.com")){
                                                url[0] = fuser.getPhotoUrl().toString() ;
                                            }
                                            else{
                                                url[0] = "";
                                            }
                                        }
                                        else{
                                            Toast.makeText(SetupAccount.this, "This is storage\nFailed to store data\n" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            showAlertDialog(SetupAccount.this
                                                    , "Updation Result"
                                                    , "Failed to save profile changes"
                                                    , true
                                                    , R.drawable.error
                                                    , "Ok") ;
                                            dialog.dismiss();
                                            return ;
                                        }

                                        //saving user data
                                        user[0] = new User(name , email , pass[0] ,
                                                url[0], mob , enrol ,
                                                bran , yea , gen , date ,
                                                true , true) ;

                                        try{
                                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user[0])
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                GlobalData.user = user[0];
                                                                editor.putBoolean(fuser.getUid() , true) ;
                                                                editor.apply();
                                                                Toast.makeText(SetupAccount.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SetupAccount.this) ;
                                                                alertDialog.setTitle("Result") ;
                                                                alertDialog.setMessage("Profile changes saved successfully") ;
                                                                alertDialog.setCancelable(false) ;
                                                                alertDialog.setIcon(R.drawable.information) ;
                                                                alertDialog.setPositiveButton("Ok" , new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        Intent intent = getIntent() ;
                                                                        if(intent.getBooleanExtra("pending" , false)){
                                                                            startActivity(new Intent(SetupAccount.this , MainActivity.class));
                                                                            finish();
                                                                        }
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                }) ;
                                                                alertDialog.show() ;
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
                                }) ;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SetupAccount.this, "Failed to store data\n" + error.toException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        showAlertDialog(SetupAccount.this
                                , "Updation Result"
                                , "Failed to save profile changes"
                                , true
                                , R.drawable.error
                                , "Ok") ;
                        dialog.dismiss();
                    }
                });
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

//        String path = uri.getPath().toString() ;
//        String name = path.substring(path.lastIndexOf("/")) ;

//        StorageReference reference = storage.getReference()
//                .child("Profile_Pictures")
//                .child(fuser.getUid() + "/" + name) ;

        StorageReference reference = storage.getReference()
                .child("Profile_Pictures")
                .child(fuser.getUid()) ;

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
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                String url = task.getResult().toString() ;
                                Map<String , Object> hash = new HashMap() ;
                                hash.put("profile" , url) ;

                                database.getReference().child("Users/" + fuser.getUid() + "/")
                                        .updateChildren(hash)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progress.dismiss();
                                                    image.setImageURI(uri);
                                                    GlobalData.profile_uri = uri ;
                                                    Toast.makeText(SetupAccount.this, "Profile pic has been updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    progress.dismiss();
                                                    Toast.makeText(SetupAccount.this, "Failed to save image\nError = " +task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }) ;
                            }
                            else{
                                progress.dismiss();
                                Toast.makeText(SetupAccount.this, "Failed to save image\nError = " +task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) ;

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
                String date = (day < 10 ? (0 + Integer.toString(day)) : day) + "/" + (month < 10 ? (0 + Integer.toString(month)) : month) + "/" + year ;
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
        User user ;
        String email = fuser.getEmail() ;

        String name = username.getText().toString() ;
        String mob = mobile.getText().toString() ;
        String enrol = enroll.getText().toString() ;
        String bran = branch.getSelectedItem().toString() ;
        String yea = year.getSelectedItem().toString() ;
        String gen = gender.getSelectedItem().toString() ;
        String date = dob.getText().toString() ;

        user= new User(name , email , "" ,
                "", mob , enrol ,
                            bran , yea , gen , date ,
                            true , true) ;

        return user;
    }


    private void preLoadData(){
        Intent intent = getIntent() ;
        if(intent.getBooleanExtra("pending" , false)){
            collapsingToolbarLayout.setTitle(intent.getStringExtra("email"));
            username.setText(intent.getStringExtra("name"));
            //loadImageFromAuth();
            getProfileToImage("");
        }
        else if(getIntent().getBooleanExtra("MainActivity" , false)){
            collapsingToolbarLayout.setTitle(GlobalData.user.getEmail());
            username.setText(GlobalData.user.getUsername());
            mobile.setText(GlobalData.user.getPhone());
            enroll.setText(GlobalData.user.getEnroll());
            branch.setSelection(branches.indexOf(GlobalData.user.getBranch()));
            //Toast.makeText(this, "Size : " + branches.size() + "index : " + branches.indexOf(GlobalData.user.getBranch()) +"\nData : " + GlobalData.user.getBranch(), Toast.LENGTH_SHORT).show();
            year.setSelection(years.indexOf(GlobalData.user.getYear()));
            gender.setSelection(genders.indexOf(GlobalData.user.getGender()));
            dob.setText(GlobalData.user.getDob());
            if(GlobalData.profile != null){
                image.setImageDrawable(GlobalData.profile);
            }
            if(GlobalData.profile_uri != null){
                image.setImageURI(GlobalData.profile_uri);
            }
            else{
                getProfileToImage(GlobalData.user.getProfile());
            }
        }
    }

    private void getProfileToImage(String url){
        if(url != null && !url.equals("")){
            Glide.with(SetupAccount.this)
                    .load(url)
                    .placeholder(R.drawable.profile_pic3)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //Toast.makeText(SetupAccount.this, "Failed to load Profile Picture\n" + e, Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            GlobalData.profile = resource ;
                            return false;
                        }
                    })
                    .into(image);
        }
        else{
            loadImageFromAuth();
        }

    }

    private void loadImageFromAuth(){
       Glide.with(SetupAccount.this)
               .load(fuser.getPhotoUrl())
               .placeholder(R.drawable.profile_pic3)
               .addListener(new RequestListener<Drawable>() {
                   @Override
                   public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                       //Toast.makeText(SetupAccount.this, "No Profile picture set", Toast.LENGTH_SHORT).show();
                       return false;
                   }

                   @Override
                   public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                       GlobalData.profile = resource ;
                       return false;
                   }
               })
               .into(image) ;
    }


    private void offsetChanged(AppBarLayout appBarLayout , int verticalOffset){
        if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
        {
            //  Collapsed
            changeColorOfStatusBar(this , R.color.red);

        }
        else
        {
            //Expanded
            changeColorOfStatusBar(this , R.color.black);

        }
    }


    //onstart activity


    @Override
    protected void onStart() {
        ProgressDialog progressDialog = new ProgressDialog(this) ;
        progressDialog.setMessage("Loading....please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        preLoadData();

        progressDialog.dismiss();

        super.onStart();
    }
}


// Glide.with(SetupAccount.this)
//         .load(task.getResult().toString())
//         .placeholder(R.drawable.profile_pic3)
//         .addListener(new RequestListener<Drawable>() {
//@Override
//public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//        Toast.makeText(SetupAccount.this, "Failed to load Profile Picture\n" + e, Toast.LENGTH_LONG).show();
//        return false;
//        }
//
//@Override
//public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//        GlobalData.profile = resource ;
//        return false;
//        }
//        })
//        .into(image);

//    loadImageFromAuth();