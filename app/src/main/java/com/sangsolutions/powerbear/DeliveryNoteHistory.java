package com.sangsolutions.powerbear;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistoryAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteHistorySingleton;

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

        Cursor cursor = helper.GetAllDeliveryNote();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String DocDate,TotalQty,iVoucherNo;

               DocDate = cursor.getString(cursor.getColumnIndex("DocDate"));
               TotalQty = cursor.getString(cursor.getColumnIndex("sumQty"));
                iVoucherNo = cursor.getString(cursor.getColumnIndex("DocNo"));

                list.add(new com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory(DocDate,TotalQty,iVoucherNo));
                cursor.moveToNext();

                if (i + 1 == cursor.getCount()) {
                    rv.setAdapter(adapter);
                    DeliveryNoteHistorySingleton.getInstance().setList(list);
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
                        try {
                            if(helper.DeleteDeliveryNoteBodyItem(deliveryNoteHistory.getiVoucherNo())) {
                                if (helper.DeleteDeliveryHeaderItem(deliveryNoteHistory.getiVoucherNo())) {
                                    list.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    setRecyclerView();
                                    Toast.makeText(DeliveryNoteHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
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


        date.setText("Delivery Note");
        list = new ArrayList<>();
        adapter = new DeliveryNoteHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));


        setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryNoteHistory.this, AddDeliveryNote.class));
            }
        });

adapter.setOnClickListener(new DeliveryNoteHistoryAdapter.OnClickListener() {
    @Override
    public void onEditItemClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int position) {
        Intent intent1 = new Intent(DeliveryNoteHistory.this, AddDeliveryNote.class);
        intent1.putExtra("DocNo",deliveryNoteHistory.getiVoucherNo());
        intent1.putExtra("EditMode", true);
        intent1.putExtra("Position",position);
        startActivity(intent1);
    }

    @Override
    public void onDeleteItemClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int pos) {
        DeleteStockCountItemAlert(deliveryNoteHistory, pos);
    }
});

    }
}