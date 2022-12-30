package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;
import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    //for email and password
    String email , password , name ;
    boolean isValidEmail , isStrongPass ;
    Button create ;
    EditText edt_email , edt_pass , edt_name ;

    //activity related stuff
    TextView ahaa ;

    //firebase
    FirebaseAuth mAuth ;
    FirebaseDatabase database ;

    //progress dialog
    ProgressDialog dialog ;

    //SharedPreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        changeColorOfStatusBar(this , R.color.black) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        database = FirebaseDatabase.getInstance() ;

        //email sign in starts here
        edt_email = findViewById(R.id.email_create) ;
        edt_pass = findViewById(R.id.password_create) ;
        edt_name = findViewById(R.id.username) ;
        create = findViewById(R.id.create) ;
        isValidEmail = false ; isStrongPass = false ;

        //Progress Diagog
        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating your account....");
        dialog.setCancelable(false);

        create.setOnClickListener(v ->{
            dialog.show();

            email = edt_email.getText().toString() ;
            password = edt_pass.getText().toString() ;
            name = edt_name.getText().toString() ;

            if(checkAndNotifyIfEmpty(email , password , name) == false){
                dialog.cancel();
                return ;
            }

            if(checkAndNotifyIfNotMatch(email , password) == false){
                dialog.cancel();
                return;
            }

            handelCreateAccount(email , password , name);

            //dialog.cancel();
        });


        //activity related suff starts here
        findViewById(R.id.ahaa).setOnClickListener(v ->{
            finish();
        });

    }
    
    private boolean checkAndNotifyIfEmpty(String email , String password , String name){
        boolean move = true ;
        boolean soft_input = true ;
        if(email.isEmpty()){
            edt_email.setError("Email is empty" , null);
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ; soft_input = false ;
            move = false ;
        }
        if(name.isEmpty()){
            edt_name.setError("Username is empty" , null);
            Toast.makeText(this, "Username is empty", Toast.LENGTH_SHORT).show();
            if(soft_input){
                edt_name.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT)  ;
                soft_input = false ;
            }
                
            move = false ;
        }
        if(password.isEmpty()){
            edt_pass.setError("Password is empty" , null);
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            if(soft_input)
                edt_pass.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT)  ;
            move = false ;
        }
        return move ;
    }

    private boolean checkAndNotifyIfNotMatch(String email , String password){
        boolean move = true ;
        boolean soft_input = true ;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Email is invalid" , null);
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT)  ; soft_input = false ;
            move = false ;
        }
        if(password.length() < 6){
            edt_pass.setError("Password should be minimum of 6 digits" , null);
            Toast.makeText(this, "Password should be minimum of 6 digits", Toast.LENGTH_SHORT).show();
            if(soft_input)
                edt_pass.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ; ;
            move = false ;
        }

        return move ;
    }
    
    private void handelCreateAccount(String email , String password , String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            editor.putString("username" , name) ;
                            editor.apply();
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            fuser.sendEmailVerification() ;
                            User user = new User(name , email , password) ;
                            database.getReference().child("Users").child(fuser.getUid()).setValue(user) ;
                            showAlertDialog(CreateAccount.this,
                                    "Success",
                                    "Your account has been created successfully....\n\nPlease check your email inbox/spam to verify your emaiil",
                                    true,
                                    R.drawable.success,
                                    "Ok");

                            dialog.cancel() ;
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            showAlertDialog(CreateAccount.this,
                                    "Error In Creating Acoount",
                                    "1)Check your internet connection and try again\n\nError = "+task.getException(),
                                    true,
                                    R.drawable.error,
                                    "Ok");

                            dialog.cancel();
                        }
                    }
                });
    }
    
}