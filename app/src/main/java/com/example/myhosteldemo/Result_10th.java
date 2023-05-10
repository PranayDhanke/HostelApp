package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Marks.Tenth_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Result_10th extends AppCompatActivity {

    Chip marksheet , allotment , aadhar ;

    ImageView profile , kebab ;
    TextView username , filled ;
    TextView phone , enroll , branch , year , gender , birth ;
    TextView marks , percent , rank ;
    Spinner  approval ;
    Toolbar toolbar ;
    TextView save ;
    EditText reject ;

    Intent prevIntent ;
    MeritListModel listModel ;
    MeritMarksModel meritMarksModel ;
    User user ;
    Tenth_Marks tenth_marks ;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy  hh:mm aa") ;

    FirebaseFirestore firestore ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result10th);

        toolbar = findViewById(R.id.result_10th_toolbar) ;
        profile = findViewById(R.id.result_10th_profile) ;
        kebab = findViewById(R.id.result_10thkebab) ;
        username = findViewById(R.id.result_10th_username) ;
        filled = findViewById(R.id.result_10th_formfilled) ;
        marksheet = findViewById(R.id.result10th_marksheet) ;
        allotment  = findViewById(R.id.result10th_allotment) ;
        aadhar  = findViewById(R.id.result10th_aadhar) ;
        phone = findViewById(R.id.result10th_phone) ;
        enroll = findViewById(R.id.result10th_enroll) ;
        branch = findViewById(R.id.result10th_branch) ;
        year = findViewById(R.id.result10th_year) ;
        gender = findViewById(R.id.result10th_gender) ;
        birth = findViewById(R.id.result10th_birth) ;
        marks = findViewById(R.id.result10th_marks) ;
        percent = findViewById(R.id.result10th_percent) ;
        approval = findViewById(R.id.result10th_approval) ;
        rank = findViewById(R.id.result10th_rank) ;
        save = findViewById(R.id.result_10th_save) ;
        reject = findViewById(R.id.result10th_rejection) ;

        prevIntent = getIntent() ;
        listModel = (MeritListModel) prevIntent.getSerializableExtra("MeritListModel");
        meritMarksModel = (MeritMarksModel) prevIntent.getSerializableExtra("MeritMarksModel");
        user = (User) prevIntent.getSerializableExtra("User");
        tenth_marks = (Tenth_Marks) prevIntent.getSerializableExtra("10th");

        firestore = FirebaseFirestore.getInstance() ;

        fillAllInfo() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void fillAllInfo(){
        username.setText(user.getUsername());
        filled.setText(sdf.format(tenth_marks.getTime()));
        phone.setText(user.getPhone());
        enroll.setText(user.getEnroll());
        branch.setText(user.getBranch());
        year.setText(user.getYear());
        gender.setText(user.getGender());
        birth.setText(user.getDob());
        marks.setText(tenth_marks.getMarks());
        percent.setText(tenth_marks.getPercent() + " %");
        rank.setText("N/A");

        String[] str = {"Unapproved" , "Approved" , "Rejected"} ;
        ArrayAdapter adapter = new ArrayAdapter(this , R.layout.drop_down_item , str) ;
        approval.setAdapter(adapter) ;

        int pos = 0 ;

        if(meritMarksModel.getApproval().equals("Unapproved")){
            pos = 0 ;
        }
        else if(meritMarksModel.getApproval().equals("Approved")){
            pos = 1 ;
        }
        else{
            pos = 2 ;
        }

        approval.setSelection(pos);

        if(GlobalData.isAdmin){
            approval.setEnabled(true);
            marksheet.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , tenth_marks.getMarksheet()) ;
                startActivity(intent);
            });

            allotment.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , tenth_marks.getAllotment()) ;
                startActivity(intent);
            });

            aadhar.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , tenth_marks.getAadhar()) ;
                startActivity(intent);
            });

            kebab.setOnClickListener(v -> {
                showPopup(kebab) ;
            });

            profile.setOnClickListener(v -> {
                Intent intent = new Intent(this , ShowImage.class) ;
                intent.putExtra("url" , user.getProfile()) ;
                startActivity(intent);
            });

            save.setVisibility(View.VISIBLE);
        }
        else{
            approval.setEnabled(false);
            save.setVisibility(View.GONE);
        }

        loadProfileImage() ;

        approval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(approval.getSelectedItem().toString().equals("Rejected")){
                    reject.setVisibility(View.VISIBLE);
                }
                else{
                    reject.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save.setOnClickListener(v -> {
            saveUpdatedData() ;
        });
    }

    private void loadProfileImage(){
        Glide.with(this)
                .load(user.getProfile())
                .into(profile) ;
    }

    private void saveUpdatedData(){
        HashMap<String , Object> hash = new HashMap<>() ;
        hash.put("approval" , approval.getSelectedItem().toString()) ;
        hash.put("rejection" , reject.getText().toString()) ;

        ProgressDialog dialog = new ProgressDialog(this) ;
        dialog.setTitle("Please wait");
        dialog.setMessage("Saving changes...");
        dialog.setIcon(R.drawable.information);
        dialog.setCancelable(false);
        dialog.show() ;

        firestore.collection("MeritListData")
                .document(listModel.getTitle())
                .collection(user.getYear())
                .document(user.getGender())
                .collection(user.getBranch())
                .document(meritMarksModel.getKey().substring(meritMarksModel.getKey().lastIndexOf('/') + 1))
                .set(hash , SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AlertUtil.showAlertDialog(Result_10th.this,
                                "Success",
                                "Changes saved successfully",
                                true,
                                R.drawable.information,
                                "Ok");
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertUtil.showAlertDialog(Result_10th.this,
                                "Failed",
                                "Changes failed to save",
                                true,
                                R.drawable.error,
                                "Ok");
                        dialog.dismiss();
                    }
                }) ;

    }

    private void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this , v) ;
        popupMenu.inflate(R.menu.result_options_menu);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.result_options_copy:
                    copyKey() ;
                    break;
                case R.id.result_options_delete:
                    deleteForm() ;
                default:
                    return false ;
            }
            return false;
        });

        popupMenu.show();
    }

    private void copyKey(){
        try{
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE) ;
            ClipData clipData = ClipData.newPlainText("Student Result List" , meritMarksModel.getKey()) ;
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(Result_10th.this, "Student Result List copied Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "Failed to copy Student Result List", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteForm(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this) ;
        alertDialog.setTitle("Select action") ;
        alertDialog.setMessage("Dou you really want to delete this form?") ;
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialog dialog = new ProgressDialog(Result_10th.this) ;
                dialog.setTitle("Please wait");
                dialog.setMessage("Deleting form...");
                dialog.setIcon(R.drawable.information);
                dialog.setCancelable(false);
                dialog.show() ;

                firestore.collection("MeritListData")
                        .document(listModel.getTitle())
                        .collection(user.getYear())
                        .document(user.getGender())
                        .collection(user.getBranch())
                        .document(meritMarksModel.getKey().substring(meritMarksModel.getKey().lastIndexOf('/') + 1))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                GlobalData.formDelete = prevIntent.getIntExtra("Position" , -1) ;
                                finish();
                                Snackbar.make(Result_10th.this ,null , "Succesfully deleted form" , 1000).show(); ;
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                AlertUtil.showAlertDialog(Result_10th.this,
                                        "Failed",
                                        "failed to delete form",
                                        true,
                                        R.drawable.error,
                                        "Ok");
                                dialog.dismiss();
                            }
                        }) ;
            }
        }) ;
       alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               Toast.makeText(Result_10th.this, "Canceled", Toast.LENGTH_SHORT).show();
               dialogInterface.dismiss();
           }
       }) ;

       alertDialog.show() ;
    }
}