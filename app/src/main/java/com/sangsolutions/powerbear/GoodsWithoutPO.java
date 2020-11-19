package com.sangsolutions.powerbear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory;
import com.sangsolutions.powerbear.Adapter.ViewPager2AdapterGoodsReceipt.ViewPager2AdapterGoodsReceipt;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.GoodReceiptHeader;
import com.sangsolutions.powerbear.Fragment.GoodsWithoutPOBodyFragment;
import com.sangsolutions.powerbear.Fragment.GoodsWithoutPOHeaderFragment;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptHistorySingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsWithoutPO extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    ViewPager2AdapterGoodsReceipt viewPager2AdapterGoodsReceipt;
    ImageView img_close,img_delete,img_forward,img_backward,img_save,img_add_new;
    DatabaseHelper helper;

    boolean EditMode = false;
    String DocNo = "";
    int current_position = 0;
    List<GoodsReceiptHistory> listHistory;
    private void Save(){
        String refno="",date = "",supplier="",narration="",voucher="" ,ProcessedDate ="";
        List<GoodsReceiptBody> listMain = GoodsReceiptBodySingleton.getInstance().getList();




        if(listMain!=null&&listMain.size()>0) {

            date = PublicData.date;
            supplier = PublicData.supplier;
            narration = PublicData.narration;
            voucher = PublicData.voucher;
            refno = PublicData.POs;// this case refno in po

            if(refno.equals("")){
                Toast.makeText(this, "Enter Reference No!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(date.equals("")||supplier.equals("")||voucher.equals("")){
                Toast.makeText(this, "Values missing in the header part!", Toast.LENGTH_SHORT).show();
                return;
            }

            ProcessedDate = String.valueOf(DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date()));
            int body_success_counter = 0;
            try {
                if (listMain.size() > 0 && PublicData.voucher != null && !PublicData.voucher.isEmpty()) {
                    com.sangsolutions.powerbear.Database.GoodsReceiptBody gb = new com.sangsolutions.powerbear.Database.GoodsReceiptBody();
                    helper.DeleteGoodsWithoutPOBodyItem(voucher);
                    for (int i = 0; i < listMain.size(); i++) {
                        gb.setDocNo(voucher);
                        gb.setiProduct(listMain.get(i).getiProduct());
                        gb.setiWarehouse(listMain.get(i).getiWarehouse());
                        gb.setBarcode(listMain.get(i).getBarcode());
                        gb.setfQty(listMain.get(i).getfQty());
                        gb.setUnit(listMain.get(i).getUnit());
                        gb.setsRemarks(listMain.get(i).getsRemarks());
                        gb.setiUser(helper.GetUserId());
                        gb.setfMinorDamageQty(listMain.get(i).getfMinorDamageQty());
                        gb.setsMinorRemarks(listMain.get(i).getsMinorRemarks());
                        gb.setsMinorAttachment(listMain.get(i).getsMinorAttachment());
                        gb.setfDamagedQty(listMain.get(i).getfDamagedQty());
                        gb.setsDamagedRemarks(listMain.get(i).getsDamagedRemarks());
                        gb.setsDamagedAttachment(listMain.get(i).getsDamagedAttachment());
                        gb.setiMinorType(listMain.get(i).getRemarksMinorType());
                        gb.setiDamageType(listMain.get(i).getRemarksDamagedType());

                        if (helper.InsertGoodsWithoutPOBody(gb,EditMode)) {
                            body_success_counter++;
                        } else Log.d("message", "failed to insert");

                        if (i + 1 == listMain.size()) {
                            if (body_success_counter == listMain.size()) {
                                try {

                                    if (!date.isEmpty() && !refno.isEmpty()) {

                                        GoodReceiptHeader gh = new GoodReceiptHeader(voucher
                                                ,date
                                                ,ProcessedDate
                                                ,supplier
                                                ,helper.GetUserId()
                                                ,refno,narration);

                                        if (helper.InsertGoodsWithoutPOHeader(gh,EditMode)) {
                                            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                                            PublicData.clearData();
                                            GoodsReceiptBodySingleton.getInstance().clearList();
                                            this.finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show();
                                           if(!EditMode)
                                            helper.DeleteGoodsWithoutPOBodyItem(voucher);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                } else {
                    Log.d("body", "body is empty");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else {
            Toast.makeText(this, "No data to save", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        Alert("Close!","Do you want to close before saving?","close");
    }


    public void Alert(String title, String message, final String type){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("save")) {

                            List<GoodsReceiptBody> listMain = GoodsReceiptBodySingleton.getInstance().getList();
                            if (listMain != null && listMain.size() > 0) {

                                for (int i = 0; i <listMain.size();i++) {
                                    if(listMain.get(i).getfQty().equals("")&&listMain.get(i).getfMinorDamageQty().equals("")&&listMain.get(i).getfDamagedQty().equals("")) {
                                        Toast.makeText(GoodsWithoutPO.this,"Enter all qty or remove empty items!",Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    if(listMain.size()==i+1) {
                                        Save();
                                    }
                                }
                            }
                        }else if(type.equals("new")){
                            GoodsReceiptBodySingleton.getInstance().clearList();
                            PublicData.voucher = "G1-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                            SetViewPager(PublicData.voucher,false);
                            EditMode = false;
                        }else if(type.equals("close")){
                            GoodsReceiptBodySingleton.getInstance().clearList();
                            PublicData.clearData();
                            finish();
                        }else if(type.equals("delete")){
                            try {
                                if(helper.DeleteGoodsWithoutPOBodyItem(DocNo)) {
                                    if (helper.DeleteGoodsWithoutPOHeaderItem(DocNo)) {
                                        Toast.makeText(GoodsWithoutPO.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void SetViewPager(String DocNo,boolean EditMode){
        List<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        PublicData.voucher = DocNo;
        bundle.putString("DocNo",DocNo);
        bundle.putBoolean("EditMode",EditMode);

        GoodsWithoutPOBodyFragment bodyFragment = new GoodsWithoutPOBodyFragment();
        bodyFragment.setArguments(bundle);

        GoodsWithoutPOHeaderFragment headerFragment = new GoodsWithoutPOHeaderFragment();
        headerFragment.setArguments(bundle);

        fragmentList.add(headerFragment);
        fragmentList.add(bodyFragment);
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
        GoodsReceiptBodySingleton.getInstance().clearList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_without_po);

        helper = new DatabaseHelper(this);

        viewPager2 = findViewById(R.id.viewpager);
        img_close = findViewById(R.id.goodsReceipt);
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
        Intent intent = getIntent();

        if(intent!=null) {
            EditMode =intent.getBooleanExtra("EditMode",false);
            if(EditMode){
                PublicData.voucher= intent.getStringExtra("DocNo");
                current_position=intent.getIntExtra("Position",0);
            }else {
                PublicData.voucher = "G1-" + DateFormat.format("ddMMyy-HHmmss", new Date());
            }
            DocNo = PublicData.voucher;
        }else {
            PublicData.voucher = "G1-" + DateFormat.format("ddMMyy-HHmmss", new Date());
            DocNo = PublicData.voucher;
        }

        tabLayout = findViewById(R.id.tabLay);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#e58989"), Color.parseColor("#ffffff"));
        SetViewPager(DocNo,EditMode);

        listHistory = GoodsReceiptHistorySingleton.getInstance().getList();
//        current_position = listHistory.indexOf(DocNo);

    }






    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goodsReceipt:
                Alert("Close!","Do you want to close before saving?","close");
                break;
            case R.id.delete:
                if(EditMode)
                    Alert("Delete!","Do you want to delete?","delete");
                break;
            case R.id.forward:
                if (listHistory.size() > 1) {
                    if (current_position+1 < listHistory.size()) {
                        current_position++;
                        SetViewPager(listHistory.get(current_position).getDocNo(),true);
                    }
                }
                break;
            case R.id.backward:
                if (listHistory.size() > 1) {
                    if (current_position > 0 &&  current_position<listHistory.size()) {
                        current_position--;
                        SetViewPager(listHistory.get(current_position).getDocNo(),true);
                    }
                }
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