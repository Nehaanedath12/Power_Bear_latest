package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputLayout;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.SearchCustomer;
import com.sangsolutions.powerbear.Adapter.CustomerAdapter.SearchCustomerAdapter;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProduct;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProductAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectCustomerOrProduct extends AppCompatActivity {
EditText et_customer_product,from,to;
SearchCustomerAdapter customerAdapter;
SearchProductAdapter productAdapter;
List<SearchCustomer> list;
List<SearchProduct> list2;
TextView title;
TextInputLayout il_from,il_to;
final DatabaseHelper helper = new DatabaseHelper(this);
Button btn_search;
String customer = "0";
    AlertDialog alertDialog;
    RecyclerView rv_search;
    View view;
    ImageView img_home;

    private void ProductSearch(String keyword) {
            Cursor cursor ;
                cursor =  helper.SearchProduct2(keyword);

        if (cursor != null&&!keyword.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Code = cursor.getString(cursor.getColumnIndex("Code"));
                    String MasterId = cursor.getString(cursor.getColumnIndex("MasterId"));
                    list2.add(new SearchProduct(Name,Code,MasterId));
                    cursor.moveToNext();

                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(productAdapter);
                    }

                      productAdapter.setOnClickListener(new SearchProductAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(SearchProduct search_item) {
                            et_customer_product.setText(search_item.getName());
                            alertDialog.dismiss();
                            customer = search_item.getBarcode();
                        }
                    });
                }

            } else {
                list2.clear();
                list2.add(new SearchProduct("No Products available!", "",""));
                rv_search.setAdapter(productAdapter);

            }

        }

    private void CustomerSearch(String keyword) {
        AndroidNetworking.get("http://"+new Tools().getIP(SelectCustomerOrProduct.this)+URLs.GetCustomer)
                .addQueryParameter("condition",keyword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            list.clear();
                            JSONArray jsonArray = new JSONArray(response.getString("CustomerDetails"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("Name");
                                String code = jsonObject.getString("Code");
                                String MasterId = jsonObject.getString("MasterId");

                                list.add(new SearchCustomer(MasterId, name, code));

                                if (jsonArray.length() == i + 1) {
                                    rv_search.setAdapter(customerAdapter);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorBody());
                        list.clear();
                        list.add(new SearchCustomer("", "No Products available!",""));
                        rv_search.setAdapter(customerAdapter);
                    }
                });


        customerAdapter.setOnClickListener(new SearchCustomerAdapter.OnClickListener() {
            @Override
            public void onItemClick(SearchCustomer search_item) {
                et_customer_product.setText(search_item.getName());
                alertDialog.dismiss();
                customer = search_item.getMasterId();
            }
        });


    }


    public void SearchAlert(final String report_type) {
        view = LayoutInflater.from(this).inflate(R.layout.search_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search " + report_type)
                .setView(view);
        alertDialog = builder.create();
        alertDialog.show();


        EditText search_edit = view.findViewById(R.id.search_edit);
        rv_search = view.findViewById(R.id.search_recycler);
        rv_search.setLayoutManager(new LinearLayoutManager(this));




        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(report_type.equals("stock_count")) {
                    ProductSearch(editable.toString());
                }else {
                    CustomerSearch(editable.toString());
                }
            }


        });

    }

@RequiresApi(api = Build.VERSION_CODES.O)
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer_or_product);
    et_customer_product = findViewById(R.id.customer_or_product);
    list= new ArrayList<>();
    list2 = new ArrayList<>();
    title = findViewById(R.id.date);
    from = findViewById(R.id.from);
    to = findViewById(R.id.to);
    il_from = findViewById(R.id.il_from);
    il_to = findViewById(R.id.il_to);
    btn_search = findViewById(R.id.search);
    img_home = findViewById(R.id.home);
    img_home.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SelectCustomerOrProduct.this,Home.class);
            startActivity(intent);
            finishAffinity();
        }
    });


    customerAdapter = new SearchCustomerAdapter(this,list);
    productAdapter = new SearchProductAdapter(this,list2);

    final Intent intent = getIntent();
    final String report_type = intent.getStringExtra("report_type");

    assert report_type != null;

    Date c = Calendar.getInstance().getTime();
    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    final String date = df.format(c);

    switch (report_type) {
        case "stock_count":

            et_customer_product.setHint("Select Product");
            title.setText("Stock count");
            from.setText(date);
            to.setText(date);


            break;
        case "delivery_note":

            et_customer_product.setHint("Select Customer");
            title.setText("Delivery note");
            from.setText(date);
            to.setText(date);

            break;
        case "goods_receipt":

            et_customer_product.setHint("Select Vendor");
            title.setText("Goods receipt");

            from.setText(date);
            to.setText(date);
            break;
        case "pending_po":

            et_customer_product.setHint("Select Vendor");
            title.setText("Pending po");
            from.setVisibility(View.INVISIBLE);
            to.setVisibility(View.INVISIBLE);
            il_from.setVisibility(View.INVISIBLE);
            il_to.setVisibility(View.INVISIBLE);
            break;
        case "pending_so":

            et_customer_product.setHint("Select Customer");
            title.setText("Pending so");

            from.setVisibility(View.INVISIBLE);
            to.setVisibility(View.INVISIBLE);
            il_from.setVisibility(View.INVISIBLE);
            il_to.setVisibility(View.INVISIBLE);
            break;
    }


    btn_search.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(report_type.equals("pending_so")||report_type.equals("pending_po")) {
                Intent intent1 = new Intent(SelectCustomerOrProduct.this, ReportOPAndSO.class);
                intent1.putExtra("report_type", report_type);
                intent1.putExtra("customer", customer);
                startActivity(intent1);
            }else{
                Intent intent1 = new Intent(SelectCustomerOrProduct.this, ReportGoods_Stock_Delivery.class);
                intent1.putExtra("report_type", report_type);
                intent1.putExtra("customer", customer);
                intent1.putExtra("from", Tools.dateFormat(from.getText().toString()));
                intent1.putExtra("to", Tools.dateFormat(to.getText().toString()));
                startActivity(intent1);
            }
        }
    });



    et_customer_product.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (report_type) {
                case "stock_count":
                    SearchAlert("Product");
                    break;
                case "delivery_note":
                case "pending_po":
                    SearchAlert("Vendor");
                    break;
                case "goods_receipt":
                case "pending_so":
                    SearchAlert("Customer");
                    break;
            }

        }
    });


    from.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    String StringDate =
                            Tools.checkDigit(dayOfMonth)+
                            "-" +
                            Tools.checkDigit(month + 1) +
                            "-"+
                            year;
                    from.setText(StringDate);
                }
            };
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(SelectCustomerOrProduct.this, R.style.datepicker, onDateSetListener, year, month, day);
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

                    String StringDate =  Tools.checkDigit(dayOfMonth)+
                            "-" +
                            Tools.checkDigit(month + 1) +
                            "-"+
                            year;
                    to.setText(StringDate);
                }
            };
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(SelectCustomerOrProduct.this, R.style.datepicker, onDateSetListener, year, month, day);
            datePickerDialog.setTitle("Select Date");
            datePickerDialog.show();
        }
    });

    }
}