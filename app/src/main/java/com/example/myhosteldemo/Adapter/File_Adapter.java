package com.example.myhosteldemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.File_model;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class File_Adapter extends RecyclerView.Adapter<File_Adapter.FileViewHolder> {

    Context context ;
    ArrayList<File_model> files ;

    public File_Adapter(Context context, ArrayList<File_model> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.files_info , parent , false) ;
        return new FileViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File_model fm = files.get(position) ;
        holder.topic.setText(fm.getTopic());
        holder.desc.setText(fm.getDesc());
        holder.upload.setText(fm.getEmail());
        holder.size.setText(Integer.toString(fm.getSize()));
        holder.date.setText(String.valueOf(fm.giveMeTime()));
    }

    @Override
    public int getItemCount() {
        return files.size() ;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image ;
        TextView topic , desc , upload , size , date ;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.file_lay_image) ;
            topic = itemView.findViewById(R.id.file_lay_topic) ;
            desc = itemView.findViewById(R.id.file_lay_desc) ;
            upload = itemView.findViewById(R.id.file_lay_uploadby) ;
            size = itemView.findViewById(R.id.file_lay_size) ;
            date = itemView.findViewById(R.id.file_lay_date) ;
        }
    }
}
