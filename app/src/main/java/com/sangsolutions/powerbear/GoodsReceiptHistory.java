package com.sangsolutions.powerbear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistoryAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptHistory extends AppCompatActivity {
    ImageView add_new;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView date;
    GoodsReceiptHistoryAdapter adapter;
    DatabaseHelper helper;
    ImageView img_home;

    List<com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory> list;

    @Override
    protected void onResume() {
        super.onResume();
        //setRecyclerView();
    }
/*
    public void setRecyclerView(){
        list.clear();

        Cursor cursor = helper.GetAllGoodsReceipt();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String Header_id,TotalQty,iVoucherNo;

                Header_id = cursor.getString(cursor.getColumnIndex("HeaderId"));
                TotalQty = cursor.getString(cursor.getColumnIndex("Qty"));
                iVoucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                list.add(new com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory(Header_id,TotalQty,iVoucherNo));
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

    private void DeleteGoodsReceiptItemAlert(final com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(helper.DeleteGoodsReceipt(goodsReceiptHistory.getHeaderId(),goodsReceiptHistory.getiVoucherNo())) {
                            list.remove(pos);
                            adapter.notifyDataSetChanged();
                            setRecyclerView();
                            Toast.makeText(GoodsReceiptHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_receipt_history);

        add_new = findViewById(R.id.add_save);
        date = findViewById(R.id.title);
        empty_frame = findViewById(R.id.empty_frame);
        img_home = findViewById(R.id.home);
        helper = new DatabaseHelper(this);


        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoodsReceiptHistory.this,Home.class));
                finishAffinity();
            }
        });

       // date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        date.setText("Goods Receipt");
        list = new ArrayList<>();
        adapter = new GoodsReceiptHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));



        //setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GoodsReceiptHistory.this,GoodsReceipt.class));
            }
        });




adapter.setOnClickListener(new GoodsReceiptHistoryAdapter.OnClickListener() {
    @Override
    public void onEditItemClick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory) {
        Intent intent1 = new Intent(GoodsReceiptHistory.this,GoodsReceipt.class);
        intent1.putExtra("HeaderId",goodsReceiptHistory.getHeaderId());
        intent1.putExtra("iVoucherNo",goodsReceiptHistory.getiVoucherNo());
        intent1.putExtra("EditMode", true);
        startActivity(intent1);
    }

    @Override
    public void onDeleteItemClick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, int pos) {
        //DeleteGoodsReceiptItemAlert(goodsReceiptHistory, pos);
    }
});
    }
}