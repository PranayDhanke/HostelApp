package com.example.myhosteldemo.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.fragments.com_seeall_Fragment;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Complaint_seeall_Adapter extends RecyclerView.Adapter<Complaint_MyComplaints_Adapter.ViewHolder>{
    Context context ;
    ArrayList<Complaint_Model> complaints ;
    ArrayList<Complaint_Model> temp_complaints ;
    ArrayList<Complaint_Model> all_complaints ;
    SimpleDateFormat sdf ;

    DatabaseReference database ;
    StorageReference storage ;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor ;

    HashMap<String , Boolean> filterMap ;

    public Complaint_seeall_Adapter(Context context, ArrayList<Complaint_Model> complaints) {
        this.context = context;
        this.complaints = complaints;
        sdf = new SimpleDateFormat("dd-MMM-yyyy  hh:mm:ss aa") ;
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        preferences = context.getSharedPreferences("Likes" , Context.MODE_PRIVATE);
        editor = preferences.edit() ;
        filterMap = new HashMap<>() ;
        temp_complaints = new ArrayList<>() ;
        all_complaints = new ArrayList<>() ;
    }


    @NonNull
    @Override
    public Complaint_MyComplaints_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_complaint_layout , parent , false) ;
        return new Complaint_MyComplaints_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Complaint_MyComplaints_Adapter.ViewHolder holder, int position) {
        Complaint_Model cm = complaints.get(position) ;



        holder.profile.setImageResource(R.drawable.user_modern);
        holder.name.setText(cm.getCom_by());
        holder.date.setText(sdf.format(new Date(cm.getTime())));
        holder.topic.setText(cm.getTopic());
        holder.desc.setText(cm.getDesc());
        holder.likes.setText(cm.getUp_votes() + " Likes");
        holder.likeImage.setImageResource(R.drawable.heart_nofill);

        if(!cm.getPri()){
            database.child("Users/" + cm.getCom_by()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class) ;
                    holder.name.setText(user.getUsername());
                    Glide.with(context)
                            .load(user.getProfile())
                            .placeholder(R.drawable.user_modern)
                            .into(holder.profile) ;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.name.setText("Anonymous");
            holder.profile.setImageResource(R.drawable.anonymous2);
        }


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
            else{
                holder.viewPager.setVisibility(View.GONE);
                holder.indicator.setVisibility(View.GONE);
            }
        }
        else{
            holder.viewPager.setVisibility(View.GONE);
            holder.indicator.setVisibility(View.GONE);
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
        ConstraintLayout root ;
        CardView card ;
        CircleImageView profile ;
        TextView name , date , topic , desc , likes , pending ;
        ViewPager2 viewPager ;
        WormDotsIndicator indicator ;
        ImageView likeImage ;
        ImageView kebab ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.mycomlay_root) ;
            card = itemView.findViewById(R.id.mycomlay_card) ;
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
            view.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if(status.equals("Under Progress")){
            view.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        else{
            view.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    public void sortData(String completeness , String others){
        if(!completeness.equals("")){
            ArrayList<Complaint_Model> temp = new ArrayList<>() ;
            complaints.clear();
            complaints.addAll(GlobalData.complaints) ;
            for(int i = 0 ; i < complaints.size() ; i++){
                if(complaints.get(i).getStatus().equals(completeness)){
                    temp.add(complaints.get(i)) ;
                    Log.d("Adding temp = " , complaints.get(i).toString()) ;
                }
            }

            complaints.clear();
            complaints.addAll(temp) ;
            Log.d("Size temp" , "" + complaints.size()) ;
            notifyDataSetChanged();
        }
        else{
            complaints.clear();
            complaints.addAll(GlobalData.complaints) ;
            Log.d("Size seeall" , "" + GlobalData.complaints.size()) ;
            notifyDataSetChanged();
        }

        if(!others.equals("")){

            if(others.equals("Most Liked")){
                Collections.sort(complaints , new MostLikeSorter());
            }
            else if(others.equals("Least Liked")){
                Collections.sort(complaints , new LeastLikeSorter());
            }
            else if(others.equals("Newest First")){
                Collections.sort(complaints , new NewestTimeSorter());
            }
            else if(others.equals("Oldest First")){
                Collections.sort(complaints , new OldestTimeSorter());
            }

          notifyDataSetChanged();
        }
    }

}

//sort classes
class NewestTimeSorter implements Comparator<Complaint_Model> {

    @Override
    public int compare(Complaint_Model cm1, Complaint_Model cm2) {
        return (int) (cm1.getTime() - cm2.getTime());
    }
}

class OldestTimeSorter implements Comparator<Complaint_Model>{

    @Override
    public int compare(Complaint_Model cm1, Complaint_Model cm2) {
        return (int) (cm2.getTime() - cm1.getTime());
    }
}

class LeastLikeSorter implements Comparator<Complaint_Model>{

    @Override
    public int compare(Complaint_Model cm1 , Complaint_Model cm2) {
        return cm1.getUp_votes() - cm2.getUp_votes() ;
    }
}

class MostLikeSorter implements Comparator<Complaint_Model>{

    @Override
    public int compare(Complaint_Model cm1 , Complaint_Model cm2) {
        return cm2.getUp_votes() - cm1.getUp_votes() ;
    }
}
