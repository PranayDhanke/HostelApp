package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.PathUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myhosteldemo.Services.UploadService;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Resources extends AppCompatActivity {

    Toolbar toolbar ;
    RelativeLayout computer , mechanical , chemical , electrical , electronics , civil , common ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        toolbar = findViewById(R.id.res_toolbar) ;
        computer = findViewById(R.id.computer) ;
        mechanical = findViewById(R.id.mechanical) ;
        chemical = findViewById(R.id.chemical) ;
        electrical = findViewById(R.id.electrical) ;
        electronics = findViewById(R.id.electronics) ;
        civil = findViewById(R.id.civil) ;
        common = findViewById(R.id.common) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
        //findViewById(R.id.upload).setOnClickListener(v -> handleUpload());

        computer.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Computer") ;
            startActivity(intent);
        });

        mechanical.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Mechanical") ;
            startActivity(intent);
        });

        chemical.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Chemical") ;
            startActivity(intent);
        });

        electrical.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Electrical") ;
            startActivity(intent);
        });

        electronics.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Electronics") ;
            startActivity(intent);
        });

        civil.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Civil") ;
            startActivity(intent);
        });

        common.setOnClickListener(v -> {
            Intent intent = new Intent(Resources.this , Resource_Courses.class) ;
            intent.putExtra("Branch" , "Common") ;
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void handleUpload() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT) ;
        intent.setType("*/*") ;
        intent.addCategory(Intent.CATEGORY_OPENABLE) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        try{
            startActivityForResult(Intent.createChooser(intent , "Select a file") , 100);
        }
        catch(Exception e){
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData() ;
            String path = uri.getPath() ;
            File file = new File(path) ;

            //Toast.makeText(this, "File selected successfully path =\n" + path + "\nName = " + file.getName(), Toast.LENGTH_LONG).show();

            //uploadToStorage(uri)
            Cursor cursor = this.getContentResolver()
                    .query(uri , null , null , null , null , null) ;
            String name = "" ; int s = 0;String size = null ;
            try{
                if(cursor != null && cursor.moveToFirst()){
                     name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ;
                     s = cursor.getColumnIndex(OpenableColumns.SIZE) ;
                    if(!cursor.isNull(s)){
                        size = cursor.getString(s) ;
                    }
                    else{
                        size = "Unknown" ;
                    }
                }
            }catch(Exception e){
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
            finally {
                cursor.close();
                AlertUtil.showAlertDialog(this , "Result" , "Path = " + file.getAbsolutePath() +"\n\nName = " + name + "\n\nSize = " + size + "\n\nType = " + name.substring(name.lastIndexOf('.') + 1), true , R.drawable.information , "Ok");
                uploadToStorage(uri , name , size) ;
            }

        }
        else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadToStorage(Uri uri , String type , String size){
       Intent intent = new Intent(this , UploadService.class) ;
       intent.putExtra("Type" , type) ;
       intent.putExtra("Size" , size) ;
       intent.putExtra("Uri" , uri) ;
       ContextCompat.startForegroundService(this , intent);
    }
}