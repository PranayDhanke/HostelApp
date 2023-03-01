package com.example.myhosteldemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhosteldemo.Merit_Confirm;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.model.MeritListModel;

import java.util.ArrayList;

public class MeritList_Select_Adapter extends RecyclerView.Adapter<MeritList_Select_Adapter.ViewHolder> {

    Context context ;
    ArrayList<MeritListModel> arrayList ;

    public MeritList_Select_Adapter(Context context, ArrayList<MeritListModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.merit_list_info ,parent , false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeritListModel model = arrayList.get(position) ;

        holder.title.setText(model.getTitle());
        holder.desc.setText(model.getDescription());
        holder.start.setText(model.getStartDate());
        holder.end.setText(model.getEndDate());

        boolean isOpen = getOpenStatus(model.getStartLong() , model.getEndLong()) ;

        if(isOpen){
            holder.status.setText("Open");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }
        else{
            holder.status.setText("Closed");
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.fill.setOnClickListener(v -> {
            if(isOpen){
                //Toast.makeText(context, "Show fill form activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context , Merit_Confirm.class) ;
                intent.putExtra("MeritListModel" , model) ;
                context.startActivity(intent);
            }
            else{
                Toast.makeText(context, "This merit list has stopped taking responses", Toast.LENGTH_SHORT).show();
            }
        });

        holder.view.setOnClickListener(v -> {

        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title , desc , start , end , view , fill , status ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.merit_select_title) ;
            desc = itemView.findViewById(R.id.merit_select_desc) ;
            start = itemView.findViewById(R.id.merit_select_start) ;
            end = itemView.findViewById(R.id.merit_select_end) ;
            view = itemView.findViewById(R.id.merit_select_view) ;
            fill = itemView.findViewById(R.id.merit_select_fill) ;
            status = itemView.findViewById(R.id.merit_select_status) ;
        }
    }

    private boolean getOpenStatus(long start , long end){
        if(System.currentTimeMillis() < start){
            return false ;
        }
        if(System.currentTimeMillis() > end){
            return false ;
        }
        return true ;
    }

}
