package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MeritListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Merit_AddList extends AppCompatActivity {

    Toolbar toolbar ;

    Button start , end , add ;
    EditText title , desc ;
    long st , et ;
    ProgressDialog dialog ;

    FirebaseFirestore firestore ;
    FirebaseUser fuser ;

    SimpleDateFormat sdf ;
    int syear , smonth , sday ,shour , sminute , eyear , emonth , eday , ehour , eminute ;
    long startLong , endLong ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_add_list);

        toolbar = findViewById(R.id.merit_add_toolbar) ;
        start = findViewById(R.id.merit_add_start) ;
        end = findViewById(R.id.merit_add_end) ;
        add = findViewById(R.id.merit_add_add) ;
        title = findViewById(R.id.merit_add_title) ;
        desc = findViewById(R.id.merit_add_desc) ;

        firestore = FirebaseFirestore.getInstance() ;
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        sdf = new SimpleDateFormat("dd-MMM-yyyy  hh:mm aa") ;

        Calendar cal = Calendar.getInstance() ;
        syear = eyear = cal.get(Calendar.YEAR) ;
        smonth = emonth = cal.get(Calendar.MONTH) ;
        sday = eday = cal.get(Calendar.DAY_OF_MONTH) ;
        shour = ehour = cal.get(Calendar.HOUR_OF_DAY) ;
        sminute = eminute = cal.get(Calendar.MINUTE) ;

        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Saving...");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        add.setOnClickListener(v -> {
            dialog.show();
            uploadMeritList() ;
        });

        start.setOnClickListener(v -> {
            getDate(start) ;
        });

        end.setOnClickListener(v -> {
            getDate(end) ;
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void getDate(Button button){
        Calendar cal = Calendar.getInstance() ;

        int year , month , day , hour , minute;

        if(button.getId() == start.getId()){
            year = syear ;
            month = smonth ;
            day = sday ;
            hour = shour ;
            minute = sminute ;
        }
        else{
            year = eyear ;
            month = emonth ;
            day = eday ;
            hour = ehour ;
            minute = eminute ;
        }

        TimePickerDialog timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Calendar temp = Calendar.getInstance() ;
                if(button.getId() == start.getId()){
                    shour = i ;
                    sminute = i1 ;
                    temp.set(syear , smonth , sday , shour , sminute);
                }
                else{
                    ehour = i;
                    eminute = i1 ;
                    temp.set(eyear , emonth , eday , ehour , eminute);
                }

                long time = temp.getTimeInMillis() ;

                if(button.getId() == start.getId()){
                    startLong = time ;
                }
                else{
                    endLong = time ;
                }

                button.setText(sdf.format(time));
            }
        } , hour , minute , false) ;


        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                if(button.getId() == start.getId()){
                    syear = i ;
                    smonth = i1 ;
                    sday = i2 ;
                }
                else{
                    eyear = i ;
                    emonth = i1 ;
                    eday = i2 ;
                }

                timeDialog.show();
            }
        }, year, month , day) ;

        timeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(Merit_AddList.this, "Please select time also", Toast.LENGTH_SHORT).show();
            }
        });

        dateDialog.show();
    }

    private boolean isValidate(){
        if(start.getText().toString().trim().equals("") || start.getText().toString().trim().equals("Select start date")){
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(end.getText().toString().trim().equals("") || end.getText().toString().trim().equals("Select end date")){
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(startLong >= endLong || start.getText().toString().equals(end.getText().toString())){
            Toast.makeText(this, "Start date can't be greater or equal to end date", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(title.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
            title.setError("Please enter title",null);
            title.requestFocus() ;
            return false ;
        }
        if(title.getText().toString().contains("/")){
            Toast.makeText(this, "title should not contain /", Toast.LENGTH_SHORT).show();
            title.setError("title should not contain /",null);
            title.requestFocus() ;
            return false ;
        }
        if(desc.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            desc.setError("Please enter description",null);
            desc.requestFocus() ;
            return false ;
        }

        return true ;
    }

    private void uploadMeritList(){
        if(isValidate()){
            MeritListModel model = new MeritListModel(fuser.getUid(),
                    start.getText().toString(),
                    end.getText().toString(),
                    startLong,
                    endLong,
                    title.getText().toString(),
                    desc.getText().toString(),
                    false,
                    System.currentTimeMillis()) ;
            chechIfTitleExist(model) ;
        }
        else{
            dialog.dismiss();
        }
    }

    private void chechIfTitleExist(MeritListModel model){
        firestore.collection("MeritList")
                .document(model.getTitle())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                dialog.dismiss();
                                AlertUtil.showAlertDialog(Merit_AddList.this,
                                        "Failed",
                                        "Merit List with title already exist",
                                        true,
                                        R.drawable.error,
                                        "Ok");
                            }
                            else{
                                upload(model) ;
                            }
                        }
                        else{
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(Merit_AddList.this,
                                    "Failed",
                                    "Unable to add Merit List",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private void upload(MeritListModel model){
        firestore.collection("MeritList")
                .document(model.getTitle())
                .set(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(Merit_AddList.this,
                                    "Success",
                                    "Merit List added successfully",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                            GlobalData.meritListAdded = true ;
                        }
                        else{
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(Merit_AddList.this,
                                    "Failed",
                                    "Unable to add Merit List",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

}