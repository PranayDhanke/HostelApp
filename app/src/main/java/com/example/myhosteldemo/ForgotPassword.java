package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;
import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText edt_email ;

    ImageView back ;

    //progress dialog
    ProgressDialog dialog ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        changeColorOfStatusBar(this , R.color.black) ;

        edt_email = findViewById(R.id.email);
        back = findViewById(R.id.backarrow) ;

        //Progress Diagog
        dialog = new ProgressDialog(this) ;
        dialog.setTitle("Please wait");
        dialog.setMessage("Sending Reset Password....");
        dialog.setCancelable(false);

        findViewById(R.id.submit).setOnClickListener(v -> handleForgotPassword());

        back.setOnClickListener(v -> finish());

    }

    private void handleForgotPassword(){
        dialog.show();
        String email = edt_email.getText().toString() ;

        if(verifyEmail(email) == false){
            dialog.cancel();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "Email sent.");
                            showAlertDialog(ForgotPassword.this,
                                    "Success",
                                    "Reset password email has been sent on " + email + " please visit email -> inbox/spam to change your password!",
                                    true,
                                    R.drawable.information,
                                    "Ok") ;
                            dialog.cancel();
                        }
                        else{
                            showAlertDialog(ForgotPassword.this,
                                    "Failed",
                                    "Failed to sent Reset Password email\n\nError = "+task.getException(),
                                    true,
                                    R.drawable.error,
                                    "Ok") ;
                            dialog.cancel();
                        }
                    }
                });
    }

    private  boolean verifyEmail(String email){
        if(email.isEmpty()){
            edt_email.setError("Email is empty");
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ;
            return false ;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Email is invalid");
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ;
            return false ;
        }

        return true ;
    }


}