package com.sangsolutions.powerbear;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sangsolutions.powerbear.Adapter.ViewPager2AdapterGoodsReceipt.ViewPager2AdapterGoodsReceipt;
import com.sangsolutions.powerbear.Fragment.GoodsReceiptBodyFragment;
import com.sangsolutions.powerbear.Fragment.GoodsReceiptHeaderFragment;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceipt extends AppCompatActivity implements View.OnClickListener {
ViewPager2 viewPager2;
TabLayout tabLayout;
ViewPager2AdapterGoodsReceipt viewPager2AdapterGoodsReceipt;
ImageView img_close,img_delete,img_forward,img_backward,img_save,img_add_new;


public void Alert(String title,String message,String type){
    AlertDialog.Builder builder= new AlertDialog.Builder(this);
    builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create().show();
}


public void SetViewPager(){
    List<Fragment> fragmentList = new ArrayList<>();
    fragmentList.add(new GoodsReceiptHeaderFragment());
    fragmentList.add(new GoodsReceiptBodyFragment());
    viewPager2AdapterGoodsReceipt = new ViewPager2AdapterGoodsReceipt(getSupportFragmentManager(),getLifecycle(),fragmentList);
    viewPager2.setAdapter(viewPager2AdapterGoodsReceipt);

    new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
         viewPager2.setCurrentItem(tab.getPosition(),true);
         if(position==0){
             tab.setText("Header");
         }else if(position==1){
             tab.setText("Body");
         }
        }
    }).attach();
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_receipt);
        viewPager2 = findViewById(R.id.viewpager);
        img_close = findViewById(R.id.close);
        img_delete = findViewById(R.id.delete);
        img_forward = findViewById(R.id.forward);
        img_backward = findViewById(R.id.backward);
        img_save = findViewById(R.id.save);
        img_add_new = findViewById(R.id.add_new);

        img_close.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        img_forward.setOnClickListener(this);
        img_backward.setOnClickListener(this);
        img_save.setOnClickListener(this);
        img_add_new.setOnClickListener(this);

        tabLayout = findViewById(R.id.tabLay);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#e58989"), Color.parseColor("#ffffff"));
        SetViewPager();
        }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.close:
        finish();
        break;
    case R.id.delete:
        Alert("Delete!","Do you want to delete?","delete");
        break;
    case R.id.forward:
        break;
    case R.id.backward:
        break;
    case R.id.save:
        Alert("Save!","Do you want to save the items?","save");
        break;
    case R.id.add_new:
        Alert("New!","Do you want to add new?","new");
        break;
}
    }
}