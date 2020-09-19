package com.sangsolutions.powerbear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountListAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;


import java.util.ArrayList;
import java.util.List;


public class StockCountList extends AppCompatActivity {
    ImageView add_new;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView title;
    StockCountListAdapter adapter;
    DatabaseHelper helper;
    ImageView img_home;
    List<com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList> list;

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    public void setRecyclerView(){
        list.clear();

        float net = 0;
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
                            list.remove(pos);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(StockCountList.this, "Deleted!", Toast.LENGTH_SHORT).show();
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

        add_new = findViewById(R.id.add_new);
        title = findViewById(R.id.title2);
        empty_frame = findViewById(R.id.empty_frame);

        helper = new DatabaseHelper(this);



        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        title.setText("Stock count");
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockCountList.this,Home.class));
                finishAffinity();
            }
        });
        list = new ArrayList<>();
        adapter = new StockCountListAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));



        setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
        startActivity(new Intent(StockCountList.this,StockCount.class));
           }
        });



        adapter.setOnClickListener(new StockCountListAdapter.OnClickListener() {
            @Override
            public void onMenuItemClick(View view, final com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList stockCountList, final int pos) {
                PopupMenu popupMenu = new PopupMenu(StockCountList.this, view);
                popupMenu.inflate(R.menu.edit_delete_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                      if (item.getItemId() == R.id.delete) {
                          DeleteStockCountItemAlert(stockCountList, pos);
                        } else if (item.getItemId() == R.id.edit) {
                            Intent intent1 = new Intent(StockCountList.this,StockCountWarehouse.class);
                             intent1.putExtra("warehouse",stockCountList.getWarehouseId());
                             intent1.putExtra("voucherNo",stockCountList.getVNo());
                             intent1.putExtra("EditMode", true);
                            startActivity(intent1);
                        }
                        return true;
                    }
                });
            }

            @Override
            public void onItemClick(View view, com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList stockCountList, int pos) {
                Intent intent1 = new Intent(StockCountList.this,StockCountWarehouse.class);
                intent1.putExtra("warehouse",stockCountList.getWarehouseId());
                intent1.putExtra("voucherNo",stockCountList.getVNo());
                intent1.putExtra("EditMode", false);
                startActivity(intent1);
            }
        });
    }}