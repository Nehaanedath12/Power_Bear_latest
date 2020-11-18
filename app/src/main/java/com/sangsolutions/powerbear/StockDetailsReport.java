package com.sangsolutions.powerbear;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.StockDetailsAdapter.StockDetailsAdapter;
import com.sangsolutions.powerbear.Adapter.StockDetailsAdapter.StockDetailsClass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockDetailsReport extends AppCompatActivity {

    RecyclerView recyclerView;
    String iProduct,iWarehouse,iType, idate;
    List<StockDetailsClass> detailClassList;
    StockDetailsAdapter detailAdapter;
    ImageView mProgressBar,home;
    private AnimationDrawable animationDrawable;
    FrameLayout emptyIcon;
    TextView wareHouse_head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details_report);
        recyclerView=findViewById(R.id.recyclerView);
        mProgressBar =findViewById(R.id.main_progress);
        emptyIcon=findViewById(R.id.empty_frame);
        home=findViewById(R.id.home);
        wareHouse_head=findViewById(R.id.warehouse_Head);

        iProduct=getIntent().getStringExtra("iProduct");
        iWarehouse=getIntent().getStringExtra("iWarehouse");
        iType=getIntent().getStringExtra("iType");
        idate =getIntent().getStringExtra("Date");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailClassList=new ArrayList<>();
        detailAdapter=new StockDetailsAdapter(this,detailClassList,iType);

        mProgressBar.setBackgroundResource(R.drawable.loading);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();

        if(iType.equals("1")){
            wareHouse_head.setVisibility(View.VISIBLE);
        }


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home.class));
                finishAffinity();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        animationDrawable.start();
        AndroidNetworking.get("http://"+new Tools().getIP(this)+URLs.GetStockDetails)
                .addQueryParameter("iProduct",iProduct)
                .addQueryParameter("iWarehouse",iWarehouse)
                .addQueryParameter("idate",idate)
                .addQueryParameter("iType",iType)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getValues(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        animationDrawable.stop();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.d("error data",anError.getErrorBody());
                        emptyIcon.setVisibility(View.VISIBLE);

                    }
                });
    }

    private void getValues(JSONObject response) {
        animationDrawable.stop();
        mProgressBar.setVisibility(View.INVISIBLE);
        try {
            emptyIcon.setVisibility(View.INVISIBLE);
            detailClassList.clear();
            JSONArray jsonArray=new JSONArray(response.getString("StockDetail"));
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    StockDetailsClass detailClass = new StockDetailsClass(jsonObject.getString("Product"),
                            jsonObject.getString("Productode"),
                            jsonObject.getString("Warehouse"),
                            jsonObject.getString("BalQty"));
                    detailClassList.add(detailClass);
                    if (i + 1 == jsonArray.length()) {
                        recyclerView.setAdapter(detailAdapter);
                        animationDrawable.stop();

                    }

                }
            }
            else if(jsonArray.length()<=0){
                emptyIcon.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            emptyIcon.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
    }
}