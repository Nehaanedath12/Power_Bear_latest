package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;
import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList;
import com.sangsolutions.powerbear.Adapter.ViewPagerAdapter.ViewPagerAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.StockCount;
import com.sangsolutions.powerbear.Fragment.BodyFragment;
import com.sangsolutions.powerbear.Fragment.HeaderFragment;
import com.sangsolutions.powerbear.Singleton.StockCountProductSingleton;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockCountWarehouse extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String warehouse="",voucherNo="";
    String EditMode = "";
    DatabaseHelper helper;
    Toolbar toolbar;

    private     @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df;
    Date c;
    int current_position=0;

    private void Save(){
        String s_date="",s_narration="" ,s_warehouse ="";

        s_date= PublicData.date;

        s_narration=PublicData.narration;

        s_warehouse = PublicData.warehouse;

        List<ListProduct> list = StockCountProductSingleton.getInstance().getList();

        if(!s_date.isEmpty()&&!list.isEmpty())
        {
            String str_date,s_voucher_no,str_narration;
            if(EditMode.equals("new")){
                str_date = s_date;
                s_voucher_no = helper.GetNewVoucherNo();
                str_narration = s_narration;
            }else {
                str_date = s_date;
                s_voucher_no = voucherNo;
                str_narration = s_narration;
            }
            com.sangsolutions.powerbear.Database.StockCount s = new StockCount();
            if(EditMode.equals("edit")) {
                helper.DeleteStockCount(voucherNo);
            }
            for(int i = 0 ; i < list.size(); i ++){
                s.setiVoucherNo(s_voucher_no);
                s.setdDate(str_date);
                if(!s_warehouse.isEmpty()) {
                    s.setiWarehouse(s_warehouse);
                }else {
                    s.setiWarehouse(warehouse);
                }
                s.setiProduct(list.get(i).getiProduct());
                s.setfQty(list.get(i).getQty());
                s.setsUnit(list.get(i).getUnit());
                s.setsNarration(str_narration);
                s.setsRemarks(list.get(i).getsRemarks());
                s.setdProcessedDate(df.format(c));
                s.setiStatus("0");

                helper.InsertStockCount(s);


                if(list.size()==i+1){
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    StockCountProductSingleton.getInstance().clearList();
                    this.finish();
                }

            }

        }else {
            Toast.makeText(this, "some data is missing", Toast.LENGTH_SHORT).show();
        }

    }
    private void SaveAlert(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Save?")
                .setMessage("Do you want't to save?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Save();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
    private void CloseAlert() {
        if (EditMode.equals("edit")||EditMode.equals("new")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit?")
                    .setMessage("Do you want't to exit without saving?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StockCountProductSingleton.getInstance().clearList();
                            finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

    }
    private void DeleteStockCountItemAlert(final String voucherNo) {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(helper.DeleteStockCount(voucherNo)) {
                            Toast.makeText(StockCountWarehouse.this, "Deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onBackPressed() {
       CloseAlert();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count_warehouse);
        final ImageView img_save = findViewById(R.id.save);
        ImageView img_forward = findViewById(R.id.forward);
        ImageView img_backward = findViewById(R.id.backward);
        ImageView img_delete = findViewById(R.id.delete);
        ImageView img_close = findViewById(R.id.close);
        ImageView img_new = findViewById(R.id.add_new);
        toolbar = findViewById(R.id.toolbar);
        helper = new DatabaseHelper(this);
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy");
        Intent intent =  getIntent();
        if(intent!=null) {

            EditMode = intent.getStringExtra("EditMode");

            assert EditMode != null;
            if(EditMode.equals("edit")){
                warehouse = intent.getStringExtra("warehouse");
                voucherNo = intent.getStringExtra("voucherNo");
            }else {
                warehouse = "";
                voucherNo = "";
                img_delete.setVisibility(View.GONE);
            }

        }
        tabLayout = findViewById(R.id.tabLay);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        tabLayout.setupWithViewPager(viewPager);

        setUpViewPager(viewPager);

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAlert();
            }
        });

        img_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warehouse = "";
                voucherNo = "";
                EditMode = "new";
                if(img_save.getVisibility()==View.GONE || img_save.getVisibility()==View.INVISIBLE){
                    img_save.setVisibility(View.VISIBLE);
                }
                PublicData.clearData();
                StockCountProductSingleton.getInstance().clearList();
              setUpViewPager(viewPager);
            }
        });

        img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EditMode.equals("new")){
                    List<StockCountList> list = StockCountSingleton.getInstance().getList();
                    if (list.size() > 1) {
                        if (current_position < list.size()) {
                            warehouse = list.get(current_position).getWarehouseId();
                            voucherNo = list.get(current_position).getVNo();
                            PublicData.clearData();
                            setUpViewPager(viewPager);
                            current_position++;
                        }
                    }
                }
            }
        });

        img_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EditMode.equals("new")) {
                    List<StockCountList> list = StockCountSingleton.getInstance().getList();
                    if (list.size() > 1) {
                        if (current_position > 0) {
                            current_position--;
                            warehouse = list.get(current_position).getWarehouseId();
                            voucherNo = list.get(current_position).getVNo();
                            PublicData.clearData();
                            setUpViewPager(viewPager);

                        }

                    }
                }
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAlert();
            }
        });

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteStockCountItemAlert(voucherNo);
            }
        });


    }


    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(new HeaderFragment(), "Header",warehouse,EditMode,voucherNo);
        adapter.add(new BodyFragment(), "Body",warehouse,EditMode,voucherNo);
        viewPager.setAdapter(adapter);
    }
}
