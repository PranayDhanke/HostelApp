package com.example.myhosteldemo.model;

import android.widget.ImageView;

public class Complaint_MyCom_Viewpager_Model {
    ImageView imageView ;
    String url ;

    public Complaint_MyCom_Viewpager_Model(ImageView imageView , String url) {
        this.imageView = imageView;
        this.url = url ;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
