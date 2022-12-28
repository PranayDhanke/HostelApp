package com.example.myhosteldemo.Utility;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.myhosteldemo.SignIn;

public class AlertUtil {
    public static void showAlertDialog(Context context , String title , String message , boolean cancelable , int icon , String button){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context) ;
        alertDialog.setTitle(title) ;
        alertDialog.setMessage(message) ;
        alertDialog.setCancelable(cancelable) ;
        alertDialog.setIcon(icon) ;
        alertDialog.setPositiveButton(button , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }) ;
        alertDialog.show() ;
    }
}
