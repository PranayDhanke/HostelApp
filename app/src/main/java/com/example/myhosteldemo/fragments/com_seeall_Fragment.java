package com.example.myhosteldemo.fragments;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.Complaint_seeall_Adapter;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Complaint_Model;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class com_seeall_Fragment extends Fragment {

    FirebaseUser fuser ;
    StorageReference storage ;
    DatabaseReference database ;

    //Custom Filter Dialog Variables
    Dialog dialog ;
    String filter_username = "" ;
    String filter_completeness = "" ;
    String filter_others = "Newest First" ;
    EditText filteredt_username ;
    Chip complete_pen , complete_pro , complete_res ;
    Chip liked_most , liked_least ;
    Chip time_new , time_old ;

    //other components
    ShimmerRecyclerView recyclerView ;
    Complaint_seeall_Adapter adapter ;
    ArrayList<Complaint_Model> complaints ;
    TextView nodata ;
    SwipeRefreshLayout refreshLayout ;
    ProgressBar progressBar ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_com_seeall_, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dialog = new Dialog(getActivity()) ;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();

        View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.com_seeall_filter_custom_dialog , null) ;
        dialog.setContentView(dialogview);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT , WindowManager.LayoutParams.WRAP_CONTENT);

        filteredt_username = dialog.findViewById(R.id.com_seeall_filter_username) ;
        complete_pen = dialog.findViewById(R.id.com_seeall_filter_completepen) ;
        complete_pro = dialog.findViewById(R.id.com_seeall_filter_completepro) ;
        complete_res = dialog.findViewById(R.id.com_seeall_filter_completeres) ;
        liked_most = dialog.findViewById(R.id.com_seeall_filter_likedmost) ;
        liked_least = dialog.findViewById(R.id.com_seeall_filter_likedleast) ;
        time_new = dialog.findViewById(R.id.com_seeall_filter_timenew) ;
        time_old = dialog.findViewById(R.id.com_seeall_filter_timeold) ;


        dialog.findViewById(R.id.com_seeall_filter_ok).setOnClickListener(v -> {
            //getting username
            filter_username = filteredt_username.getText().toString() ;

            //getting correctness
            if(complete_pen.isChecked()){
                filter_completeness = "Pending" ;
            }
            else if(complete_pro.isChecked()){
                filter_completeness = "Under Progress" ;
            }
            else if(complete_res.isChecked()){
                filter_completeness = "Resolved" ;
            }
            else{
                filter_completeness = "" ;
            }

            GlobalData.filter_completeness = filter_completeness ;

            //getting others
            if(liked_most.isChecked()){
                filter_others = liked_most.getText().toString() ;
            }
            else if(liked_least.isChecked()){
                filter_others = liked_least.getText().toString() ;
            }
            else if(time_new.isChecked()){
                filter_others = time_new.getText().toString() ;
            }
            else if(time_old.isChecked()){
                filter_others = time_old.getText().toString() ;
            }
            else{
                filter_others = "" ;
            }

            Toast.makeText(getActivity(), "Filters Applied", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            performFiltering();
        });

        dialog.findViewById(R.id.com_seeall_filter_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //getActivity().getMenuInflater().inflate(R.menu.com_seeall_menu , null) ;
        inflater.inflate(R.menu.com_seeall_menu , menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.com_seeall_recycler) ;
        nodata = view.findViewById(R.id.com_seeall_nodata) ;
        refreshLayout = view.findViewById(R.id.com_seeall_refresh) ;
        progressBar = view.findViewById(R.id.com_seeall_progress) ;
        complaints = new ArrayList<>() ;
        adapter = new Complaint_seeall_Adapter(getActivity() , complaints) ;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.showShimmerAdapter();

        loadAllData() ;

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(filter_username.trim().equals("")){
                    loadAllData();
                }
                else
                {
                    getComplaintsByUsername(filter_username);
                }
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.com_seeall_menu_filter:
                showCustomFilterDialog() ;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCustomFilterDialog(){
        dialog.show();
    }

    private void loadAllData(){
        complaints.clear();
        GlobalData.complaints.clear();
        ArrayList<String> keys = new ArrayList<>() ;
        nodata.setVisibility(View.GONE);

        database.child("Complaints/")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            nodata.setVisibility(View.GONE);
                            //Toast.makeText(getActivity(), "Data Exists", Toast.LENGTH_SHORT).show();
                            for(DataSnapshot data : snapshot.getChildren()){
                                //Toast.makeText(getActivity(), "Adding key", Toast.LENGTH_SHORT).show();
                                if(!fuser.getUid().equals(data.getKey())){
                                    keys.add(data.getKey()) ;
                                }
                            }

                            loadChildrens(keys) ;
                        }
                        else{
                            recyclerView.hideShimmerAdapter();
                            nodata.setText("No Complaints Found");
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        recyclerView.hideShimmerAdapter();
                        nodata.setText("Unable to load Complaints");
                        nodata.setOnClickListener( v -> {
                            loadAllData();
                        });
                        nodata.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadChildrens(ArrayList<String> keys){
        //Toast.makeText(getActivity(), "Keys size : " + keys.size(), Toast.LENGTH_SHORT).show();

        if(keys.size() == 0){
            recyclerView.hideShimmerAdapter();
            nodata.setText("No Complaints Found");
            nodata.setVisibility(View.VISIBLE);
            return ;
        }

        for(int i = 0 ; i < keys.size() ; i++){
            database.child("Complaints/" + keys.get(i) + "/")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                recyclerView.hideShimmerAdapter();
                                for(DataSnapshot data : snapshot.getChildren()){
                                    //Toast.makeText(getActivity(), "Adding data", Toast.LENGTH_SHORT).show();
                                    Complaint_Model cm = data.getValue(Complaint_Model.class) ;
                                    complaints.add(cm) ;
                                    GlobalData.complaints.add(cm) ;
                                    //Log.d("Model Complaint" , cm.toString()) ;
                                    adapter.notifyItemInserted(complaints.size());
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //todo
                        }
                    });
        }

    }

    private void performFiltering(){
        String str = "" ;

        if(!filter_username.trim().equals("")){
            str += filter_username.trim() ;
            str += "\n" ;
            getComplaintsByUsername(filter_username) ;
            return ;
        }

        adapter.sortData(filter_completeness , filter_others);
    }

    private void getComplaintsByUsername(String filter_username){
        nodata.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.showShimmerAdapter();

        database.child("Users/").orderByChild("username").equalTo(filter_username)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Toast.makeText(getActivity(), "Exist = " + snapshot.exists() + "\nkey=" + snapshot.getKey(), Toast.LENGTH_SHORT).show();
                        if(snapshot.exists()){
                            loadUserComplaints(snapshot.getChildren().iterator().next().getKey()); ;
                        }
                        else{
                            recyclerView.hideShimmerAdapter();
                            nodata.setText("No Complaints exist by user " + filter_username);
                            recyclerView.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        recyclerView.hideShimmerAdapter();
                        nodata.setText("Unable to load complaints by " + filter_username);
                        recyclerView.setVisibility(View.GONE);
                        nodata.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadUserComplaints(String key){
        database.child("Complaints/" + key + "/")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getActivity(), "Private complaints are Hidden", Toast.LENGTH_SHORT).show();
                            complaints.clear();
                            GlobalData.complaints.clear();
                            recyclerView.hideShimmerAdapter();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                //Toast.makeText(getActivity(), "Adding data", Toast.LENGTH_SHORT).show();
                                Complaint_Model cm = data.getValue(Complaint_Model.class);
                                complaints.add(cm);
                                GlobalData.complaints.add(cm);
                                //Log.d("Model Complaint" , cm.toString()) ;
                                adapter.notifyItemInserted(complaints.size());
                            }

                            adapter.sortData(filter_completeness , filter_others);

                        }
                        else{
                            recyclerView.hideShimmerAdapter();
                            nodata.setText("No Complaints exist by user " + filter_username);
                            recyclerView.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        recyclerView.hideShimmerAdapter();
                        nodata.setText("Unable to load complaints by " + filter_username);
                        recyclerView.setVisibility(View.GONE);
                        nodata.setVisibility(View.VISIBLE);
                    }
                }) ;
    }

}

