package com.example.myhosteldemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Result_10th;
import com.example.myhosteldemo.Result_2nd_year;
import com.example.myhosteldemo.model.Marks.SecondYear_Marks;
import com.example.myhosteldemo.model.Marks.Tenth_Marks;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.example.myhosteldemo.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Result_List_Shover_Adapter extends RecyclerView.Adapter<Result_List_Shover_Adapter.ViewHolder> {
    Context context ;
    public static ArrayList<MeritMarksModel> arrayList ;
    public static MeritListModel listModel ;

    public Result_List_Shover_Adapter() {
    }

    public Result_List_Shover_Adapter(Context context, ArrayList<MeritMarksModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_shower_recycler_layout , parent, false) ;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeritMarksModel model = arrayList.get(position) ;

        if(model.getResultOf().equals("10th")){
            fillResultOf10th(holder , model) ;
        }
        else if(model.getResultOf().equals("1st year")){

        }
        else if(model.getResultOf().equals("12th")){

        }
        else if(model.getResultOf().equals("ITI")){

        }
        else{
            fillResultOf3rdYear(holder , model) ;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name , percent , form ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.result_list_name) ;
            percent = itemView.findViewById(R.id.result_list_percent) ;
            form = itemView.findViewById(R.id.result_list_form) ;
        }
    }

    private void fillResultOf10th(ViewHolder holder , MeritMarksModel model){
        HashMap<String , Object> hash = (HashMap<String, Object>) model.getResult();

        String marks = (String) hash.get("marks");
        String percent = (String) hash.get("percent");
        String marksheet = (String) hash.get("marksheet");
        String allotment = (String) hash.get("allotment");
        String aadhar = (String) hash.get("aadhar");
        long time = (long) hash.get("time");

        Tenth_Marks tenth_marks = new Tenth_Marks(marks,percent,marksheet,allotment,aadhar,time) ;

        User user = model.getUser() ;
        holder.name.setText("    " + user.getUsername());
        holder.percent.setText(tenth_marks.getPercent() + " %");

        if(model.getApproval().equals("Unapproved")){
            holder.form.setTextColor(Color.YELLOW);
            holder.form.setText("View");
        }
        else if(model.getApproval().equals("Approved")){
            holder.form.setTextColor(Color.GREEN);
            holder.form.setText("View");
        }
        else{
            holder.form.setTextColor(Color.RED);
            holder.form.setText("View");
        }

        holder.form.setOnClickListener(v -> {
            Intent intent = new Intent(context , Result_10th.class) ;
            intent.putExtra("MeritMarksModel" , model) ;
            intent.putExtra("User" , user) ;
            intent.putExtra("10th" , tenth_marks) ;
            intent.putExtra("MeritListModel" , listModel) ;
            context.startActivity(intent);
        });
    }

    private void fillResultOf3rdYear(ViewHolder holder , MeritMarksModel model){
        HashMap<String , Object> hash = (HashMap<String, Object>) model.getResult();

        String percent3 = (String) hash.get("percentege_of_3rd_sem");
        String percent4 = (String) hash.get("percentege_of_4rd_sem");
        String marksheet3 = (String) hash.get("marksheet_3rd_sem");
        String marksheet4 = (String) hash.get("marksheet_4th_sem");
        String allotment = (String) hash.get("allotment");
        String aadhar = (String) hash.get("aadhar");
        long time = (long) hash.get("time");

        SecondYear_Marks secondYear_marks = new SecondYear_Marks(percent3,
                percent4,
                marksheet3,
                marksheet4,
                allotment,
                aadhar,
                time) ;

        User user = model.getUser() ;
        holder.name.setText("    " + user.getUsername());
        holder.percent.setText(secondYear_marks.getAverage_percentage() + " %");

        if(model.getApproval().equals("Unapproved")){
            holder.form.setTextColor(Color.YELLOW);
            holder.form.setText("View");
        }
        else if(model.getApproval().equals("Approved")){
            holder.form.setTextColor(Color.GREEN);
            holder.form.setText("View");
        }
        else{
            holder.form.setTextColor(Color.RED);
            holder.form.setText("View");
        }

        holder.form.setOnClickListener(v -> {
            Intent intent = new Intent(context , Result_2nd_year.class) ;
            intent.putExtra("MeritMarksModel" , model) ;
            intent.putExtra("User" , user) ;
            intent.putExtra("2nd year" , secondYear_marks) ;
            intent.putExtra("MeritListModel" , listModel) ;
            context.startActivity(intent);
        });
    }
}
