package com.example.myhosteldemo.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhosteldemo.fragments.com_my_Fragment;
import com.example.myhosteldemo.fragments.com_seeall_Fragment;

public class Complaint_ViewPager_Adapter extends FragmentStateAdapter {

    public Complaint_ViewPager_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0 :
                return new com_my_Fragment() ;
            case 1 :
                return new com_seeall_Fragment() ;
            default:
                return new com_my_Fragment() ;
        }
    }

    @Override
    public int getItemCount() {
        return 2 ;
    }
}
