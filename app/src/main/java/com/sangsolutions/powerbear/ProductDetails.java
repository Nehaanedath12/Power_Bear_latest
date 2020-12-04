package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sangsolutions.powerbear.Adapter.ProductDetailsViewPagerAdapter.ProductDetailsViewPagerAdapter;

public class ProductDetails extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String iProduct;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.viewPager);
        iProduct= getIntent().getStringExtra("iProduct");

        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("Spare Parts"));
        tabLayout.addTab(tabLayout.newTab().setText("Attachments"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#e58989"), Color.parseColor("#ffffff"));

        viewPager.beginFakeDrag();
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        settingViewPager();


    }

    private void settingViewPager() {
        final ProductDetailsViewPagerAdapter adapter = new ProductDetailsViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this, tabLayout.getTabCount(),iProduct);
        viewPager.setAdapter(adapter);

    }

}
