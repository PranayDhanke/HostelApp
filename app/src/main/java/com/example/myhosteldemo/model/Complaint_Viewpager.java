package com.example.myhosteldemo.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Complaint_Viewpager {
    Uri uri ;

    public Complaint_Viewpager(Uri uri) {
        this.uri = uri;
    }

    public Complaint_Viewpager() {}

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
