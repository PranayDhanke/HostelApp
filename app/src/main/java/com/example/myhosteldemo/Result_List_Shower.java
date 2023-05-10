package com.example.myhosteldemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.myhosteldemo.Adapter.Result_List_Shover_Adapter;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MeritListModel;
import com.example.myhosteldemo.model.MeritMarksModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class Result_List_Shower extends AppCompatActivity {

    Toolbar toolbar ;

    FirebaseFirestore firestore ;

    Intent prevIntent ;
    MeritListModel model ;

    static Dialog dialog ;
    Spinner branches ;
    TextView apply , cancel ;
    RadioGroup gender , year ;
    String[] spinner_branches = {"Computer" , "Mechanical" , "Chemical" , "Electrical" , "Electronics" , "Civil"} ;
    ArrayAdapter spinner_adapter ;
    String y = "", g = "", b = "";
    boolean back = true ;

    TextView nodata ;
    ShimmerRecyclerView recyclerView ;
    LinearLayout self ;

    ArrayList<MeritMarksModel> arrayList ;
    ArrayList<MeritMarksModel> searchList ;
    Result_List_Shover_Adapter resultListShoverAdapter ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list_shower);

        toolbar = findViewById(R.id.result_list_toolbar) ;
        nodata = findViewById(R.id.result_list_nodata) ;
        recyclerView = findViewById(R.id.result_list_recyclerview) ;
        self = findViewById(R.id.result_list_selfll) ;

        dialog = new Dialog(Result_List_Shower.this) ;
        dialog.setContentView(R.layout.activity_result_filter);
        dialog.setCancelable(false);

        branches = dialog.findViewById(R.id.result_filter_spinner) ;
        apply = dialog.findViewById(R.id.result_filter_apply) ;
        cancel = dialog.findViewById(R.id.result_filter_cancel) ;
        gender = dialog.findViewById(R.id.result_filter_gendergroup) ;
        year = dialog.findViewById(R.id.result_filter_yeargroup) ;
        spinner_adapter = new ArrayAdapter(this , R.layout.drop_down_item , spinner_branches) ;
        branches.setAdapter(spinner_adapter);

        arrayList = new ArrayList<MeritMarksModel>() ;
        searchList = new ArrayList<MeritMarksModel>() ;
        resultListShoverAdapter = new Result_List_Shover_Adapter(this , arrayList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resultListShoverAdapter);
        recyclerView.showShimmerAdapter();

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.result_filter_gendermale){
                    g = "Boys" ;
                }
                else{
                    g = "Girls" ;
                }
            }
        });

        year.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.result_filter_year1){
                    y = "1st year" ;
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.result_filter_year2){
                    y = "2nd year" ;
                }
                else{
                    y = "3rd year" ;
                }
            }
        });

        apply.setOnClickListener(v -> {
            if(validateFilter()){
                b = branches.getSelectedItem().toString() ;
                loadAllResult() ;
                dialog.dismiss();
                back = false ;
            }
            else{
                Toast.makeText(this, "Please select all the filters", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> {
            if(back){
                finish();
            }
            else{
                dialog.dismiss();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);

        firestore = FirebaseFirestore.getInstance() ;
        prevIntent = getIntent() ;
        model = (MeritListModel) prevIntent.getSerializableExtra("MeritListModel");
        Result_List_Shover_Adapter.listModel = model ;
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        if(GlobalData.formDelete >= 0){
            arrayList.remove(GlobalData.formDelete) ;
            resultListShoverAdapter.notifyItemRemoved(GlobalData.formDelete);
            GlobalData.formDelete = -1 ;
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_list_toolbar_menu , menu);
        MenuItem menuItem = menu.findItem(R.id.search) ;

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter candidate name");

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForUsername(searchView.getQuery().toString()) ;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    Result_List_Shover_Adapter.arrayList = arrayList ;
                    resultListShoverAdapter.notifyDataSetChanged();
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        searchView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE)) ;

        menu.findItem(R.id.filter).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dialog.show();
                return false;
            }
        }) ;

        return super.onCreateOptionsMenu(menu);
    }

    private boolean validateFilter(){
        if(g.equals("")){
            return false ;
        }
        if(y.equals("")){
            return false ;
        }
        return true ;
    }

    private void loadAllResult(){
       if(y.equals("1st year")){
           loadResultOf1stYear() ;
       }
       else if(y.equals("2nd year")){
           //loadResultOf2ndYear() ;
       }
       else{
           loadResultOf3rdYear() ;
       }
    }

    private void loadResultOf1stYear(){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.showShimmerAdapter();
        nodata.setVisibility(View.GONE);

        firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection(y)
                .document(g)
                .collection(b)
                .orderBy("result.percent" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            recyclerView.hideShimmerAdapter();
                            recyclerView.setVisibility(View.GONE);
                            nodata.setText("Unable to load\nTap to try again");
                            nodata.setVisibility(View.VISIBLE);
                            nodata.setOnClickListener(v -> {
                                loadResultOf1stYear();
                            });
                        }
                        else{
                            arrayList.clear();

                            if(value.isEmpty()){
                                recyclerView.hideShimmerAdapter();
                                recyclerView.setVisibility(View.GONE);
                                nodata.setText("No data found");
                                nodata.setVisibility(View.VISIBLE);
                            }
                            else{
                                int cur = 0 ;
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                recyclerView.hideShimmerAdapter();

                                for(DocumentSnapshot snapshot : value.getDocuments()){
                                    MeritMarksModel meritMarksModel = snapshot.toObject(MeritMarksModel.class) ;
                                    arrayList.add(meritMarksModel) ;
                                    resultListShoverAdapter.notifyItemInserted(cur);
                                    cur++ ;
                                }

                            }
                        }
                    }
                }) ;
    }

    private void loadResultOf3rdYear(){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.showShimmerAdapter();
        nodata.setVisibility(View.GONE);

        firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection(y)
                .document(g)
                .collection(b)
                .orderBy("result.average_percentage" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            recyclerView.hideShimmerAdapter();
                            recyclerView.setVisibility(View.GONE);
                            nodata.setText("Unable to load\nTap to try again");
                            nodata.setVisibility(View.VISIBLE);
                            nodata.setOnClickListener(v -> {
                                loadResultOf1stYear();
                            });
                        }
                        else{

                            if(value.isEmpty()){
                                recyclerView.hideShimmerAdapter();
                                recyclerView.setVisibility(View.GONE);
                                nodata.setText("No data found");
                                nodata.setVisibility(View.VISIBLE);
                            }
                            else{
                                arrayList.clear();
                                int cur = 0 ;
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                recyclerView.hideShimmerAdapter();

                                for(DocumentSnapshot snapshot : value.getDocuments()){
                                    MeritMarksModel meritMarksModel = snapshot.toObject(MeritMarksModel.class) ;
                                    arrayList.add(meritMarksModel) ;
                                    resultListShoverAdapter.notifyItemInserted(cur);
                                    cur++ ;
                                }

                            }
                        }
                    }
                }) ;
    }

    private void searchForUsername(String query){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.showShimmerAdapter();
        nodata.setVisibility(View.GONE);

        firestore.collection("MeritListData")
                .document(model.getTitle())
                .collection(y)
                .document(g)
                .collection(b)
                .orderBy("user.username")
                .whereGreaterThanOrEqualTo("user.username" , query)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            recyclerView.hideShimmerAdapter();
                            recyclerView.setVisibility(View.GONE);
                            nodata.setText("Unable to load\nTap to try again");
                            nodata.setVisibility(View.VISIBLE);
                            nodata.setOnClickListener(v -> {
                                searchForUsername(query);
                            });
                        }
                        else{
                            searchList.clear();

                            if(value.isEmpty()){
                                recyclerView.hideShimmerAdapter();
                                recyclerView.setVisibility(View.GONE);
                                nodata.setText("No data exist for user : " + query);
                                nodata.setVisibility(View.VISIBLE);
                            }
                            else{
                                int cur = 0 ;
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                recyclerView.hideShimmerAdapter();
                                resultListShoverAdapter.arrayList = searchList ;
                                for(DocumentSnapshot snapshot : value.getDocuments()){
                                    MeritMarksModel meritMarksModel = snapshot.toObject(MeritMarksModel.class) ;
                                    if(meritMarksModel.getUser().getUsername().contains(query) || meritMarksModel.getUser().getUsername().contains(query.toLowerCase())){
                                        searchList.add(meritMarksModel) ;
                                        resultListShoverAdapter.notifyItemInserted(cur);
                                        cur++ ;
                                    }
                                }

                                if(searchList.size() == 0){
                                    recyclerView.hideShimmerAdapter();
                                    recyclerView.setVisibility(View.GONE);
                                    nodata.setText("No data exist for user : " + query);
                                    nodata.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }) ;
    }
}