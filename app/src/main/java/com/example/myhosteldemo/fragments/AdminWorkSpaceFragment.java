package com.example.myhosteldemo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.AdminModel;
import com.example.myhosteldemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminWorkSpaceFragment extends Fragment {

    EditText email , password ;
    TextView add ;
    TextView nodata ;
    CardView card ;

    DatabaseReference database ;
    FirebaseFirestore firestore ;

    static ProgressDialog dialog ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance() ;
        dialog = new ProgressDialog(getActivity()) ;
        dialog.setTitle("adding Admin");
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.information) ;
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
        add = view.findViewById(R.id.admin_work_add) ;
        nodata = view.findViewById(R.id.admin_work_nodata) ;
        card = view.findViewById(R.id.admin_work_card) ;

        add.setOnClickListener(v -> {
            dialog.show();
            uploadData() ;
        });

        if(!GlobalData.isAdmin){
            nodata.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
        }
        else{
            nodata.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        if(!GlobalData.isAdmin){
            nodata.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
        }
        else{
            nodata.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
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

}