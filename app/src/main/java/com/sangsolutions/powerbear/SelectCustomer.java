package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.Customer;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.CustomerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectCustomer extends AppCompatActivity {
Spinner sp_customer;
CustomerAdapter customerAdapter;
List<Customer> list;
ProgressDialog pd;
public void LoadCustomer(){
    pd.show();
    list.clear();
    AndroidNetworking.get(URLs.GetCustomer)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {

                @Override
                public void onResponse(JSONObject response) {
                    if(pd.isShowing()){
                        pd.dismiss();
                    }

                    try {
                        JSONArray jsonArray = new JSONArray(response.getString("CustomerDetails"));
                        list.add(new Customer("","select customer",""));
                        for(int i = 0;i<jsonArray.length();i++){
                          JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("Name");
                            String code = jsonObject.getString("Code");
                            String MasterId = jsonObject.getString("MasterId");

                            list.add(new Customer(MasterId,name,code));

                            if(jsonArray.length()==i+1){
                                sp_customer.setAdapter(customerAdapter);
                            }
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("error",anError.getErrorBody());
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                }
            });
}

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer);
    sp_customer = findViewById(R.id.customer);
    list= new ArrayList<>();
    customerAdapter = new CustomerAdapter(list,this);
    final Intent intent = getIntent();
    final String report_type = intent.getStringExtra("report_type");

    pd = new ProgressDialog(SelectCustomer.this);
    pd.setMessage("please wait..");
    pd.setCancelable(false);
    pd.setIndeterminate(true);

    LoadCustomer();

    sp_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!adapterView.getSelectedItem().toString().equals("")) {
                assert report_type != null;
                if(report_type.equals("pending_so")||report_type.equals("pending_po")){
                    Intent intent1 = new Intent(SelectCustomer.this,ReportOPAndSO.class);
                    intent1.putExtra("report_type",report_type);
                    intent1.putExtra("customer",adapterView.getSelectedItem().toString());
                    startActivity(intent1);
                }else{
                    Intent intent2 = new Intent(SelectCustomer.this,ReportGoods_Stock_Delivery.class);
                    intent2.putExtra("report_type",report_type);
                    intent2.putExtra("customer",adapterView.getSelectedItem().toString());
                    startActivity(intent2);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

    }
}