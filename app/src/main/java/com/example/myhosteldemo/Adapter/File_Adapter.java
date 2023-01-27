package com.example.myhosteldemo.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.File_model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class File_Adapter extends RecyclerView.Adapter<File_Adapter.FileViewHolder> {

    Context context ;
    ArrayList<File_model> files ;

    SimpleDateFormat sdf ;

    public File_Adapter(Context context, ArrayList<File_model> files) {
        this.context = context;
        this.files = files;
        sdf = new SimpleDateFormat("dd-MM-yyyy  hh:mm:ss") ;
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
        holder.image.setImageResource(getImage(fm.getType()));
        holder.topic.setText(fm.getTopic());

        String desc = fm.getDesc() ;
        holder.desc.setText(desc.length() > 30 ? desc.substring(0 , 30) + "..." : desc);
        holder.desc2.setText(desc);
        holder.upload.setText(fm.getUser_name());
        String size = Double.toString(getMB(fm.getSize())) ;
        holder.size.setText( size.substring(0 , size.indexOf('.') + 3)+ " MB");
        holder.date.setText(sdf.format(new Date(fm.giveMeTime())));

        holder.download.setOnClickListener(v -> download(fm));

        holder.view.setOnClickListener(v -> view(fm));

        holder.more.setOnClickListener(v -> {
            holder.ll_more.setVisibility(View.GONE);
            holder.ll_less.setVisibility(View.VISIBLE);
        });

        holder.less.setOnClickListener(v -> {
            holder.ll_less.setVisibility(View.GONE);
            holder.ll_more.setVisibility(View.VISIBLE);
        });

        holder.desc2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.desc2.getViewTreeObserver().removeOnPreDrawListener(this);
                int lines = holder.desc2.getLineCount() ;
                if(lines == 1){
                    holder.more.setVisibility(View.GONE);
                    holder.ll_less.setVisibility(View.GONE);
                }
                else if(lines > 1){
                    holder.ll_less.setVisibility(View.GONE);
                    holder.ll_more.setVisibility(View.VISIBLE);
                }
                else {

                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.size() ;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder{

        ImageView image ;
        TextView topic , desc ,desc2 , more ,less , upload , size , date ;
        LinearLayout ll_more , ll_less ;
        TextView download , view ;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.file_lay_image) ;
            topic = itemView.findViewById(R.id.file_lay_topic) ;
            desc = itemView.findViewById(R.id.file_lay_desc) ;
            desc2 = itemView.findViewById(R.id.file_lay_desc2) ;
            more = itemView.findViewById(R.id.file_lay_more) ;
            less = itemView.findViewById(R.id.file_lay_less) ;
            upload = itemView.findViewById(R.id.file_lay_uploadby) ;
            size = itemView.findViewById(R.id.file_lay_size) ;
            date = itemView.findViewById(R.id.file_lay_date) ;
            ll_more = itemView.findViewById(R.id.file_lay_ll1) ;
            ll_less = itemView.findViewById(R.id.file_lay_ll2) ;
            download = itemView.findViewById(R.id.file_lay_download) ;
            view = itemView.findViewById(R.id.file_lay_view) ;
        }
    }

    private double getMB(int size){
        return (double)(((double) size /1000)/1000) ;
    }

    private int getImage(String type){
        switch (type){
            case "pdf":
                return R.drawable.pdf_svg ;
            case "doc" :
                return R.drawable.docx_svg ;
            case "docx" :
                return R.drawable.docx_svg ;
            case "ppt" :
                return R.drawable.ppt_svg ;
            case "pptx" :
                return R.drawable.ppt_svg ;
            case "zip" :
                return R.drawable.zip_svg ;
            case "xlsx" :
                return R.drawable.excel_svg ;
            case "csv" :
                return R.drawable.excel_svg ;
            case "jpg" :
            case "png" :
            case "jpeg" :
                return  R.drawable.image_svg ;
            default:
                return R.drawable.file_svg ;
        }
    }

    private void download(File_model fm){
        try {
            Toast.makeText(context, "Download Started...", Toast.LENGTH_SHORT).show();
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(fm.getUrl());
            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    fm.getTopic() + "." + fm.getType());

            dm.enqueue(request);
        }catch (Exception e){
            Toast.makeText(context, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private  void view(File_model fm){
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fm.getTopic() + "." + fm.getType() ;
        File file = new File(dir) ;
        Toast.makeText(context, "Dir = " + dir , Toast.LENGTH_SHORT).show();
        if(file.exists()){
            Uri path = FileProvider.getUriForFile(context ,
                    context.getApplicationContext().getPackageName() + ".provider" ,
                    file) ;
            Intent intent = new Intent(Intent.ACTION_VIEW) ;
            intent.setDataAndType(path , "*/*") ;
            intent.putExtra(Intent.EXTRA_STREAM , Uri.parse("content://" + file)) ;
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) ;
            context.startActivity(Intent.createChooser(intent , "Open File"));
        }
        else{
            Toast.makeText(context, "Download the file first", Toast.LENGTH_SHORT).show();
        }
    }

}
