package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter.Goods_Stock;
import com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter.Goods_Delivery_Adapter;
import com.sangsolutions.powerbear.Adapter.StockCountReportAdapter.StockCountReport;
import com.sangsolutions.powerbear.Adapter.StockCountReportAdapter.StockCountReportAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ReportGoods_Stock_Delivery extends AppCompatActivity {
    String report_type="",customer="";
    EditText from,to;
    Button search;
    FrameLayout empty_frame;
    RecyclerView recyclerView;
    Goods_Delivery_Adapter goods_delivery_adapter;
    StockCountReportAdapter stockCountReportAdapter;
    List<Goods_Stock> list;
    List<StockCountReport> list2;
    DatabaseHelper helper;
    LinearLayout ll_stock,ll_goods_delivery;


    public void LoadRecycler(String from,String to,String report_type,String customer){
        if(report_type.equals("goods_receipt")){
            AndroidNetworking.get(URLs.GetGRNote)
                    .addQueryParameter("fDate",from)
                    .addQueryParameter("tDate",to)
                    .addQueryParameter("iUser",helper.GetUserId())
                    .addQueryParameter("iCustomer",customer)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                empty_frame.setVisibility(View.INVISIBLE);
                                JSONArray jsonArray = new JSONArray(response.getString("MRN_Details"));
                                for(int i = 0;i<jsonArray.length();i++ ){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String ProductName = jsonObject.getString("ProductName");
                                    String ProductCode = jsonObject.getString("ProductCode");
                                    String Vendor = jsonObject.getString("Vendor");
                                    String Qty = jsonObject.getString("Qty");

                                    list.add(new Goods_Stock(ProductName,ProductCode,Vendor,Qty));

                                    if(jsonArray.length()==i+1){
                                        recyclerView.setAdapter(goods_delivery_adapter);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error data",anError.getErrorBody());
                        empty_frame.setVisibility(View.VISIBLE);
                        }
                    });
        }
        else if(report_type.equals("delivery_note")){
            AndroidNetworking.get(URLs.GetDeliveryNote)
                    .addQueryParameter("fDate",from)
                    .addQueryParameter("tDate",to)
                    .addQueryParameter("iUser",helper.GetUserId())
                    .addQueryParameter("iVendor",customer)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                empty_frame.setVisibility(View.INVISIBLE);
                                JSONArray jsonArray = new JSONArray(response.getString("Delivery_Details"));
                                for(int i = 0;i<jsonArray.length();i++ ){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String ProductName = jsonObject.getString("ProductName");
                                    String ProductCode = jsonObject.getString("ProductCode");
                                    String Vendor = jsonObject.getString("Vendor");
                                    String Qty = jsonObject.getString("Qty");

                                    list.add(new Goods_Stock(ProductName,ProductCode,Vendor,Qty));

                                    if(jsonArray.length()==i+1){
                                        recyclerView.setAdapter(goods_delivery_adapter);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            empty_frame.setVisibility(View.VISIBLE);
                            Log.d("error",anError.getErrorBody());
                        }
                    });
        }
        else if(report_type.equals("stock_count")){
            AndroidNetworking.get(URLs.GetStockCount)
                    .addQueryParameter("fDate",from)
                    .addQueryParameter("tDate",to)
                    .addQueryParameter("iUser",helper.GetUserId())
                    .addQueryParameter("iProduct",customer)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                empty_frame.setVisibility(View.INVISIBLE);
                                JSONArray jsonArray = new JSONArray(response.getString("Stock_Details"));
                                for(int i = 0;i<jsonArray.length();i++ ){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String DocDate = jsonObject.getString("DocDate");
                                    String iVoucherNo = jsonObject.getString("iVoucherNo");
                                    String Waehouse = jsonObject.getString("Waehouse");
                                    String Name = jsonObject.getString("Name");
                                    String ProductCode = jsonObject.getString("ProductCode");
                                    String fQty = jsonObject.getString("fQty");
                                    String sUnit = jsonObject.getString("sUnit");
                                    String sRemarks = jsonObject.getString("sRemarks");

                                    list2.add(new StockCountReport(DocDate,iVoucherNo,ProductCode,Waehouse,Name,fQty,sUnit,sRemarks));

                                    if(jsonArray.length()==i+1){
                                        recyclerView.setAdapter(stockCountReportAdapter);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            empty_frame.setVisibility(View.VISIBLE);
                            Log.d("error",anError.getErrorBody());
                        }
                    });
        }
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DatabaseHelper(this);
        setContentView(R.layout.activity_report_goods__stock__delivery);
        Intent intent = getIntent();
        if(intent!=null) {
            report_type = intent.getStringExtra("report_type");
            customer = intent.getStringExtra("customer");
        }
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        search = findViewById(R.id.search);
        empty_frame = findViewById(R.id.empty_frame);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        goods_delivery_adapter = new Goods_Delivery_Adapter(list,this);
        stockCountReportAdapter = new StockCountReportAdapter(list2,this);
        ll_stock = findViewById(R.id.ll_stock_count);
        ll_goods_delivery = findViewById(R.id.ll_goods_delivery);
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String date = df.format(c);

        from.setText(date);
        to.setText(date);

        if(report_type.equals("stock_count")){
            ll_stock.setVisibility(View.VISIBLE);
            ll_goods_delivery.setVisibility(View.GONE);
        }else {
            ll_stock.setVisibility(View.GONE);
            ll_goods_delivery.setVisibility(View.VISIBLE);
        }

        LoadRecycler(date,date,report_type,customer);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!report_type.equals("")&&!customer.equals("")) {
                    LoadRecycler(from.getText().toString(), to.getText().toString(), report_type, customer);
                }
            }
        });

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String StringDate = year +
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-" +
                                Tools.checkDigit(dayOfMonth);
                        from.setText(StringDate);
                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportGoods_Stock_Delivery.this, R.style.datepicker, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }

        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String StringDate = year +
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-" +
                                Tools.checkDigit(dayOfMonth);
                        to.setText(StringDate);
                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportGoods_Stock_Delivery.this, R.style.datepicker, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }

        });
    }
}