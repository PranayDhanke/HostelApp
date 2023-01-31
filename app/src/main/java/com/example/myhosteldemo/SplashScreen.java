package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;
import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    long start , end ;

    //Firebase
    FirebaseAuth mAuth ;
    FirebaseDatabase database ;
    FirebaseUser fuser ;
    FirebaseStorage storage ;

    //Sharedpreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        changeColorOfStatusBar(this , R.color.white) ;

        start = System.currentTimeMillis() ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        fuser = mAuth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;

        if(fuser != null){
            boolean remember = signinpref.getBoolean("remember" , false) ;
                List list = fuser.getProviderData() ;
                UserInfo info = (UserInfo) list.get(list.size() - 1) ;
                String provider = info.getProviderId() ;
                if(provider.contains("google.com")){
                    //Toast.makeText(this, "Signing using google\n"+fuser.getEmail(), Toast.LENGTH_SHORT).show();
                    goForNext(fuser , false);
                }
                else if(provider.contains("facebook.com")){
                    //Toast.makeText(this, "Signing using facebook\n"+fuser.getEmail(), Toast.LENGTH_SHORT).show();
                    goForNext(fuser , false);
                }
                else{
                    if(remember){
                       // Toast.makeText(this, "Signing using email\n"+fuser.getEmail(), Toast.LENGTH_SHORT).show();
                        goForNext(fuser , true);
                    }
                    else{
                        goForNext(fuser , true);
                    }
                }

            }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Toast.makeText(SplashScreen.this, "In else : " + time, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashScreen.this , SignIn.class));
                    finish();
                }
            } , 2000) ;
        }



    }


    private void goForNext(FirebaseUser fuser , boolean email){
        if(signinpref.getBoolean(fuser.getUid() , false)){
            preLoadGlobalData();
            end = System.currentTimeMillis() ;
            long time = end - start ;
            if(time > 2000){
               // Toast.makeText(this, "In if : " + time, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreen.this , MainActivity.class));
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(SplashScreen.this, "In else : " + time, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashScreen.this , MainActivity.class));
                        finish();
                    }
                } , 2000) ;
            }
            return;
        }

        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user ;
                        Intent intent ;
                        if(snapshot.exists()){
                            user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            if(user.isSetuped()){
                                intent = new Intent(new Intent(SplashScreen.this , MainActivity.class)) ;
                                startActivity(intent);
                            }
                            else{
                                intent = new Intent(new Intent(SplashScreen.this , SetupAccount.class)) ;
                                intent.putExtra("pending" , true) ;
                                if(email){
                                    intent.putExtra("name" , user.getUsername()) ;
                                }
                                else{
                                    intent.putExtra("name" , fuser.getDisplayName()) ;
                                }
                                intent.putExtra("email" , fuser.getEmail()) ;
                                startActivity(intent);
                                finish() ;
                            }
                            finish();
                        }
                        else{
                            intent = new Intent(new Intent(SplashScreen.this , SetupAccount.class)) ;
                            intent.putExtra("pending" , true) ;
                            if(email){
                                intent.putExtra("name" , signinpref.getString("username" , fuser.getDisplayName())) ;
                            }
                            else{
                                intent.putExtra("name" , fuser.getDisplayName()) ;
                            }
                            intent.putExtra("email" , fuser.getEmail()) ;
                            startActivity(intent);
                            finish() ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                       startActivity(new Intent(SplashScreen.this , SignIn.class));
                    }
                });
    }

    private void preLoadGlobalData(){
        try {
            database.getReference().child("Users").child(fuser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                GlobalData.user = snapshot.getValue(User.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}