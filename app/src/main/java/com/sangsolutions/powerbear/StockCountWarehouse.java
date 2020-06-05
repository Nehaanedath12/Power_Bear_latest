package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sangsolutions.powerbear.Adapter.ViewPagerAdapter.ViewPagerAdapter;
import com.sangsolutions.powerbear.Fragment.BodyFragment;
import com.sangsolutions.powerbear.Fragment.HeaderFragment;

public class StockCountWarehouse extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String warehouse="",voucherNo="";
    boolean EditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count_warehouse);
        Intent intent =  getIntent();
        if(intent!=null) {
            warehouse = intent.getStringExtra("warehouse");
            EditMode = intent.getBooleanExtra("EditMode", false);
            voucherNo = intent.getStringExtra("voucherNo");
        }
        tabLayout = findViewById(R.id.tabLay);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        tabLayout.setupWithViewPager(viewPager);

       if(!warehouse.equals("")) {setUpViewPager(viewPager);}else {
           Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
           finish();
       }


    }


    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(new HeaderFragment(), "Header",warehouse,EditMode,voucherNo);
        adapter.add(new BodyFragment(), "Body",warehouse,EditMode,voucherNo);
        viewPager.setAdapter(adapter);
    }
}
