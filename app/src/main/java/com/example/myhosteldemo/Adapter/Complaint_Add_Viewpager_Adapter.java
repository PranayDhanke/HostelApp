package com.example.myhosteldemo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.Add_Complaint;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.Complaint_Viewpager;
import com.github.drjacky.imagepicker.ImagePicker;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

public class Complaint_Add_Viewpager_Adapter extends RecyclerView.Adapter<Complaint_Add_Viewpager_Adapter.ViewHolder> {
    ArrayList<Complaint_Viewpager> images ;
    Context context ;

    static final int GALLERY_REQUEST = 100 ;

    public Complaint_Add_Viewpager_Adapter(ArrayList<Complaint_Viewpager> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_add_image , parent , false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint_Viewpager cv = images.get(position) ;
        holder.image.setImageURI(cv.getUri()) ;
        holder.more.setOnClickListener(v -> getImage());
    }

    @Override
    public int getItemCount() {
        return images.size() ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image ;
        TextView more ;
        WormDotsIndicator indicator ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.com_add_view_image) ;
            more = itemView.findViewById(R.id.com_add_view_more) ;
            indicator = itemView.findViewById(R.id.com_add_view_dot) ;
        }


    }

    private  void getImage(){
        ImagePicker.Companion.with((Activity) context)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start(GALLERY_REQUEST) ;
    }

    private void updateViewPager(Uri uri){
        images.add(new Complaint_Viewpager(uri)) ;
        this.notifyDataSetChanged();
    }

}
