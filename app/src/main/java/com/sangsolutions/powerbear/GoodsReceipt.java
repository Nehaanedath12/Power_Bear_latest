package com.sangsolutions.powerbear;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POList;
import com.sangsolutions.powerbear.Adapter.ViewPager2AdapterGoodsReceipt.ViewPager2AdapterGoodsReceipt;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.GoodReceiptHeader;
import com.sangsolutions.powerbear.Fragment.GoodsReceiptBodyFragment;
import com.sangsolutions.powerbear.Fragment.GoodsReceiptHeaderFragment;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsReceipt extends AppCompatActivity implements View.OnClickListener {
ViewPager2 viewPager2;
TabLayout tabLayout;
ViewPager2AdapterGoodsReceipt viewPager2AdapterGoodsReceipt;
ImageView img_close,img_delete,img_forward,img_backward,img_save,img_add_new;
DatabaseHelper helper;


private void Save(){
String POs="",date = "",supplier="",narration="",voucher="";
    List<POList> listPO=GoodsReceiptPoSingleton.getInstance().getList();
    List<GoodsReceiptBody> listMain = GoodsReceiptBodySingleton.getInstance().getList();


    List<String> list = new ArrayList<>();
    if(listPO!=null&&listPO.size()>0){
        for (int i = 0; i < listPO.size(); i++) {
            list.add(listPO.get(i).getDocNo());
        }
        POs = TextUtils.join(",",list);
    }
    date = PublicData.date;
    supplier = PublicData.supplier;
    narration = PublicData.narration;
    voucher = PublicData.voucher;


    //TODO complete insertion
    int body_success_counter =0;
    try {
        if (listMain != null && listMain.size() > 0 && PublicData.voucher != null && !PublicData.voucher.isEmpty()) {
            com.sangsolutions.powerbear.Database.GoodsReceiptBody gb = new com.sangsolutions.powerbear.Database.GoodsReceiptBody();

            for (int i = 0; i < listMain.size(); i++) {
                gb.setDocNo(PublicData.voucher);
                gb.setsPONo(listMain.get(i).getsPONo());
                gb.setiProduct(listMain.get(i).getiProduct());
                gb.setiWarehouse(listMain.get(i).getiWarehouse());
                gb.setBarcode(listMain.get(i).getBarcode());
                gb.setfPOQty(listMain.get(i).getfPOQty());
                gb.setfQty(listMain.get(i).getfQty());
                gb.setUnit(listMain.get(i).getUnit());
                gb.setsRemarks(listMain.get(i).getsRemarks());
                gb.setfMinorDamageQty(listMain.get(i).getfMinorDamageQty());
                gb.setsMinorRemarks(listMain.get(i).getsMinorRemarks());
                gb.setsMinorAttachment(listMain.get(i).getsMinorAttachment());
                gb.setfDamagedQty(listMain.get(i).getfDamagedQty());
                gb.setsDamagedRemarks(listMain.get(i).getsDamagedRemarks());
                gb.setsDamagedAttachment(listMain.get(i).getsDamagedAttachment());

                if(helper.InsertGoodsReceiptBody(gb)){
                    body_success_counter ++;
                }else Log.d("message","failed to insert");

                if(i+1==listMain.size()) {
                    if (body_success_counter == listMain.size()) {
                        try {

                            if (!date.isEmpty() && !POs.isEmpty() && !supplier.isEmpty()) {

                                GoodReceiptHeader gh = new GoodReceiptHeader();
                                gh.setDocNo(voucher);
                                gh.setDocDate(date);
                                gh.setsSupplier(supplier);
                                gh.setsPONo(POs);
                                gh.setsNarration(narration);
                                if (helper.InsertGoodsReceiptHeader(gh)) {
                                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                                    PublicData.clearData();
                                }else {
                                    Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show();
                                    helper.deleteGoodsBodyItem(voucher);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }else {
            Log.d("body","body is empty");
        }
    }catch (Exception e){
        e.printStackTrace();
    }






}


public void Alert(String title, String message, final String type){
    AlertDialog.Builder builder= new AlertDialog.Builder(this);
    builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(type.equals("save")) {
                        Save();
                    }
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
    viewPager2.setUserInputEnabled(false);
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
    protected void onDestroy() {
        super.onDestroy();
        GoodsReceiptPoSingleton.getInstance().clearList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_receipt);
        helper = new DatabaseHelper(this);

        viewPager2 = findViewById(R.id.viewpager);
        img_close = findViewById(R.id.close);
        img_delete = findViewById(R.id.delete);
        img_forward = findViewById(R.id.forward);
        img_backward = findViewById(R.id.backward);
        img_save = findViewById(R.id.save);
        img_add_new = findViewById(R.id.add_save);

        img_close.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        img_forward.setOnClickListener(this);
        img_backward.setOnClickListener(this);
        img_save.setOnClickListener(this);
        img_add_new.setOnClickListener(this);

        PublicData.voucher = "G-"+ DateFormat.format("ddMMyy-HHmmss", new Date());
        Log.d("body",PublicData.voucher);


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
    case R.id.add_save:
        Alert("New!","Do you want to add new?","new");
        break;
}
    }
}