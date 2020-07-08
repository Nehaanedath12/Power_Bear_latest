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

import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistoryAdapter;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteHistory extends AppCompatActivity {
    ImageView add_new,img_home;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView date;
    DeliveryNoteHistoryAdapter adapter;
    DatabaseHelper helper;
    List<com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory> list;

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    public void setRecyclerView(){
        list.clear();

        float net = 0;
        Cursor cursor = helper.GetDeliveryNoteList();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String Header_id,TotalQty,iVoucherNo;

               Header_id = cursor.getString(cursor.getColumnIndex("HeaderId"));
               TotalQty = cursor.getString(cursor.getColumnIndex("Qty"));
                iVoucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));

                list.add(new com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory(Header_id,TotalQty,iVoucherNo));
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

    private void DeleteStockCountItemAlert(final com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(helper.DeleteDeliveryNoteHeaderIdAndVoucherNO(deliveryNoteHistory.getHeaderId(),deliveryNoteHistory.getiVoucherNo())) {
                            list.remove(pos);
                            adapter.notifyDataSetChanged();
                            setRecyclerView();
                            Toast.makeText(DeliveryNoteHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
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
        date = findViewById(R.id.title);
        empty_frame = findViewById(R.id.empty_frame);
        img_home = findViewById(R.id.home);
        helper = new DatabaseHelper(this);

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeliveryNoteHistory.this,Home.class));
                finishAffinity();
            }
        });

     /*   date.setText("Delivery Note    "+new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));*/
        date.setText("Delivery Note");
        list = new ArrayList<>();
        adapter = new DeliveryNoteHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));



        setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryNoteHistory.this, SelectCustomer.class));
            }
        });



        adapter.setOnClickListener(new DeliveryNoteHistoryAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, final com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, final int pos) {
                PopupMenu popupMenu = new PopupMenu(DeliveryNoteHistory.this, view);
                popupMenu.inflate(R.menu.edit_delete_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            DeleteStockCountItemAlert(deliveryNoteHistory, pos);
                        } else if (item.getItemId() == R.id.edit) {
                            Intent intent1 = new Intent(DeliveryNoteHistory.this, AddDeliveryNote.class);
                            intent1.putExtra("HeaderId",deliveryNoteHistory.getHeaderId());
                            intent1.putExtra("iVoucherNo",deliveryNoteHistory.getiVoucherNo());
                            intent1.putExtra("EditMode", true);
                            startActivity(intent1);
                        }
                        return true;
                    }
                });
            }
        });
    }
}