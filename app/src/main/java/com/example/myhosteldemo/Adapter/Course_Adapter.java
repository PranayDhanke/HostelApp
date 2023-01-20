package com.example.myhosteldemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.Course_files;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.Course;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Course_Adapter extends RecyclerView.Adapter<Course_Adapter.CourseViewHolder> {
    Context context ;
    ArrayList<Course> courses ;
    int imageId ;
    String branch ;

    public Course_Adapter(Context context , ArrayList<Course> courses , int imageId , String branch){
        this.context = context ;
        this.courses = courses ;
        this.imageId = imageId ;
        this.branch = branch ;
    }


    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_info , parent , false) ;
        return new CourseViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position) ;
        holder.image.setImageResource(imageId);
        holder.course_id.setText(course.getCourse_id());
        holder.course_name.setText(course.getCourse_name());
        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context , Course_files.class) ;
            intent.putExtra("Name" , course.getCourse_name()) ;
            intent.putExtra("Id" , course.getCourse_id()) ;
            intent.putExtra("Branch" , branch) ;
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return courses.size() ;
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image ;
        TextView course_id , course_name ;
        RelativeLayout layout ;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.info_image) ;
            course_id = itemView.findViewById(R.id.info_id) ;
            course_name = itemView.findViewById(R.id.info_name) ;
            layout = itemView.findViewById(R.id.course_info_layout) ;
        }
    }

}
