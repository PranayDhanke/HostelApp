package com.example.myhosteldemo.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhosteldemo.fragments.AdminLoginFragment;
import com.example.myhosteldemo.fragments.AdminWorkSpaceFragment;
import com.example.myhosteldemo.fragments.com_my_Fragment;
import com.example.myhosteldemo.fragments.com_seeall_Fragment;

public class AdminPanel_Viewpager_Adapter extends FragmentStateAdapter {

    public AdminPanel_Viewpager_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0 :
                return new AdminLoginFragment();
            case 1 :
                return new AdminWorkSpaceFragment();
            default:
                return new AdminLoginFragment() ;
        }
    }

    @Override
    public int getItemCount() {
        return 2 ;
    }
}
