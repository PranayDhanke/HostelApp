package com.example.myhosteldemo.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Complaint_Model;
import com.example.myhosteldemo.model.Complaint_MyCom_Viewpager_Model;
import com.example.myhosteldemo.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Complaint_MyComplaints_Adapter extends RecyclerView.Adapter<Complaint_MyComplaints_Adapter.ViewHolder> {
    Context context ;
    ArrayList<Complaint_Model> complaints ;
    SimpleDateFormat sdf ;

    DatabaseReference database ;
    StorageReference storage ;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor ;

    public Complaint_MyComplaints_Adapter(Context context, ArrayList<Complaint_Model> complaints) {
        this.context = context;
        this.complaints = complaints;
        sdf = new SimpleDateFormat("dd-MMM-yyyy  hh:mm:ss aa");
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        preferences = context.getSharedPreferences("Likes", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_complaint_layout , parent , false) ;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint_Model cm = complaints.get(position) ;

        holder.profile.setImageResource(R.drawable.profile_pic3);
        holder.name.setText(cm.getCom_by());
        holder.date.setText(sdf.format(new Date(cm.getTime())));
        holder.topic.setText(cm.getTopic());
        holder.desc.setText(cm.getDesc());
        holder.likes.setText(cm.getUp_votes() + " Likes");
        holder.likeImage.setImageResource(R.drawable.heart_nofill);

        database.child("Users/" + cm.getCom_by()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class) ;
                holder.name.setText(user.getUsername());
                Glide.with(context)
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_pic3)
                        .into(holder.profile) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<String> images = cm.getImages() ;

        if(images != null){
            if(images.size() > 0){
                View root = LayoutInflater.from(context).inflate(R.layout.mycomplaint_image_viewpager , null)  ;
                ArrayList<Complaint_MyCom_Viewpager_Model> model = new ArrayList<>() ;
                for(int i = 0 ; i < images.size() ; i++){
                    model.add(new Complaint_MyCom_Viewpager_Model(root.findViewById(R.id.mycomlay_view_image), images.get(i))) ;
                }
                Complaint_MyCom_Viewpager_Adapter adapter = new Complaint_MyCom_Viewpager_Adapter(context , model) ;
                holder.viewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                holder.viewPager.setVisibility(View.VISIBLE);
                holder.indicator.setVisibility(View.VISIBLE);
                holder.indicator.attachTo(holder.viewPager);

               holder.viewPager.setUserInputEnabled(true);
            }
        }


        holder.likeImage.setOnClickListener(v -> {

            if(preferences.getBoolean(cm.getKey() , false)){
                holder.likeImage.setImageResource(R.drawable.heart_nofill);
                if(cm.getUp_votes() == 0 || cm.getUp_votes() < 0){
                    holder.likes.setText(0  + " Likes");
                }
                else{
                    cm.setUp_votes(cm.getUp_votes()-1);
                    holder.likes.setText(cm.getUp_votes()  + " Likes");
                }
                editor.putBoolean(cm.getKey() , false) ;
                editor.apply();
                Map<String , Object> hash = new HashMap() ;
                hash.put("up_votes" , ServerValue.increment(-1)) ;
                database.child(cm.getKey()).updateChildren(hash).
                        addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                holder.likeImage.setEnabled(true);
                                //holder.likes.setText(cm.getUp_votes()-1 + " Likes");
                            }
                            else{
                                holder.likeImage.setEnabled(true);
                                holder.likeImage.setImageResource(R.drawable.heart_fill);
                                cm.setUp_votes(cm.getUp_votes()+1);
                                holder.likes.setText(cm.getUp_votes()  + " Likes");
                                editor.putBoolean(cm.getKey() , true) ;
                                editor.apply();
                                Toast.makeText(context, "Failed to unlike post", Toast.LENGTH_SHORT).show();
                            }
                        }) ;
            }
            else{
                holder.likeImage.setImageResource(R.drawable.heart_fill);
                cm.setUp_votes(cm.getUp_votes()+1);
                holder.likes.setText(cm.getUp_votes() + " Likes");
                editor.putBoolean(cm.getKey() , true) ;
                editor.apply();
                Map<String , Object> hash = new HashMap() ;
                hash.put("up_votes" , ServerValue.increment(1)) ;
                database.child(cm.getKey()).updateChildren(hash)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        holder.likeImage.setEnabled(true);
                                        //holder.likes.setText(cm.getUp_votes() + 1 + " Likes");
                                    }
                                    else{
                                        holder.likeImage.setEnabled(true);
                                        holder.likeImage.setImageResource(R.drawable.heart_nofill);
                                        cm.setUp_votes(cm.getUp_votes()-1);
                                        holder.likes.setText(cm.getUp_votes() + " Likes");
                                        editor.putBoolean(cm.getKey() , false) ;
                                        editor.apply();
                                        Toast.makeText(context, "Failed to add like to post", Toast.LENGTH_SHORT).show();
                                    }
                                }) ;
            }
        });

        if(preferences.getBoolean(cm.getKey() , false)){
            holder.likeImage.setImageResource(R.drawable.heart_fill);
        }
        else{
            holder.likeImage.setImageResource(R.drawable.heart_nofill);
        }

        holder.kebab.setOnClickListener(v -> {
            showPopup(v , cm.getKey()) ;
        });

        setUpStatus(holder.pending , cm.getStatus()) ;

    }

    @Override
    public int getItemCount() {
        return complaints.size() ;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile ;
        TextView name , date , topic , desc , likes , pending ;
        ViewPager2 viewPager ;
        WormDotsIndicator indicator ;
        ImageView likeImage ;
        ImageView kebab ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.mycomlay_image) ;
            name = itemView.findViewById(R.id.mycomlay_name) ;
            date = itemView.findViewById(R.id.mycomlay_time) ;
            topic = itemView.findViewById(R.id.mycomlay_topic) ;
            desc = itemView.findViewById(R.id.mycomlay_desc) ;
            likes = itemView.findViewById(R.id.mycomlay_likes) ;
            pending = itemView.findViewById(R.id.mycomlay_pending) ;
            viewPager = itemView.findViewById(R.id.mycomlay_view) ;
            indicator = itemView.findViewById(R.id.mycomlay_dot) ;
            likeImage = itemView.findViewById(R.id.mycomlay_likeimage) ;
            kebab = itemView.findViewById(R.id.mycomlay_kebab) ;
        }
    }

    private void showPopup(View v , String key){
        PopupMenu popupMenu = new PopupMenu(context , v) ;
        popupMenu.inflate(R.menu.mycomlay_kebab_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.mycomlay_kebab_copyid:
                    copyToClipBoard(key);
                    return true ;
                default:
                    return false ;
            }
        });
        popupMenu.show();
    }

    private void copyToClipBoard(String key){
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE) ;
        ClipData clipData = ClipData.newPlainText("Complaint Id" , key) ;
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "Complaint Id copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void setUpStatus(TextView view , String status){
        view.setText(status);

        if(status.equals("Pending")){
            //do nothing
        }
        else if(status.equals("Under Progress")){
            view.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        else{
            view.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

}
