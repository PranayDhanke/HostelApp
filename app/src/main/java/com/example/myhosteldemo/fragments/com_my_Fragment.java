package com.example.myhosteldemo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myhosteldemo.Add_Complaint;
import com.example.myhosteldemo.Complaints;
import com.example.myhosteldemo.R;

public class com_my_Fragment extends Fragment {

    Button add ;

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
        add.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Add_Complaint.class));
        });
    }
}