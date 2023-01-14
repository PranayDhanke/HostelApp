package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Firebase
    FirebaseAuth mAuth ;
    FirebaseUser fuser ;
    FirebaseDatabase database ;
    FirebaseStorage storage ;

    //SharedPreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;

    Toolbar toolbar ;
    DrawerLayout drawerLayout ;
    NavigationView navigationView ;
    CircleImageView header_pic ;
    TextView header_email , header_name ;
    View header ;

    AlertDialog.Builder alertDialog  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_demo);

        changeColorOfStatusBar(MainActivity.this , R.color.cyandark) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        fuser = mAuth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;

        editor.putBoolean(fuser.getUid() , true) ;
        editor.apply();

        alertDialog = new AlertDialog.Builder(this) ;

        try{
            changeContentView();
        }
        catch (Exception e){
            alertDialog.setTitle("Loading Result") ;
            alertDialog.setMessage("Failed to load your data") ;
            alertDialog.setCancelable(false) ;
            alertDialog.setIcon(R.drawable.error) ;
            alertDialog.setPositiveButton("Retry" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    changeContentView();
                }
            }) ;
            alertDialog.show() ;
        }

    }

    private void changeContentView() {
        if(GlobalData.user != null){
            initialiseAll();
            return ;
        }

        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            initialiseAll() ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        try {
                            throw new Exception("Canceled") ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initialiseAll(){
        setContentView(R.layout.activity_main);
        changeColorOfStatusBar(MainActivity.this , R.color.cyandark) ;
        toolbar = findViewById(R.id.main_toolbar) ;
        drawerLayout = findViewById(R.id.drawer) ;
        navigationView = findViewById(R.id.nav) ;
        header = navigationView.getHeaderView(0) ;
        header_pic = header.findViewById(R.id.head_prof) ;
        header_email = header.findViewById(R.id.head_email) ;
        header_name = header.findViewById(R.id.head_name) ;

        setUpNavigationDrawerMenu() ;

        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        preLoadHeader() ;
    }

    private void setUpNavigationDrawerMenu(){
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this
                        , drawerLayout
                        , toolbar
                        , R.string.draweropen
                        , R.string.drawerclosed) ;

        drawerToggle.getDrawerArrowDrawable().setColor(this.getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String itemName = item.getTitle().toString() ;
        closedrawer() ;
        Intent intent ;

        switch (item.getItemId()){
            case R.id.nav_profile:
                intent = new Intent(MainActivity.this , SetupAccount.class) ;
                intent.putExtra("MainActivity" , true) ;
                startActivity(intent);
                break;
            case R.id.nav_update_password:
                intent = new Intent(MainActivity.this , ForgotPassword.class) ;
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logout() ;
                break;
        }

        return false;
    }

    private void showDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closedrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closedrawer();
        }
        else{
            super.onBackPressed();
        }
    }

    public void preLoadHeader(){
        if(header_pic == null){
           //Toast.makeText(this, "Header pic is null", Toast.LENGTH_SHORT).show();
        }

        if(GlobalData.user != null){
            header_email.setText(GlobalData.user.getEmail());
            header_name.setText(GlobalData.user.getUsername());
        }
        else{
            setUserFromDatabase() ;
        }

        if(GlobalData.profile != null){
            header_pic.setImageDrawable(GlobalData.profile);
        }
        if(GlobalData.profile_uri != null){
            header_pic.setImageURI(GlobalData.profile_uri);
        }
        else{
            getProfileToImage();
        }

    }

    private void getProfileToImage(){
        storage.getReference().child("Profile_Pictures").child(fuser.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(SetupAccount.this, "Task successful", Toast.LENGTH_SHORT).show();
                    try {
                        Glide.with(MainActivity.this)
                                .load(task.getResult().toString())
                                .placeholder(R.drawable.profile_pic3)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Toast.makeText(MainActivity.this, "Failed to load Profile Picture\n" + e, Toast.LENGTH_LONG).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        GlobalData.profile = resource;
                                        return false;
                                    }
                                })
                                .into(header_pic);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else{
                    loadImageFromAuth();
                    //do nothing
                    //Toast.makeText(SetupAccount.this, "Task failed", Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }

    private void loadImageFromAuth(){
        Glide.with(MainActivity.this)
                .load(fuser.getPhotoUrl())
                .placeholder(R.drawable.profile_pic3)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //Toast.makeText(MainActivity.this, "No Profile picture set", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        GlobalData.profile = resource ;
                        return false;
                    }
                })
                .into(header_pic) ;
    }

    private void setUserFromDatabase(){
        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            header_email.setText(user.getEmail());
                            header_name.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    protected void onResume() {
        preLoadHeader();
        super.onResume();
    }

    private void logout(){
                ProgressDialog dialog = new ProgressDialog(MainActivity.this) ;
                dialog.setCancelable(false);
                dialog.setTitle("Logout");
                dialog.setTitle("Please wait....");
                dialog.setIcon(R.drawable.information);

                dialog.show() ;

                mAuth.signOut() ;

                startActivity(new Intent(MainActivity.this , SignIn.class));
                Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
    }


}