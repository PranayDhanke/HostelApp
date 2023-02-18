package com.example.myhosteldemo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.AdminModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminLoginFragment extends Fragment {

    EditText email , password ;
    CheckBox remember ;
    TextView login;

    FirebaseFirestore firestore ;

    static ProgressDialog dialog ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance() ;
        dialog = new ProgressDialog(getActivity()) ;
        dialog.setTitle("Checking");
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.information) ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.admin_login_email) ;
        password = view.findViewById(R.id.admin_login_password) ;
        login = view.findViewById(R.id.admin_login_login) ;
        remember = view.findViewById(R.id.admin_login_remember) ;

        email.setText(GlobalData.user.getEmail());

        login.setOnClickListener(v -> {
            dialog.show();
            checkIfAdmin() ;
        });
    }

    private void checkIfAdmin(){
        if(isValidate()){
           admin() ;
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

    private void admin(){
        firestore.collection("Admins")
                .whereEqualTo("admin.email" , email.getText().toString().trim())
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "Failure"
                                    ,"Failed to login as admin , try again",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                        else if(value.isEmpty()){
                            dialog.dismiss();
                            AlertUtil.showAlertDialog(getActivity(),
                                    "No such user exist"
                                    ,"Failed to login as admin",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                        }
                        else if(value.getDocuments().size() > 0){
                            //Toast.makeText(getActivity(), "Admin exist " + value.getDocuments().size() , Toast.LENGTH_SHORT).show();
                            AdminModel model = value.getDocuments().get(0).toObject(AdminModel.class) ;
                            if(model.getPassword().equals(password.getText().toString().trim())){
                                dialog.dismiss();
                                AlertUtil.showAlertDialog(getActivity(),
                                        "Successfull"
                                        ,"Succeessfull logged in as admin",
                                        true,
                                        R.drawable.information,
                                        "Ok");
                                GlobalData.isAdmin = true ;
                            }
                            else {
                                dialog.dismiss();
                                AlertUtil.showAlertDialog(getActivity(),
                                        "Failure"
                                        ,"Password won't match",
                                        true,
                                        R.drawable.error,
                                        "Ok");
                            }
                        }
                    }
                }) ;
    }
}