package com.example.myhosteldemo.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.Complaint_MyCom_Viewpager_Model;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class Complaint_MyCom_Viewpager_Adapter extends RecyclerView.Adapter<Complaint_MyCom_Viewpager_Adapter.ViewHolder> {
    Context context ;
    ArrayList<Complaint_MyCom_Viewpager_Model> images ;

    public Complaint_MyCom_Viewpager_Adapter(Context context, ArrayList<Complaint_MyCom_Viewpager_Model> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mycomplaint_image_viewpager , parent , false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint_MyCom_Viewpager_Model mycom = images.get(position);
//        Glide.with(context)
//                .load(mycom.getUrl())
//                .addListener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        holder.layout.hideShimmer();
//                        holder.nodata.setVisibility(View.VISIBLE);
//                        holder.nodata.setOnClickListener(v -> loadImage(context , holder , mycom.getUrl()));
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.layout.hideShimmer();
//                        return false;
//                    }
//                })
//                .into(holder.imageView);
        loadImage(context , holder , mycom.getUrl());

        holder.imageView.setOnClickListener(v -> {
            if(holder.imageView.getScaleType().equals(ImageView.ScaleType.FIT_CENTER)){
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else{
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView ;
        ShimmerFrameLayout layout ;
        TextView nodata ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mycomlay_view_image) ;
            layout = itemView.findViewById(R.id.mycomlay_view_shimmer) ;
            nodata = itemView.findViewById(R.id.mycomlay_nodata) ;
        }
    }

    private void loadImage(Context context , ViewHolder holder , String url){
        holder.nodata.setVisibility(View.GONE);
        holder.layout.startShimmer();

        Glide.with(context)
                .load(url)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.nodata.setVisibility(View.VISIBLE);
                        holder.layout.hideShimmer();
                        holder.nodata.setOnClickListener(v -> loadImage(context , holder , url));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.layout.hideShimmer();
                        return false;
                    }
                })
                .into(holder.imageView) ;
    }

}
