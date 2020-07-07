package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter.Goods_Stock;
import com.sangsolutions.powerbear.Adapter.PendingSOAndPOReportAdapter.PendingSoReportOP;
import com.sangsolutions.powerbear.Adapter.PendingSOAndPOReportAdapter.PendingSoReportOPAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportOPAndSO extends AppCompatActivity {
String report_type="",customer="0";
    FrameLayout empty_frame;
    RecyclerView recyclerView;
    PendingSoReportOPAdapter pendingSoReportOPAdapter;
    List<PendingSoReportOP> list;
    ImageView img_home;
    TextView title;
  public void LoadPendingSoOrPO(final String report_type, final String customer){
      if(report_type.equals("pending_so")){
          title.setText("Pending SO");
          AndroidNetworking.get(URLs.GetPendingSODetails)
                  .addQueryParameter("iCustomer",customer)
                  .setPriority(Priority.MEDIUM)
                  .build()
                  .getAsJSONObject(new JSONObjectRequestListener() {
                      @Override
                      public void onResponse(JSONObject response) {
                          try {
                              empty_frame.setVisibility(View.INVISIBLE);
                              list.clear();
                              JSONArray jsonArray = new JSONArray(response.getString("PendingSO_Details"));
                              for(int i = 0;i<jsonArray.length();i++ ){
                                  JSONObject jsonObject = jsonArray.getJSONObject(i);
                                  String DocNo = jsonObject.getString("DocNo");
                                  String DocDate = jsonObject.getString("DocDate");
                                  String Cusomer = jsonObject.getString("Cusomer");
                                  String HeaderId = jsonObject.getString("HeaderId");
                                  String SINo = jsonObject.getString("SINo");
                                  String Product = jsonObject.getString("Product");
                                  String ProductName = jsonObject.getString("ProductName");
                                  String ProductCode = jsonObject.getString("ProductCode");
                                  String Qty = jsonObject.getString("Qty");
                                  String unit = jsonObject.getString("unit");


                                  list.add(new PendingSoReportOP(Cusomer,DocDate,DocNo,HeaderId,Product,ProductCode,ProductName,Qty,SINo,unit));

                                  if(jsonArray.length()==i+1){
                                      recyclerView.setAdapter(pendingSoReportOPAdapter);
                                  }
                              }
                          } catch (JSONException e) {
                              empty_frame.setVisibility(View.VISIBLE);
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
      else if(report_type.equals("pending_po")){
          title.setText("Pending PO");
          AndroidNetworking.get(URLs.GetPendingPODetails)
                  .addQueryParameter("iVendor",customer)
                  .setPriority(Priority.MEDIUM)
                  .build()
                  .getAsJSONObject(new JSONObjectRequestListener() {
                      @Override
                      public void onResponse(JSONObject response) {
                          try {
                              empty_frame.setVisibility(View.INVISIBLE);
                              list.clear();
                              JSONArray jsonArray = new JSONArray(response.getString("PendingPO_Details"));
                              for(int i = 0;i<jsonArray.length();i++ ){
                                  JSONObject jsonObject = jsonArray.getJSONObject(i);
                                  String DocNo = jsonObject.getString("DocNo");
                                  String DocDate = jsonObject.getString("DocDate");
                                  String Cusomer = jsonObject.getString("Cusomer");
                                  String HeaderId = jsonObject.getString("HeaderId");
                                  String SINo = jsonObject.getString("SINo");
                                  String Product = jsonObject.getString("Product");
                                  String ProductName = jsonObject.getString("ProductName");
                                  String ProductCode = jsonObject.getString("ProductCode");
                                  String Qty = jsonObject.getString("Qty");
                                  String unit = jsonObject.getString("unit");


                                  list.add(new PendingSoReportOP(Cusomer,DocDate,DocNo,HeaderId,Product,ProductCode,ProductName,Qty,SINo,unit));

                                  if(jsonArray.length()==i+1){
                                      recyclerView.setAdapter(pendingSoReportOPAdapter);
                                  }
                              }
                          } catch (JSONException e) {
                              empty_frame.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_report_o_p_and_s_o);
        Intent intent = getIntent();
        if(intent!=null) {
            report_type = intent.getStringExtra("report_type");
            customer = intent.getStringExtra("customer");
        }
        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportOPAndSO.this,Home.class));
                finishAffinity();
            }
        });
        empty_frame = findViewById(R.id.empty_frame);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        pendingSoReportOPAdapter = new PendingSoReportOPAdapter(list,this);

        LoadPendingSoOrPO(report_type,customer);
    }
}