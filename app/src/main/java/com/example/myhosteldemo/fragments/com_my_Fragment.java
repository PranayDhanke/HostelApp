package com.example.myhosteldemo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.Complaint_MyComplaints_Adapter;
import com.example.myhosteldemo.Add_Complaint;
import com.example.myhosteldemo.Complaints;
import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.Complaint_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class com_my_Fragment extends Fragment {

    Button add ;
    ShimmerRecyclerView recyclerView ;
    TextView nodata ;

    ArrayList<Complaint_Model> complaints ;
    Complaint_MyComplaints_Adapter adapter ;

    DatabaseReference database ;
    FirebaseUser user ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_com_my_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add = view.findViewById(R.id.com_add_complaint) ;
        recyclerView = view.findViewById(R.id.com_add_recycler) ;
        nodata = view.findViewById(R.id.com_add_nodata) ;
        complaints = new ArrayList<>() ;
        adapter = new Complaint_MyComplaints_Adapter(getActivity() ,complaints) ;

        add.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Add_Complaint.class));
        });

        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);

        loadComplaints() ;
    }

    private void loadComplaints(){
        recyclerView.showShimmerAdapter();

        //loading complaints
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    complaints.clear();
                    nodata.setVisibility(View.GONE);
                    for(DataSnapshot data : snapshot.getChildren()){
                        Complaint_Model cm = data.getValue(Complaint_Model.class) ;
                        complaints.add(cm) ;
                    }
                    Collections.reverse(complaints);
                    recyclerView.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
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
                nodata.setText("Failed to load Complaints");
                nodata.setVisibility(View.VISIBLE);
            }
        } ;

       database.child("Complaints/" + user.getUid() + "/")
               .addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalData.complaintAdded){
            loadComplaints();
            GlobalData.complaintAdded = false ;
        }

    }

}