package com.sangsolutions.powerbear;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.AppBarLayout;
import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountListAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;

import java.util.ArrayList;
import java.util.List;


public class StockCountHistory extends AppCompatActivity {
    ImageView add_new;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView title,title_selection;
    StockCountListAdapter adapter;
    DatabaseHelper helper;
    ImageView img_home;
    ImageView close,delete;
    AppBarLayout appbar;
    Toolbar toolbar;
    List<com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList> list;


    private boolean selection_active = false ;



    public void DeleteItems(){
       List<Integer> listSelectedItem = adapter.getSelectedItems();
        for(int i = 0 ; i<listSelectedItem.size();i++) {
            for(int j = 0 ;j<list.size();j++) {
                if(listSelectedItem.get(i)==j)
                  if (helper.DeleteStockCount(list.get(j).getVNo())) {
                        Log.d("StockCount", "deleted!");
                    }
            }
            if(i+1 == listSelectedItem.size()){
                setRecyclerView();
                closeSelection();
            }
        }

    }



    public void deleteAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete "+adapter.getSelectedItemCount()+" items?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        DeleteItems();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create()
                .show();
    }

    private void initToolbar() {
        appbar = findViewById(R.id.appbar);
        close = findViewById(R.id.goodsReceipt);
        delete = findViewById(R.id.delete);
        toolbar = findViewById(R.id.toolbar);
        title_selection = findViewById(R.id.title_selection);
        setSupportActionBar(toolbar);
        close.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        appbar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    public void closeSelection(){
        adapter.clearSelections();
        appbar.setVisibility(View.GONE);
        selection_active = false;

    }

    private void toggleSelection(int position) {
        appbar.setVisibility(View.VISIBLE);
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if(count==0){
            closeSelection();
        }
        title_selection.setText("Selected "+count+" item's");
        close.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    public void setRecyclerView(){
        list.clear();
        Cursor cursor = helper.GetStockCountList();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String Warehouse, Date, TotalQty, VoucherNo,warehouseId;

                Warehouse = helper.GetWarehouse(cursor.getString(cursor.getColumnIndex("iWarehouse")));
                Date = cursor.getString(cursor.getColumnIndex("dDate"));
                VoucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                TotalQty = cursor.getString(cursor.getColumnIndex("SumQty"));
                warehouseId = cursor.getString(cursor.getColumnIndex("iWarehouse"));

                list.add(new com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList(VoucherNo,Date,TotalQty,Warehouse,warehouseId));
                cursor.moveToNext();

                if (i + 1 == cursor.getCount()) {
                    rv.setAdapter(adapter);
                    StockCountSingleton.getInstance().setList(list);
                    cursor.close();
                }
            }
        }else {
            empty_frame.setVisibility(View.VISIBLE);
            rv.setAdapter(adapter);
        }
    }

    private void DeleteStockCountItemAlert(final com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList stockCountList, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(helper.DeleteStockCount(stockCountList.getVNo())) {
                            closeSelection();
                            setRecyclerView();
                            StockCountSingleton.getInstance().setList(list);
                            Toast.makeText(StockCountHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count_list);

        add_new = findViewById(R.id.add_save);
        title = findViewById(R.id.date);
        empty_frame = findViewById(R.id.empty_frame);
        initToolbar();
        helper = new DatabaseHelper(this);



        img_home = findViewById(R.id.home);
        title= findViewById(R.id.title);
        title.setText("Stock count");
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockCountHistory.this,Home.class));
                finishAffinity();
            }
        });
        list = new ArrayList<>();
        adapter = new StockCountListAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(null);



        add_new.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(StockCountHistory.this,StockCountWarehouse.class);
             // new for new entry
             intent.putExtra("EditMode", "new");
             startActivity(intent);
           }
        });



        adapter.setOnClickListener(new StockCountListAdapter.OnClickListener() {
            @Override
            public void onDeleteItemClick(final com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList stockCountList, final int pos) {
                DeleteStockCountItemAlert(stockCountList, pos);
            }

            @Override
            public void onItemClick(com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList stockCountList, int pos) {

              if(!selection_active) {
                  Intent intent1 = new Intent(StockCountHistory.this, StockCountWarehouse.class);
                  intent1.putExtra("warehouse", stockCountList.getWarehouseId());
                  intent1.putExtra("voucherNo", stockCountList.getVNo());
                  intent1.putExtra("EditMode", "edit");
                  startActivity(intent1);
              }else {
                  enableActionMode(pos);
              }
            }

            @Override
            public void onItemLongClick(int pos) {
                enableActionMode(pos);
                selection_active = true;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });
    }}