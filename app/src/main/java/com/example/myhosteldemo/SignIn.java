package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.AlertUtil.showAlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PatternMatcher;
import android.provider.ContactsContract;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookContentProvider;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;
import kotlin.text.RegexOption;

import com.example.myhosteldemo.Utility.AlertUtil.* ;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    //for google
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    ProgressDialog gprogress ;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database ;
    private FirebaseUser fuser ;
    User user ;
    TextView google ;

    //for facebook
    TextView facebook ;
    CallbackManager callbackManager ;
    private static final String EMAIL = "email";

    //for email and password
    String email , password ;
    boolean isValidEmail , isStrongPass ;
    Button siep ;
    EditText edt_email , edt_pass ;
    ProgressDialog eprogress ;

    //activity related variables
    TextView dhaa ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //google sign in starts here

        google = findViewById(R.id.google) ;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance() ;

        //initilise gprocess bar
        gprogress = new ProgressDialog(SignIn.this) ;
        gprogress.setTitle("Please wait....");
        gprogress.setMessage("Loading your accounts");
        gprogress.setIcon(R.drawable.information);
        gprogress.setCancelable(false);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gprogress.show();

                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(SignIn.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                try {
                                    startIntentSenderForResult(
                                            result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                            null, 0, 0, 0);
                                    gprogress.cancel();
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e("error", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                                    gprogress.cancel();

                                    showAlertDialog(SignIn.this,
                                            "Cann't initialise google sign in",
                                            "Possible reasons :-\n1)Check your internet connection\n2)Service is currently not available\n3)"+e,
                                            true,
                                            R.drawable.error,
                                            "Ok");

                                }
                            }
                        })
                        .addOnFailureListener(SignIn.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No saved credentials found. Launch the One Tap sign-up flow, or
                                // do nothing and continue presenting the signed-out UI.
                                Log.d("error", e.getLocalizedMessage());
                                Toast.makeText(SignIn.this, "Google sign in failed\nIn begin sign in method", Toast.LENGTH_LONG).show();
//
                                gprogress.cancel();

                                showAlertDialog(SignIn.this,
                                        "Cann't initialise google sign in",
                                        "Possible reasons :-\n1)Check your internet connection\n2)Service is currently not available\n3)"+e,
                                        true,
                                        R.drawable.error,
                                        "Ok");
                            }
                        });

                //dialog.cancel();
            }
        });

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("1095865715950-05n165g75a5dg382mhu7l3m2m4mdkc4g.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        //google sign in ends here


        //for facebook starts here
        facebook = findViewById(R.id.facebook) ;
        callbackManager = CallbackManager.Factory.create() ;

        facebook.setOnClickListener( v ->{
            //LoginManager.getInstance().setAuthType("reauthenticate") ;
            LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList(EMAIL , "public_profile"));
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        Toast.makeText(SignIn.this, "Facebook sign in succeeded\n"+mAuth.getCurrentUser().getDisplayName()+"\n"+mAuth.getCurrentUser().getPhotoUrl(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignIn.this, "Sign in with Facebook has been cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlertDialog(SignIn.this,
                                "Some error occured",
                                "Please try again\nOr try to sign in using different method\nError="+exception,
                                true,
                                R.drawable.error,
                                "Ok"
                        );
                    }
                });
        //for facebook ends here

        //email sign in starts here
        edt_email = findViewById(R.id.email) ;
        edt_pass = findViewById(R.id.password) ;
        siep = findViewById(R.id.signin) ;
        isValidEmail = false ; isStrongPass = false ;

        siep.setOnClickListener(v ->{
            email = edt_email.getText().toString() ;
            password = edt_pass.getText().toString() ;


            eprogress = new ProgressDialog(SignIn.this) ;
            eprogress.setTitle("Please wait");
            eprogress.setMessage("Signing you in....");
            eprogress.setCancelable(false);
            eprogress.show();

            if(checkAndNotifyIfEmpty(email , password) == false){
                eprogress.cancel();
                return ;
            }

            if(checkAndNotifyIfNotMatch(email , password) == false){
                eprogress.cancel();
                return;
            }

            handleEmailsignIn(email , password) ;

            //dialog.cancel();
        });
        //email sign in ends here


        //Activity related work
        dhaa = findViewById(R.id.dhaa) ;
        dhaa.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this , CreateAccount.class)) ;
        });

        findViewById(R.id.forgot).setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this , ForgotPassword.class)) ;
        });


        //Activity related work ends here


        //on create ends here
    }


    //on result activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                handleGoogleSignIn(data) ;
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleGoogleSignIn(Intent data){
        ProgressDialog dialog = new ProgressDialog(SignIn.this) ;
        dialog.setTitle("Please wait");
        dialog.setMessage("We're geting you in");
        dialog.setCancelable(false);
        dialog.show();
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            if (idToken !=  null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.

                //starting
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("sucees", "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignIn.this, "Google sign in with firebase Succeeded", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                    //startActivity(new Intent(SignIn.this , SetupAccount.class));
                                    goForGF(user);
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("fail", "signInWithCredential:failure", task.getException());
                                    Toast.makeText(SignIn.this, "Google sign in failed\nIn on complete listener", Toast.LENGTH_LONG).show();
                                    //updateUI(null);
                                    dialog.cancel();

                                    showAlertDialog(SignIn.this,
                                            "Failed to sign in",
                                            "Possible reasons :-\n1)Check your internet connection\n2)Service is currently not available",
                                            true,
                                            R.drawable.error,
                                            "Ok");
                                }
                            }
                        });
                //ending

                Log.d("GotId", "Got ID token.");
            }
            else{
                dialog.cancel();

                showAlertDialog(SignIn.this,
                        "Failed to sign in",
                        "Possible reasons :-\n1)Check your internet connection\n2)Service is currently not available",
                        true,
                        R.drawable.error,
                        "Ok");
            }
        } catch (ApiException e) {
            // ...
            dialog.cancel();
            Toast.makeText(this, "Google sign in failed\nIn on activity result", Toast.LENGTH_LONG).show();

            showAlertDialog(SignIn.this,
                    "Failed to sign in",
                    "Possible reasons :-\n1)Check your internet connection\n2)Service is currently not available\n3)"+e,
                    true,
                    R.drawable.error,
                    "Ok");
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           // startActivity(new Intent(SignIn.this , SetupAccount.class));
                            goForGF(user);
                          //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                            showAlertDialog(SignIn.this,
                                    "Some error occured",
                                    "Please try again\nOr try to sign in using different method\n"+task.getException(),
                                    true,
                                    R.drawable.error,
                                    "Ok"
                            );
                        }
                    }
                });
    }

    private boolean checkAndNotifyIfEmpty(String email , String password){
        boolean move = true ;
        boolean soft_email = true ;
        if(email.isEmpty()){
            edt_email.setError("Email is empty" , null);
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ; soft_email = false ;
            move = false ;
        }
        if(password.isEmpty()){
            edt_pass.setError("Password is empty" , null);
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            if(soft_email)
                edt_pass.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT)  ;
            move = false ;
        }
        return move ;
    }

    private boolean checkAndNotifyIfNotMatch(String email , String password){
        boolean move = true ;
        boolean soft_email = true ;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Email is invalid" , null); ;
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
            edt_email.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT)  ; soft_email = false ;
            move = false ;
        }
        if(password.length() < 6){
            edt_pass.setError("Password should be minimum of 6 digits" ,null);
            Toast.makeText(this, "Password should be minimum of 6 digits", Toast.LENGTH_SHORT).show();
            if(soft_email)
                edt_pass.requestFocus(EditText.LAYOUT_DIRECTION_INHERIT) ; ;
            move = false ;
        }
        return move ;
    }

    private void handleEmailsignIn(String email , String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("email", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleUserSingedUsingEmail(user) ;
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("email", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showAlertDialog(SignIn.this,
                                    "Error In SignIn",
                                    "1)Please create a account if you don't have\n\n2)Check your internet connection and try again\n\nError = "+task.getException(),
                                    true,
                                    R.drawable.error,
                                    "Ok");

                            eprogress.cancel();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void handleUserSingedUsingEmail(FirebaseUser user){
        if(user.isEmailVerified()){
            Toast.makeText(SignIn.this, "Email & Password Sign in success", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Information : Name = " + user.getDisplayName() + "\nProfile = "+user.getPhotoUrl()+"\nProvider = " + user.getProviderData().get(user.getProviderData().size() - 1), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(SignIn.this , SetupAccount.class));
            //eprogress.dismiss();
            goForEmail(user);
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignIn.this) ;
            alertDialog.setTitle("Please verify your email") ;
            alertDialog.setMessage("Your email verification is pending\n\nVisit email -> inbox/spam to verify your email") ;
            alertDialog.setCancelable(true) ;
            alertDialog.setIcon(R.drawable.error) ;
            alertDialog.setPositiveButton("Ok" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }) ;
            alertDialog.setNegativeButton("Resend email", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    user.sendEmailVerification().addOnSuccessListener(v -> {
                        Toast.makeText(SignIn.this, "New verification mail has been sent on " + user.getEmail(), Toast.LENGTH_LONG).show();
                    }).addOnFailureListener(v -> {
                        Toast.makeText(SignIn.this, "Failed to sent verification mail", Toast.LENGTH_LONG).show();
                    }) ;

                    dialogInterface.dismiss();
                }
            }) ;
            alertDialog.show() ;
            eprogress.cancel();
        }

    }


    private void goForEmail(FirebaseUser fuser){
        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent ;
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            if(user.isSetuped()){
                                intent = new Intent(new Intent(SignIn.this , MainActivity.class)) ;
                                eprogress.cancel();
                                startActivity(intent);
                            }
                            else{
                                intent = new Intent(new Intent(SignIn.this , SetupAccount.class)) ;
                                intent.putExtra("emailpending" , true) ;
                                intent.putExtra("name" , user.getUsername()) ;
                                //intent.putExtra("profile" , fuser.getPhotoUrl().toString()) ;
                                intent.putExtra("email" , fuser.getEmail()) ;
                                intent.putExtra("password" , user.getPassword()) ;
                                eprogress.cancel();
                                startActivity(intent);
                            }
                            finish();
                        }
                        else{
                            intent = new Intent(new Intent(SignIn.this , SetupAccount.class)) ;
                            intent.putExtra("emailpending" , true) ;
                            intent.putExtra("name" , fuser.getDisplayName()) ;
                            //intent.putExtra("profile" , fuser.getPhotoUrl().toString()) ;
                            intent.putExtra("email" , fuser.getEmail()) ;
                            intent.putExtra("password" , "") ;
                            eprogress.cancel();
                            startActivity(intent);
                            finish() ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        eprogress.cancel();
                        showAlertDialog(SignIn.this
                                        , "Try Again"
                                        , "Failed to load your data\nTry signing again"
                                        , true
                                        , R.drawable.error
                                        , "Ok");
                    }
                });
    }


    private void goForGF(FirebaseUser fuser){
        ProgressDialog dialog = new ProgressDialog(SignIn.this) ;
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading your data....");
        dialog.setCancelable(false);
        dialog.show();

        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user ;
                        Intent intent ;
                        if(snapshot.exists()){
                            user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            if(user.isSetuped()){
                                dialog.dismiss();
                                intent = new Intent(new Intent(SignIn.this , MainActivity.class)) ;
                                startActivity(intent);
                            }
                            else{
                                intent = new Intent(new Intent(SignIn.this , SetupAccount.class)) ;
                                intent.putExtra("gfpending" , true) ;
                                intent.putExtra("name" , fuser.getDisplayName()) ;
                                intent.putExtra("profile" , fuser.getPhotoUrl().toString()) ;
                                intent.putExtra("email" , fuser.getEmail()) ;
                                dialog.dismiss();
                                startActivity(intent);
                                finish() ;
                            }
                            finish();
                        }
                        else{
                            intent = new Intent(new Intent(SignIn.this , SetupAccount.class)) ;
                            intent.putExtra("gfpending" , true) ;
                            intent.putExtra("name" , fuser.getDisplayName()) ;
                            intent.putExtra("profile" , fuser.getPhotoUrl().toString()) ;
                            intent.putExtra("email" , fuser.getEmail()) ;
                            dialog.dismiss();
                            startActivity(intent);
                            finish() ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        showAlertDialog(SignIn.this
                                , "Try Again"
                                , "Failed to load your data\nTry signing again"
                                , true
                                , R.drawable.error
                                , "Ok");
                    }
                });
    }


}