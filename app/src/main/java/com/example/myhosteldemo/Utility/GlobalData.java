package com.example.myhosteldemo.Utility;


import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.example.myhosteldemo.model.User;

public class GlobalData {
    public static User user ;

    public static void changeColorOfStatusBar(Activity activity, int color){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(color));
    }
}
