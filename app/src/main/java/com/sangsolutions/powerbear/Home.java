package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

public class Home extends AppCompatActivity {
ImageButton sync_btn,delivery_btn,stock_count_btn,goods_btn,report_btn;
DatabaseHelper helper;



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sync_btn= findViewById(R.id.sync);
        delivery_btn= findViewById(R.id.delivery);
        stock_count_btn = findViewById(R.id.stock_count);
        goods_btn = findViewById(R.id.goods);
        report_btn = findViewById(R.id.delivery_note);
        helper = new DatabaseHelper(this);

    stock_count_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Home.this,StockCountList.class));
        }
    });

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //start with SyncGoodsReceipt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    new ScheduleJob().SyncGoodsReceipt(Home.this);
                }

            }
        });

    goods_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Home.this,GoodsReceiptHistory.class);
            startActivity(intent);
        }
    });

        delivery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,DeliveryNoteHistory.class);
                startActivity(intent);
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        startActivity(new Intent(Home.this,ReportMain.class));
            }
        });
    }
}
