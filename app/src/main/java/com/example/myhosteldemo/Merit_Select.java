package com.example.myhosteldemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.MeritList_Select_Adapter;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MeritListModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Merit_Select extends AppCompatActivity {

    Toolbar toolbar ;

    FloatingActionButton fab ;
    ShimmerRecyclerView recyclerView ;
    TextView nodata ;

    FirebaseFirestore firestore ;
    ArrayList<MeritListModel> arrayList ;
    MeritList_Select_Adapter adapter ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_select);

        toolbar = findViewById(R.id.select_toolbar) ;
        fab = findViewById(R.id.select_fab) ;
        recyclerView = findViewById(R.id.select_recycler) ;
        nodata = findViewById(R.id.merit_select_nodata) ;

        firestore = FirebaseFirestore.getInstance() ;
        arrayList = new ArrayList<>() ;
        adapter = new MeritList_Select_Adapter(this , arrayList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            if(GlobalData.isAdmin){
                startActivity(new Intent(this , Merit_AddList.class));
            }
            else{
                Toast.makeText(this, "Please login as admin first", Toast.LENGTH_SHORT).show();
            }
        });

        loadAllMeritLists() ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        if(GlobalData.meritListAdded){
            loadAllMeritLists();
            GlobalData.meritListAdded = false ;
        }
        super.onResume();
    }

    private void loadAllMeritLists(){
        arrayList.clear();
        recyclerView.showShimmerAdapter();

        firestore.collection("MeritList")
                .orderBy("createDate" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            recyclerView.hideShimmerAdapter();
                            recyclerView.setVisibility(View.GONE);
                            nodata.setText("Unable to load Merit List\nTap to try again");
                            nodata.setVisibility(View.VISIBLE);
                            nodata.setOnClickListener(v -> {
                                loadAllMeritLists();
                            });
                        }
                        else if(value.isEmpty()){
                            recyclerView.hideShimmerAdapter();
                            recyclerView.setVisibility(View.GONE);
                            nodata.setText("No Merit Lists Available");
                            nodata.setVisibility(View.VISIBLE);
                        }
                        else{
                            List<DocumentSnapshot> data = value.getDocuments() ;
                            for(int i = 0 ; i < data.size() ; i++){
                                arrayList.add(data.get(i).toObject(MeritListModel.class)) ;
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.hideShimmerAdapter();
                        }
                    }
                }) ;
    }

}