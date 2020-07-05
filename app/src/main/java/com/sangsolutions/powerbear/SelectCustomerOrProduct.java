package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.Customer;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.CustomerAdapter;
import com.sangsolutions.powerbear.Adapter.ProductAdapter.Product;
import com.sangsolutions.powerbear.Adapter.ProductAdapter.ProductAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectCustomerOrProduct extends AppCompatActivity {
Spinner sp_customer_product;
CustomerAdapter customerAdapter;
ProductAdapter productAdapter;
List<Customer> list;
List<Product> list2;
ProgressDialog pd;
DatabaseHelper helper = new DatabaseHelper(this);
public void LoadCustomer(String report_type) {

    list.clear();
    list2.clear();

    if (report_type.equals("stock_count")) {
        Cursor cursor = helper.GetProduct();

        if(cursor!=null){
            if(cursor.moveToFirst()){
                pd.show();
                list2.add(new Product("select product", "", ""));
                for(int i = 0 ;i<cursor.getCount();i++){
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    String code = cursor.getString(cursor.getColumnIndex("Code"));
                    String master_id = cursor.getString(cursor.getColumnIndex("MasterId"));

                list2.add(new Product("Name :"+name,"Code :"+code,master_id));

                cursor.moveToNext();

                if(cursor.getCount()==i+1){
                    sp_customer_product.setAdapter(productAdapter);
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
                }
            }
        }

    } else {
        pd.show();
        AndroidNetworking.get(URLs.GetCustomer)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }

                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("CustomerDetails"));
                            list.add(new Customer("", "select customer", ""));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("Name");
                                String code = jsonObject.getString("Code");
                                String MasterId = jsonObject.getString("MasterId");

                                list.add(new Customer(MasterId, "Name :"+name, "Code :"+code));

                                if (jsonArray.length() == i + 1) {
                                    sp_customer_product.setAdapter(customerAdapter);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorBody());
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
    }
}
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer_or_product);
    sp_customer_product = findViewById(R.id.customer);
    list= new ArrayList<>();
    list2 = new ArrayList<>();

    customerAdapter = new CustomerAdapter(list,this);
    productAdapter = new ProductAdapter(list2,this);
    final Intent intent = getIntent();
    final String report_type = intent.getStringExtra("report_type");

    pd = new ProgressDialog(SelectCustomerOrProduct.this);
    pd.setMessage("please wait..");
    pd.setCancelable(false);
    pd.setIndeterminate(true);

    assert report_type != null;
    LoadCustomer(report_type);

    sp_customer_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!adapterView.getSelectedItem().toString().equals("")) {
                if(report_type.equals("pending_so")||report_type.equals("pending_po")) {
                    Intent intent1 = new Intent(SelectCustomerOrProduct.this, ReportOPAndSO.class);
                    intent1.putExtra("report_type", report_type);
                    intent1.putExtra("customer", adapterView.getSelectedItem().toString());
                    startActivity(intent1);
                }else if(report_type.equals("stock_count")){
                    Intent intent1 = new Intent(SelectCustomerOrProduct.this, ReportGoods_Stock_Delivery.class);
                    intent1.putExtra("report_type", report_type);
                    intent1.putExtra("customer", adapterView.getSelectedItem().toString());
                    startActivity(intent1);
                }else{
                    Intent intent2 = new Intent(SelectCustomerOrProduct.this,ReportGoods_Stock_Delivery.class);
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