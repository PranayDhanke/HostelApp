package com.example.myhosteldemo.Utility;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.SetupAccount;
import com.example.myhosteldemo.model.Complaint_Model;
import com.example.myhosteldemo.model.File_model;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class GlobalData {
    public static User user ;
    public static Drawable profile ;
    public static Uri profile_uri ;
    public static FirebaseUser fuser ;
    public static File_model fm ;
    public static boolean complaintAdded = false ;
    public static String filter_completeness = "" ;
    public static ArrayList<Complaint_Model> complaints = new ArrayList<>() ;

    public static void changeColorOfStatusBar(Activity activity, int color){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(color));
    }

}
