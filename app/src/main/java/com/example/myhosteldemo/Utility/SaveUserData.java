package com.example.myhosteldemo.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.SetupAccount;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SaveUserData {
    Context context ;
    FirebaseDatabase database ;
    User user ;

    public SaveUserData(Context context , User user){
        this.context = context ;
        this.user = user ;
        database = FirebaseDatabase.getInstance() ;
    }

    public boolean saveUser(){
        final boolean[] result = new boolean[1];
        result[0] = true ;

        ProgressDialog dialog = new ProgressDialog(context) ;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage("Saving Profile Changes....");
        dialog.setIcon(R.drawable.information);
        dialog.show();

        try{
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                result[0] = true ;
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(context, "Failed to store data\n" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }) ;
        }catch (Exception e){
            dialog.dismiss();
            //Toast.makeText(context , "Cann't save data\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return result[0] ;
    }
}
