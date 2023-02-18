package com.example.myhosteldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myhosteldemo.Adapter.AdminPanel_Viewpager_Adapter;
import com.example.myhosteldemo.Adapter.Complaint_ViewPager_Adapter;
import com.example.myhosteldemo.Utility.GlobalData;
import com.google.android.material.tabs.TabLayout;

public class AdminPanel extends AppCompatActivity {

    Toolbar toolbar ;
    TabLayout tabLayout ;
    ViewPager2 viewPager2 ;
    AdminPanel_Viewpager_Adapter adapter ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        toolbar = findViewById(R.id.admin_toolbar) ;
        tabLayout = findViewById(R.id.admin_tab_layout) ;
        viewPager2 = findViewById(R.id.admin_viewpager) ;
        adapter = new AdminPanel_Viewpager_Adapter(this) ;

        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setSmoothScrollingEnabled(true);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData.changeColorOfStatusBar(this , R.color.cyandark);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish() ;
        return super.onSupportNavigateUp();
    }
}
