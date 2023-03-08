package com.example.myhosteldemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MessMenuModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Mess_Main extends AppCompatActivity {

    Toolbar toolbar ;

    TextView day , breakfast , lunch , dinner ;
    FloatingActionButton fab ;

    FirebaseFirestore firestore ;

    SimpleDateFormat sdf ;
    SimpleDateFormat dsdf ;
    static ProgressDialog dialog ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_main);

        day = findViewById(R.id.mess_day) ;
        breakfast = findViewById(R.id.mess_break_btext) ;
        lunch = findViewById(R.id.mess_lunch_ltext) ;
        dinner = findViewById(R.id.mess_dinner_dtext) ;
        toolbar = findViewById(R.id.mess_toolbar) ;
        fab = findViewById(R.id.mess_menu_fab) ;

        sdf = new SimpleDateFormat("dd-MMM-yyyy") ;
        dsdf = new SimpleDateFormat("EEEE") ;
        firestore = FirebaseFirestore.getInstance() ;

        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Loading...");
        dialog.setMessage("please wait...");
        dialog.setCancelable(true);
        dialog.show();

        day.setText(dsdf.format(System.currentTimeMillis()));

        getMess() ;

        fab.setOnClickListener( v -> handlePrevious());

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void getMess(){
       day.setText(dsdf.format(System.currentTimeMillis()));
        dialog.show();
       firestore.collection("Mess Menu")
               .document(sdf.format(System.currentTimeMillis()))
               .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                   @Override
                   public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                       dialog.dismiss();
                       if(error != null){
                           Toast.makeText(Mess_Main.this, "Failed to load Today's menu", Toast.LENGTH_SHORT).show();
                       }
                       else{
                          if(value.exists()){
                              setUpMessMenu(value.toObject(MessMenuModel.class)) ;
                          }
                          else{
                              Toast.makeText(Mess_Main.this, "Today's menu not updated", Toast.LENGTH_SHORT).show();
                          }
                       }
                   }
               }) ;
    }

    private void getMess(int i, int i1, int i2){
        Calendar cal2 = Calendar.getInstance() ;
        cal2.set(i , i1 , i2);

        String doc = sdf.format(cal2.getTimeInMillis());

        dialog.show();
        firestore.collection("Mess Menu")
                .document(doc)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        dialog.dismiss();
                        if(error != null){
                            Toast.makeText(Mess_Main.this, "Failed to load menu", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(value.exists()){
                                day.setText(dsdf.format(cal2.getTimeInMillis()));
                                setUpMessMenu(value.toObject(MessMenuModel.class)) ;
                            }
                            else{
                                Toast.makeText(Mess_Main.this, "Menu not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }) ;
    }

    private void setUpMessMenu(MessMenuModel model){
        if(model.getBreakfast() != null){
            breakfast.setText(model.getBreakfast());
            breakfast.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            breakfast.setText("Not Updated yet");
            breakfast.setTextColor(getResources().getColor(R.color.red));
        }

        if(model.getLunch() != null){
            lunch.setText(model.getLunch());
            lunch.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            lunch.setText("Not Updated yet");
            lunch.setTextColor(getResources().getColor(R.color.red));
        }

        if(model.getDinner() != null){
            dinner.setText(model.getDinner());
            dinner.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            dinner.setText("Not Updated yet");
            dinner.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void handlePrevious(){
        Calendar cal = Calendar.getInstance() ;
        cal.setTimeInMillis(System.currentTimeMillis());

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar cal2 = Calendar.getInstance() ;
                cal2.set(i , i1 , i2);
                Log.d("Selected date", sdf.format(cal2.getTimeInMillis())) ;
                getMess(i,i1,i2);
            }
        } , cal.get(Calendar.YEAR) , cal.get(Calendar.MONTH) , cal.get(Calendar.DAY_OF_MONTH)) ;

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.show();

    }


}