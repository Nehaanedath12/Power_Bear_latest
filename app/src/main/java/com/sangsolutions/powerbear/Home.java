package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ALL")
public class Home extends AppCompatActivity {
Button sync_btn,delivery_btn,stock_count_btn,goods_btn,report_btn;
DatabaseHelper helper;
ImageView img_logout;
TextView tv_username,tv_date;
ImageView img_settings;
    SharedPreferences preferences;
    @Override
    protected void onResume() {
        super.onResume();
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        tv_date.setText(df.format(c));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        sync_btn= findViewById(R.id.sync);
        delivery_btn= findViewById(R.id.delivery);
        stock_count_btn = findViewById(R.id.stock_count);
        goods_btn = findViewById(R.id.goods);
        report_btn = findViewById(R.id.delivery_note);
        img_logout = findViewById(R.id.logout);
        tv_username = findViewById(R.id.username);
        tv_date = findViewById(R.id.date);
        img_settings = findViewById(R.id.settings);
        helper = new DatabaseHelper(this);

        tv_username.setText(helper.GetLoginUser());

    stock_count_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preferences.getBoolean("WarehouseFinished", false)
                    && preferences.getBoolean("pendingPOFinished", false)
                    && preferences.getBoolean("pendingSOFinished", false)
                    &&  preferences.getBoolean("goodsReceiptTypeFinished", false)
                    ){
            startActivity(new Intent(Home.this,StockCountList.class));
        }
            else {
                Toast.makeText(Home.this, "Wait for the data to be synced..", Toast.LENGTH_SHORT).show();
            }
        }
    });

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Sync started...", Toast.LENGTH_SHORT).show();
               //start with SyncGoodsReceipt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    new ScheduleJob().SyncGoodsReceipt(Home.this);
                }

            }
        });

    img_logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(Home.this,view);
            popupMenu.inflate(R.menu.logout_menu);
            popupMenu.setGravity(Gravity.BOTTOM);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.logout){
                    if(helper.DeleteCurrentUser()){


                        startActivity(new Intent(Home.this,MainActivity.class));
                        finish();
                    }
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    });

    goods_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preferences.getBoolean("WarehouseFinished", false)
                    && preferences.getBoolean("pendingPOFinished", false)
                    && preferences.getBoolean("pendingSOFinished", false)
                    &&  preferences.getBoolean("goodsReceiptTypeFinished", false)
                    ){

                    Intent intent = new Intent(Home.this, GoodsReceiptHistory.class);
                    startActivity(intent);
                }else {
                Toast.makeText(Home.this, "Wait for the data to be synced..", Toast.LENGTH_SHORT).show();
            }
        }
    });

        delivery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getBoolean("WarehouseFinished", false)
                        && preferences.getBoolean("pendingPOFinished", false)
                        && preferences.getBoolean("pendingSOFinished", false)
                        &&  preferences.getBoolean("goodsReceiptTypeFinished", false)
                ){
                Intent intent = new Intent(Home.this,DeliveryNoteHistory.class);
                startActivity(intent);
            }else {
                Toast.makeText(Home.this, "Wait for the data to be synced..", Toast.LENGTH_SHORT).show();
            }
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        startActivity(new Intent(Home.this,ReportMain.class));
            }
        });

        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,SetIPActivity.class));
            }
        });
    }


}
