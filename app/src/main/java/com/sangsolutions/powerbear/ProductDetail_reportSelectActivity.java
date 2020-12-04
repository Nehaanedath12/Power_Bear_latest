package com.sangsolutions.powerbear;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.ProductClassAdapter.ProductClass;
import com.sangsolutions.powerbear.Adapter.ProductClassAdapter.ProductClassAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail_reportSelectActivity extends AppCompatActivity {
    EditText productE;
    List<ProductClass> productList;
    ProductClassAdapter productAdapter;
    AlertDialog alertDialog;
    RecyclerView recyclerViewProduct;
    DatabaseHelper helper;
    String iProduct="0";
    Button search;
    ImageView home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_report_select);
        productE=findViewById(R.id.product_E);
        search=findViewById(R.id.search_b);
        home=findViewById(R.id.home);
        helper=new DatabaseHelper(this);
        productList=new ArrayList<>();
        productAdapter=new ProductClassAdapter(this,productList);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home.class));
                finish();
            }
        });

        productE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAlert();
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!productE.getText().toString().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
                    intent.putExtra("iProduct", iProduct);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ProductDetail_reportSelectActivity.this, "select product", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void productAlert() {
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_product,null,false);
        AlertDialog.Builder builder=new AlertDialog.Builder(ProductDetail_reportSelectActivity.this).setView(view);
        alertDialog=builder.create();
        alertDialog.show();
        EditText searchEdit=view.findViewById(R.id.searchEdit);
        recyclerViewProduct=view.findViewById(R.id.recyclerViewProduct);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectProduct(s.toString());
            }
        });
    }

    private void selectProduct(String string) {
        productList.clear();
        Cursor cursor=helper.GetProductByKeyword(string);
        if(cursor!=null&& string!=null){
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                for (int i=0;i<cursor.getCount();i++){
                    ProductClass productClass = new ProductClass(cursor.getString(cursor.getColumnIndex("Name")),
                            cursor.getString(cursor.getColumnIndex("Unit")),
                            cursor.getString(cursor.getColumnIndex("MasterId")),
                            cursor.getString(cursor.getColumnIndex("Code")),
                            cursor.getString(cursor.getColumnIndex("Barcode")));
                    productList.add(productClass);
                    cursor.moveToNext();
                    if (cursor.getCount() == i + 1) {
                        recyclerViewProduct.setAdapter(productAdapter);
                    }

                    productAdapter.setOnClickListener(new ProductClassAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(ProductClass productClass, int position) {
                            productE.setText(productClass.getProduct_name());
                            iProduct=productClass.getProduct_masterId();
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        }
        else {
            productList.clear();
            productList.add(new ProductClass("No Products available!", "", "", "", ""));
            recyclerViewProduct.setAdapter(productAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        productE.setText("");
    }
}
