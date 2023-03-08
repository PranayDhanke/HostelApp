package com.example.myhosteldemo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.AdminModel;
import com.example.myhosteldemo.model.MessMenuModel;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AdminWorkSpaceFragment extends Fragment {

    EditText email , password , menulist ;
    TextView addadminbtn , addmenubtn ;
    TextView nodata ;
    CardView cardadmin ;
    NestedScrollView scrollView ;
    Spinner menuspinner ;

    DatabaseReference database ;
    FirebaseFirestore firestore ;

    static ProgressDialog dialog ;

    ArrayAdapter menuadapter ;
    SimpleDateFormat sdf ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance() ;
        dialog = new ProgressDialog(getActivity()) ;

        String[] menulist = {"Breakfast" , "Lunch" , "Dinner"} ;
        menuadapter = new ArrayAdapter(getActivity() , R.layout.drop_down_item , menulist) ;

        sdf = new SimpleDateFormat("dd-MMM-yyyy") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_work_space, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.admin_work_email) ;
        password = view.findViewById(R.id.admin_work_password) ;
        menulist = view.findViewById(R.id.admin_work_menulist) ;
        addadminbtn = view.findViewById(R.id.admin_work_addadmin) ;
        addmenubtn = view.findViewById(R.id.admin_work_addmenu) ;
        nodata = view.findViewById(R.id.admin_work_nodata) ;
        cardadmin = view.findViewById(R.id.admin_work_cardaddadmin) ;
        scrollView = view.findViewById(R.id.admin_work_scroll) ;
        menuspinner = view.findViewById(R.id.admin_work_menuspiner) ;

        menuspinner.setAdapter(menuadapter);

        addadminbtn.setOnClickListener(v -> {
            dialog.setTitle("adding Admin");
            dialog.setMessage("Please wait....");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.information) ;
            dialog.show();
            uploadData() ;
        });

        addmenubtn.setOnClickListener(v ->{
            dialog.setTitle("Updating Menu");
            dialog.setMessage("Please wait....");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.information) ;
            dialog.show();
            uploadMessMenu() ;
        });


        if(!GlobalData.isAdmin){
            nodata.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
        else{
            nodata.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        if(!GlobalData.isAdmin){
            nodata.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
        else{
            nodata.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private void uploadData(){
        if(isValidate()){
            getUserFromDatabase(email.getText().toString()) ;
        }
        else{
            dialog.dismiss();
        }
    }

    private boolean isValidate(){
        if(email.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), "Enter enter email", Toast.LENGTH_SHORT).show();
            email.setError("Please enter email" , null) ;
            email.requestFocus() ;
            return false ;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
            email.setError("Invalid email" , null) ;
            email.requestFocus() ;
            return false ;
        }
        if(password.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), "Enter enter password", Toast.LENGTH_SHORT).show();
            password.setError("Please enter password" , null) ;
            password.requestFocus() ;
            return false ;
        }
        if(password.getText().toString().length() < 8){
            Toast.makeText(getActivity(), "Password should be minimum 8 characters long", Toast.LENGTH_SHORT).show();
            password.setError("Password should be minimum 8 characters long" , null) ;
            password.requestFocus() ;
            return false ;
        }
        return true ;
    }

    private void getUserFromDatabase(String email){
        database.child("Users/")
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            DataSnapshot data = snapshot.getChildren().iterator().next() ;
                            upload(data.getKey(), data.getValue(User.class)) ;
                            //Toast.makeText(getActivity(), "" + snapshot.getKey(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "No such user exist"
                            ,"Please check entered email address",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        AlertUtil.showAlertDialog(getActivity(),
                                "Failure"
                                ,"Failed to add admin try again",
                                true,
                                R.drawable.error,
                                "Ok");
                    }
                });
    }

    private void upload(String key , User user){
        AdminModel model = new AdminModel(user , password.getText().toString()) ;

        firestore.collection("Admins")
                .document(key).set(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "Success"
                                    ,"Admin added successfully",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                        else{
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "Failure"
                                    ,"Failed to add admin try again",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }
                }) ;
    }

    private boolean isMessValidated(){
        if(menulist.getText().toString().trim().equals("")){
            //Toast.makeText(getActivity(), "Please enter menu list", Toast.LENGTH_SHORT).show();
            menulist.setError("Please enter menu list" , null) ;
            menulist.requestFocus() ;
            return false ;
        }
        return true ;
    }

    private void uploadMessMenu(){
        if(isMessValidated()){
            String doc =  sdf.format(System.currentTimeMillis()) ;
            MessMenuModel model = new MessMenuModel(menuspinner.getSelectedItem().toString() ,
                    doc ,
                    menulist.getText().toString().trim() ,
                    FirebaseAuth.getInstance().getCurrentUser().getUid()) ;

            HashMap<String , Object> hash = new HashMap<>() ;
            hash.put(menuspinner.getSelectedItem().toString() , menulist.getText().toString().trim()) ;
            hash.put("uploadBy" , model.getUploadBy()) ;
            hash.put("key" , model.getKey()) ;

            firestore.collection("Mess Menu")
                    .document(doc)
                    .set(hash , SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "Success"
                                    ,"Menu updated successfully",
                                    true,
                                    R.drawable.information,
                                    "Ok");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "Failure"
                                    ,"Failed to update menu",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                    }) ;
        }
        else{
            dialog.dismiss();
        }
    }

}