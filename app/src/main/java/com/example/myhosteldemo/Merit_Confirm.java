package com.example.myhosteldemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Merit_Confirm extends AppCompatActivity {

    Toolbar toolbar ;

    EditText username , phone , enroll ;
    TextView branch , yos , gender ;
    Button changes , next , dob ;

    NestedScrollView nestedScrollView ;
    ProgressBar progressBar ;
    TextView nodata ;

    FirebaseFirestore firestore ;
    FirebaseUser firebaseUser ;

    MeritListModel model ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_confirm);

        toolbar = findViewById(R.id.confirm_toolbar) ;
        username = findViewById(R.id.confirm_username) ;
        phone = findViewById(R.id.confirm_phone) ;
        enroll = findViewById(R.id.confirm_enroll) ;
        branch = findViewById(R.id.confirm_branch) ;
        yos = findViewById(R.id.confirm_year) ;
        gender = findViewById(R.id.confirm_gender) ;
        changes = findViewById(R.id.confirm_makechanges) ;
        next = findViewById(R.id.confirm_next) ;
        dob = findViewById(R.id.confirm_dob) ;
        nestedScrollView = findViewById(R.id.confirm_nested) ;
        progressBar = findViewById(R.id.confirm_progress) ;
        nodata = findViewById(R.id.confirm_nodata) ;

        changes.setOnClickListener(v -> {
            Intent intent =  new Intent(this , SetupAccount.class) ;
            intent.putExtra("MainActivity" , true) ;
            startActivity(intent);
        });

        next.setOnClickListener(v -> {
            handelNext() ;
        });

        getAllData() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance() ;

        model = (MeritListModel) (getIntent().getSerializableExtra("MeritListModel")) ;
        loadCurrentStatus() ;
    }

    @Override
    protected void onResume() {
        getAllData() ;
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    private void getAllData(){
        User user = GlobalData.user ;
        username.setText(user.getUsername());
        phone.setText(user.getPhone());
        enroll.setText(user.getEnroll());
        branch.setText(user.getBranch());
        dob.setText(user.getDob());
        gender.setText(user.getGender());
        yos.setText(user.getYear());
    }

    private void loadCurrentStatus(){
        nodata.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection("All Forms")
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                nodata.setText("You have already filled form");
                                nodata.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                nestedScrollView.setVisibility(View.GONE);
                                nodata.setOnClickListener(v -> {});
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                nodata.setVisibility(View.GONE);
                                nestedScrollView.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            nodata.setText("Failed to load\nTap to retry");
                            nodata.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.GONE);
                            nodata.setOnClickListener(v -> {
                                loadCurrentStatus();
                            });
                        }
                    }
                }) ;
    }

    private void handelNext(){
        User user = GlobalData.user ;
        if(user.getGender().equals("others")){
            Snackbar.make(findViewById(R.id.confirm_relative) , "Please select gender from male or female" ,Snackbar.LENGTH_LONG)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =  new Intent(Merit_Confirm.this , SetupAccount.class) ;
                            intent.putExtra("MainActivity" , true) ;
                            startActivity(intent);
                        }
                    })
                    .show();
            return;
        }

        Intent intent ;

        if(user.getYear().equals("1st year")){
            intent = new Intent(Merit_Confirm.this , Merit_1st_year.class) ;
            intent.putExtra("MeritListModel" , model) ;
            startActivity(intent);
        }
        else if(user.getYear().equals("2nd year")){
            intent = new Intent(Merit_Confirm.this , Merit_2nd_Year.class) ;
            intent.putExtra("MeritListModel" , model) ;
            startActivity(intent);
        }
        else{
            intent = new Intent(Merit_Confirm.this , Merit_3rd_Year.class) ;
            intent.putExtra("MeritListModel" , model) ;
            startActivity(intent);
        }
    }

}