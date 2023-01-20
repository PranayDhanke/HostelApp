package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.Course_Adapter;
import com.example.myhosteldemo.Adapter.File_Adapter;
import com.example.myhosteldemo.Services.UploadService;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Course;
import com.example.myhosteldemo.model.File_model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class Course_files extends AppCompatActivity {

    Toolbar toolbar ;

    DatabaseReference database ;
    FirebaseStorage storage ;
    StorageReference ref ;

    ShimmerRecyclerView recyclerView ;
    TextView nodata ;
    FloatingActionButton add ;

    ArrayList<File_model> files ;
    Intent intent ;
    String branch , course ;
    ArrayList<String> types ;
    ArrayAdapter types_adapter ;

    EditText file_topic , file_desc ;
    Spinner file_type ;
    Button file_select , file_upload ;
    Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_files);

        toolbar = findViewById(R.id.files_toolbar) ;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        intent = getIntent() ;
        branch = intent.getStringExtra("Branch") ;
        course = intent.getStringExtra("Name") ;
        toolbar.setTitle(course);

        database = FirebaseDatabase.getInstance().getReference() ;
        storage = FirebaseStorage.getInstance() ;
        ref = storage.getReference() ;

        recyclerView = findViewById(R.id.files_recycle) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nodata = findViewById(R.id.files_nodata) ;
        add = findViewById(R.id.add_files) ;
        files = new ArrayList<>() ;

        types = new ArrayList<>() ;
        types.add("Any") ; types.add("pdf") ; types.add("doc") ; types.add("image") ; types.add("ppt") ; types.add("csv") ; types.add("excel") ; types.add("zip") ;
        types_adapter = new ArrayAdapter(this, R.layout.drop_down_item,types) ;
        types_adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        add.setOnClickListener(v -> addFile());

        preLoadFiles() ;

        dialog = new Dialog(this) ;
        dialog.setContentView(R.layout.file_custom_dialog);
        file_topic = dialog.findViewById(R.id.file_topic) ;
        file_desc = dialog.findViewById(R.id.file_desc) ;
        file_type = dialog.findViewById(R.id.file_type) ;
        file_select = dialog.findViewById(R.id.file_select) ;
        file_upload = dialog.findViewById(R.id.file_upload) ;

        file_type.setAdapter(types_adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }


    private  void addFile(){
        String topic , desc , type , user , email , name ;
        int size ;
        long time ;

        topic = file_topic.getText().toString() ;
        desc = file_desc.getText().toString() ;
        type = file_type.getSelectedItem().toString() ;

        file_select.setOnClickListener(v -> {handleUpload(file_type.getSelectedItem().toString()) ;});

        dialog.show();
    }

    private void preLoadFiles(){
        database.child("Resources/"+branch+"/Files/"+course+"/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    files.clear();
                    nodata.setVisibility(View.GONE);
                    int size = 0 ;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        File_model file = dataSnapshot.getValue(File_model.class) ;
                        files.add(file) ;
                        size++ ;
                    }
                    File_Adapter adapter = new File_Adapter(Course_files.this , files) ;
                    recyclerView.setAdapter(adapter);
                    recyclerView.hideShimmerAdapter();
                    Toast.makeText(Course_files.this, "Size = " + size, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                else{
                    recyclerView.hideShimmerAdapter();
                    nodata.setVisibility(View.VISIBLE);
                    //Toast.makeText(Resource_Courses.this, "No data avalable", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recyclerView.hideShimmerAdapter();
                nodata.setText("Failed to load data");
                nodata.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleUpload(String type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT) ;

        Toast.makeText(this, "Type = " + type , Toast.LENGTH_SHORT).show();

        if(type.equals("image")){
            intent.setType("image/*") ;
        }
        else if(type.equals("Any")){
            intent.setType("*/*") ;
        }
        else if(type.equals("doc")){
            String mimes[] = {"application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"} ;
            intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ;
            intent.putExtra(Intent.EXTRA_MIME_TYPES , mimes) ;
        }
        else if(type.equals("ppt")){
            String mimes[] = {"application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"} ;
            intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation") ;
            intent.putExtra(Intent.EXTRA_MIME_TYPES , mimes) ;
        }
        else if(type.equals("excel")){
            String mimes[] = {"application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"} ;
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ;
            intent.putExtra(Intent.EXTRA_MIME_TYPES , mimes) ;
        }
        else{
            intent.setType("application/"+type) ;
        }

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
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            Toast.makeText(this, "Failed to perform action", Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 100){
           if(data != null){
               file_upload.setEnabled(true);
               file_upload.setBackground(getDrawable(R.drawable.signin_btn_back));
               file_upload.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
               Toast.makeText(this, "File selected successfully", Toast.LENGTH_SHORT).show();

               file_upload.setOnClickListener( v -> {
                   //file upload starts here
                   Uri uri = data.getData() ;
                   String path = uri.getPath() ;

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

                       File_model fm = new File_model(file_topic.getText().toString(),
                               file_desc.getText().toString(),
                               FirebaseAuth.getInstance().getCurrentUser().getUid(),
                               /*GlobalData.user.getEmail() */"tejaspokale9204@gmail.com" ,
                               /*GlobalData.user.getUsername() */ "Tejas Pokale",
                               name.substring(name.lastIndexOf('.') + 1),
                               Integer.parseInt(size),
                               ServerValue.TIMESTAMP,
                               ""
                               ) ;

                       //AlertUtil.showAlertDialog(this , "Result" , "Name = " + name + "\n\nSize = " + size + "\n\nType = " + name.substring(name.lastIndexOf('.') + 1) + "\n\nClick upload to upload the file", true , R.drawable.information , "Ok");

                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(Course_files.this) ;
                       alertDialog.setTitle("Upload File") ;
                       alertDialog.setMessage("Name = " + name + "\n\nSize = " + size + "\n\nType = " + name.substring(name.lastIndexOf('.') + 1) + "\n\nClick ok to upload the file") ;
                       alertDialog.setCancelable(true) ;
                       alertDialog.setIcon(R.drawable.information) ;
                       alertDialog.setPositiveButton("Ok" , new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               uploadToStorage(uri , fm , branch , course) ;
                               dialogInterface.dismiss();
                               dialog.dismiss();
                           }
                       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                           }
                       }) ;
                       alertDialog.show() ;

                   }
               }) ;

               //file upload ends here

           }
           else{
               Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
               file_upload.setEnabled(false);
               file_upload.setBackground(getDrawable(R.drawable.signin_btn_back));
               file_upload.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightgrey)));
           }
        }

    }

    private void uploadToStorage(Uri uri , File_model fm , String branch , String course){
        Intent intent = new Intent(this , UploadService.class) ;
        intent.putExtra("Type" , fm.getType()) ;
        intent.putExtra("Size" , fm.getSize()) ;
        intent.putExtra("Branch" , branch) ;
        intent.putExtra("Course" , course) ;
        intent.putExtra("Uri" , uri) ;
        GlobalData.fm = fm ;

        ContextCompat.startForegroundService(this , intent);
    }
}