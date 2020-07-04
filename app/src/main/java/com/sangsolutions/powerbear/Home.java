package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {
ImageButton sync_btn,delivery_btn,stock_count_btn,goods_btn;
DatabaseHelper helper;



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sync_btn= findViewById(R.id.sync);
        delivery_btn= findViewById(R.id.delivery);
        stock_count_btn = findViewById(R.id.stock_count);
        goods_btn = findViewById(R.id.goods);
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
    }
}
