package com.example.myhosteldemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.MainActivity;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.ShowImage;
import com.example.myhosteldemo.model.Complaint_MyCom_Viewpager_Model;
import com.example.myhosteldemo.model.Main_Viewpager_Model;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class Main_viewpager_adapter extends RecyclerView.Adapter<Main_viewpager_adapter.ViewHolder>{

    Context context ;
    ArrayList<Main_Viewpager_Model> images ;

    public Main_viewpager_adapter(Context context, ArrayList<Main_Viewpager_Model> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_viewpager , parent , false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Main_Viewpager_Model vm = images.get(position);

        loadImage(context , holder , vm.getUrl() , vm.getId());

        holder.imageView.setOnClickListener(v -> {
//            if(holder.imageView.getScaleType().equals(ImageView.ScaleType.FIT_CENTER)){
//                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            }
//            else{
//                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            }
            Intent intent = new Intent(context, ShowImage.class) ;
            if(vm.getUrl() != null){
                intent.putExtra("url" , vm.getUrl()) ;
            }
            else if(vm.getId() > 0){
                intent.putExtra("id" , vm.getId()) ;
            }

            if(vm.getId() != -1){
                context.startActivity(intent);
            }

        });

        holder.imageView.setOnLongClickListener(v -> {
            MainActivity.pageHandeler.removeCallbacks(MainActivity.runnable);
            Toast.makeText(context, "scrolling has been stopped", Toast.LENGTH_SHORT).show();
            return true ;
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
            imageView = itemView.findViewById(R.id.main_view_image) ;
            layout = itemView.findViewById(R.id.main_view_shimmer) ;
            nodata = itemView.findViewById(R.id.main_view_nodata) ;
        }
    }


    private void loadImage(Context context , Main_viewpager_adapter.ViewHolder holder , String url , int id){
        holder.nodata.setVisibility(View.GONE);
        holder.layout.startShimmer();

        if(id > 0){
            holder.imageView.setImageResource(id);
            holder.layout.hideShimmer();
            return ;
        }
        else if(id == -1){
            return ;
        }

        Glide.with(context)
                .load(url)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.nodata.setVisibility(View.VISIBLE);
                        holder.layout.hideShimmer();
                        holder.nodata.setOnClickListener(v -> loadImage(context , holder , url , id));
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
