package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeColorOfStatusBar(this , R.color.cyandark) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        fuser = mAuth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;

        editor.putBoolean(fuser.getUid() , true) ;
        editor.apply();

        toolbar = findViewById(R.id.main_toolbar) ;
        drawerLayout = findViewById(R.id.drawer) ;
        navigationView = findViewById(R.id.nav) ;

        setUpNavigationDrawerMenu() ;

        navigationView.setNavigationItemSelectedListener(this);
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

        switch (item.getItemId()){

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
}