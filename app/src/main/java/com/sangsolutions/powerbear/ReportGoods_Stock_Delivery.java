package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ReportGoods_Stock_Delivery extends AppCompatActivity {
    String report_type="",customer="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_goods__stock__delivery);
        Intent intent = getIntent();
        report_type = intent.getStringExtra("report_type");
        customer = intent.getStringExtra("customer");
    }
}