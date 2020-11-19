package com.sangsolutions.powerbear;


import android.content.DialogInterface;
import android.content.Intent;
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

import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory;
import com.sangsolutions.powerbear.Adapter.ViewPager2AdapterGoodsReceipt.ViewPager2AdapterGoodsReceipt;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.DeliveryNoteBody;
import com.sangsolutions.powerbear.Database.DeliveryNoteHeader;
import com.sangsolutions.powerbear.Fragment.DeliveryNoteBodyFragment;
import com.sangsolutions.powerbear.Fragment.DeliveryNoteHeaderFragment;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteBodySingleton;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteHistorySingleton;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteSOSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SuppressWarnings("ALL")
public class AddDeliveryNote extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    ViewPager2AdapterGoodsReceipt viewPager2AdapterGoodsReceipt;
    ImageView img_close,img_delete,img_forward,img_backward,img_save,img_add_new;
    DatabaseHelper helper;
    boolean EditMode = false;
    String DocNo = "";
    int current_position = 0;

    private void Save(){
        String SOs="",date = "",supplier="",narration="",voucher="" ,ProcessedDate ="";
        List<String> listSO= DeliveryNoteSOSingleton.getInstance().getList();
        List<com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody> listMain = DeliveryNoteBodySingleton.getInstance().getList();

        for(int i = 0 ;i<listMain.size();i++){
            if(!listSO.contains(listMain.get(i).getsSONo())){
                Toast.makeText(this, "SO numbers don't match! check header an body.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if(listSO!=null&&listSO.size()>0&&listMain!=null&&listMain.size()>0) {


            List<String> list = new ArrayList<>(listSO);
            SOs = TextUtils.join(",", list);

            date = PublicData.date;
            supplier = PublicData.supplier;
            narration = PublicData.narration;
            voucher = PublicData.voucher;


            ProcessedDate = String.valueOf(DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date()));
            if (EditMode) {
                List<String> listProduct = new ArrayList<>();
                List<String> listPo = new ArrayList<>();
                listProduct.clear();
                listPo.clear();

                for (int i = 0; i < listMain.size(); i++) {
                    listProduct.add(listMain.get(i).getiProduct());
                    listPo.add(listMain.get(i).getsSONo());
                }
                try {
                    helper.DeleteBodyItems(listProduct, listPo, DocNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int body_success_counter = 0;
            try {
                if (listMain.size() > 0 && PublicData.voucher != null && !PublicData.voucher.isEmpty()) {
                    DeliveryNoteBody b = new DeliveryNoteBody();
                    helper.DeleteDeliveryNoteBodyItem(voucher);
                    for (int i = 0; i < listMain.size(); i++) {
                        b.setsVoucherNo(voucher);
                        b.setsSONo(listMain.get(i).getsSONo());
                        b.setfQty(listMain.get(i).getfQty());
                        b.setiProduct(listMain.get(i).getiProduct());
                        b.setiWarehouse(listMain.get(i).getiWarehouse());
                        b.setsAttachment(listMain.get(i).getsAttachment());
                        b.setsRemarks(listMain.get(i).getsRemarks());
                        b.setiUser(helper.GetUserId());
                        b.setsSOQty(listMain.get(i).getfSOQty());
                        b.setUnit(listMain.get(i).getUnit());

                        if (helper.InsertDeliverNoteBody(b)) {
                            body_success_counter++;
                        } else Log.d("message", "failed to insert");

                        if (i + 1 == listMain.size()) {
                            if (body_success_counter == listMain.size()) {
                                try {
                                    if (!date.isEmpty() && !SOs.isEmpty() && !supplier.isEmpty()) {

                                        DeliveryNoteHeader dh = new DeliveryNoteHeader(
                                                 voucher
                                                ,date
                                                ,ProcessedDate
                                                ,helper.GetUserId()
                                                ,SOs
                                                ,supplier
                                                ,narration);

                                        if (helper.InsertDeliverNoteHeader(dh)) {
                                            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                                            PublicData.clearData();
                                            DeliveryNoteBodySingleton.getInstance().clearList();
                                            DeliveryNoteSOSingleton.getInstance().clearList();
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show();
                                            helper.DeleteDeliveryNoteBodyItem(voucher);
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


    @Override
    public void onBackPressed() {
        Alert("Close!","Do you want to close before saving?","close");
    }

    public void Alert(String title, String message, final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("save")) {

                            List<com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody> listMain = DeliveryNoteBodySingleton.getInstance().getList();
                            if (listMain != null && listMain.size() > 0) {

                                for (int i = 0; i <listMain.size();i++) {
                                    if(listMain.get(i).getfQty().equals("")) {
                                        Toast.makeText(AddDeliveryNote.this,"Enter all qty or remove empty items!",Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    if(listMain.size()==i+1) {
                                        Save();
                                    }
                                }
                            }
                        }else if(type.equals("new")){
                            DeliveryNoteBodySingleton.getInstance().clearList();
                            DeliveryNoteSOSingleton.getInstance().clearList();
                            PublicData.voucher = "D-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                            EditMode = false;
                            SetViewPager(PublicData.voucher,false);
                        }else if(type.equals("close")){
                            DeliveryNoteBodySingleton.getInstance().clearList();
                            DeliveryNoteSOSingleton.getInstance().clearList();
                            PublicData.clearData();
                            finish();
                        }else if(type.equals("delete")){
                            try {
                                if(helper.DeleteDeliveryNoteBodyItem(DocNo)) {
                                    if (helper.DeleteGoodsHeaderItem(DocNo)) {
                                        Toast.makeText(AddDeliveryNote.this, "Deleted!", Toast.LENGTH_SHORT).show();
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
                })
                .create().show();
    }


    public void SetViewPager(String DocNo,boolean EditMode){
        List<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        PublicData.voucher = DocNo;
        bundle.putString("DocNo",DocNo);
        bundle.putBoolean("EditMode",EditMode);

        DeliveryNoteBodyFragment bodyFragment = new DeliveryNoteBodyFragment();
        bodyFragment.setArguments(bundle);

        DeliveryNoteHeaderFragment headerFragment = new DeliveryNoteHeaderFragment();
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
        DeliveryNoteBodySingleton.getInstance().clearList();
        DeliveryNoteSOSingleton.getInstance().clearList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_note);
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
                PublicData.voucher = intent.getStringExtra("DocNo");
                DocNo = PublicData.voucher;
                current_position=intent.getIntExtra("Position",0);
            }else {
                PublicData.voucher = "D-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                DocNo = PublicData.voucher;
            }
        }else {
            PublicData.voucher = "D-" + DateFormat.format("ddMMyy-HHmmss", new Date());
            DocNo = PublicData.voucher;
        }

        tabLayout = findViewById(R.id.tabLay);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#e58989"), Color.parseColor("#ffffff"));
        SetViewPager(DocNo,EditMode);
    }

    List<DeliveryNoteHistory> listHistory = DeliveryNoteHistorySingleton.getInstance().getList();


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
                    if (current_position < listHistory.size()) {
                        SetViewPager(listHistory.get(current_position).getiVoucherNo(),true);
                        current_position++;
                    }
                }
                break;
            case R.id.backward:
                if (listHistory.size() > 1) {
                    if (current_position > 0) {
                        current_position--;
                        SetViewPager(listHistory.get(current_position).getiVoucherNo(),true);
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
