package com.example.myhosteldemo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
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
    SwipeListener swipeListener ;
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
        Complaint_MyCom_Viewpager_Model mycom = images.get(position) ;
        Glide.with(context)
                .load(mycom.getUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.layout.hideShimmer();
                        holder.nodata.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.layout.hideShimmer();
                        return false;
                    }
                })
                .into(holder.imageView) ;

        swipeListener = new SwipeListener(holder.imageView) ;

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

    private class SwipeListener extends Activity implements View.OnTouchListener{

        GestureDetector gestureDetector ;

        SwipeListener(View view){
            view.getParent().requestDisallowInterceptTouchEvent(true);
            int threshold = 100 ;
            int velocity_threshold = 100 ;
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    //Toast.makeText(context, "On down", Toast.LENGTH_SHORT).show();
                    return super.onDown(e);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY() ;
                    Toast.makeText(context, "onFling", Toast.LENGTH_SHORT).show();
                    try{
                        if(Math.abs(xDiff) > Math.abs(yDiff)){
                            if(Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold){
                                if(xDiff > 0){
                                    //swiped right
                                    Toast.makeText(context, "swiped right", Toast.LENGTH_SHORT).show();
                                    view.getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                else{
                                    //swiped left
                                    Toast.makeText(context, "swiped left", Toast.LENGTH_SHORT).show();
                                    view.getParent().requestDisallowInterceptTouchEvent(true);
                                }

                                return true ;
                            }
                        }
                        else{
                            if(Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocity_threshold){
                                if(yDiff > 0){
                                    //swiped down
                                    Toast.makeText(context, "swiped down", Toast.LENGTH_SHORT).show();
                                    view.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                else{
                                    //swiped up
                                    Toast.makeText(context, "swiped up", Toast.LENGTH_SHORT).show();
                                    view.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                return true ;
                            }
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(context, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            } ;

            gestureDetector = new GestureDetector(context , listener) ;

            view.setOnTouchListener((v , m) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                //Toast.makeText(context, "touch on image", Toast.LENGTH_SHORT).show();
                return gestureDetector.onTouchEvent(m) ;
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            return false;
        }
    }
}
