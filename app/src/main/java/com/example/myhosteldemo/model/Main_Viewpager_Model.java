package com.example.myhosteldemo.model;

import android.media.Image;
import android.widget.ImageView;

public class Main_Viewpager_Model {
    ImageView imageview ;
    String url ;
    int id ;

    public Main_Viewpager_Model(ImageView imageview, String url, int id) {
        this.imageview = imageview;
        this.url = url;
        this.id = id;
    }

    public Main_Viewpager_Model() {
    }

    public ImageView getImageview() {
        return imageview;
    }

    public void setImageview(ImageView imageview) {
        this.imageview = imageview;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
