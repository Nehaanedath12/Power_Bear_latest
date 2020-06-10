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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryNoteHistory extends AppCompatActivity {
    ImageView add_new;
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
                String Header_id,TotalQty;

               Header_id = cursor.getString(cursor.getColumnIndex("HeaderId"));
               TotalQty = cursor.getString(cursor.getColumnIndex("Qty"));

                list.add(new com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory(Header_id,TotalQty));
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
                        if(helper.DeleteDeliveryNote(deliveryNoteHistory.getHeaderId())) {
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
        date = findViewById(R.id.date);
        empty_frame = findViewById(R.id.empty_frame);

        helper = new DatabaseHelper(this);


        date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        list = new ArrayList<>();
        adapter = new DeliveryNoteHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));



        setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryNoteHistory.this,SelectDN.class));
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
                            Intent intent1 = new Intent(DeliveryNoteHistory.this,AddProduct.class);
                            intent1.putExtra("DocNo",deliveryNoteHistory.getHeaderId());
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