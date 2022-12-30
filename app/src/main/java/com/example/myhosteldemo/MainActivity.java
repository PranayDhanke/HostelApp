package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    //Firebase
    FirebaseAuth mAuth ;
    FirebaseUser fuser ;
    FirebaseDatabase database ;
    FirebaseStorage storage ;

    //SharedPreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeColorOfStatusBar(this , R.color.cyan) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        fuser = mAuth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;

        editor.putBoolean(fuser.getUid() , true) ;
        editor.apply();
    }
}