package com.sangsolutions.powerbear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sangsolutions.powerbear.Adapter.ProductClassAdapter.ProductClass;
import com.sangsolutions.powerbear.Adapter.ProductClassAdapter.ProductClassAdapter;
import com.sangsolutions.powerbear.Adapter.WareHouseAdapter.WareHouse;
import com.sangsolutions.powerbear.Adapter.WareHouseAdapter.WareHouseAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockDetailsReportSelectActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton;
    int radioButtonId;
    EditText productE,warehouseE,dateE;
    ImageView home;
    DatabaseHelper helper;
    List<ProductClass> productList;
    ProductClassAdapter productAdapter;
    RecyclerView recyclerViewProduct;
    AlertDialog alertDialog;
    WareHouseAdapter wareHouseAdapter;
    List<WareHouse> wareHouseList;
    SimpleDateFormat formatter;
    String StringDate;
    Button search_B;
    String iWareHouse="0",iProduct="0";
    String iType="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details_report_select);
        home=findViewById(R.id.home);
        radioGroup=findViewById(R.id.radioGrp);
        productE=findViewById(R.id.product_E);
        warehouseE=findViewById(R.id.warehouse_E);
        dateE=findViewById(R.id.date);
        search_B=findViewById(R.id.search_b);

        productList=new ArrayList<>();
        wareHouseList=new ArrayList<>();
        productAdapter=new ProductClassAdapter(this,productList);
        wareHouseAdapter=new WareHouseAdapter(this,wareHouseList);

        helper=new DatabaseHelper(this);
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        StringDate = formatter.format(new Date());
        dateE.setText(StringDate);

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
        warehouseE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warehouseAlert();
            }
        });
        dateE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringDate = Tools.checkDigit(dayOfMonth) +
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-" +
                                year;
                        dateE.setText(StringDate);

                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StockDetailsReportSelectActivity.this, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        search_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),StockDetailsReport.class);
                intent.putExtra("iWarehouse",iWareHouse);
                intent.putExtra("iProduct",iProduct);
                intent.putExtra("Date",StringDate);
                intent.putExtra("iType",iType);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        iWareHouse="0";iProduct="0";
        iType="0";
        StringDate = formatter.format(new Date());
        productE.setText("");warehouseE.setText("");dateE.setText(StringDate);
        radioGroup.check(R.id.overallRadio);
    }

    private void warehouseAlert() {
        View view = LayoutInflater.from(this).inflate(R.layout.excess_shortage_search_warehouse, null, false);

        RecyclerView WareH_RView = view.findViewById(R.id.search_Rview);
        AlertDialog.Builder dialog_WareHouse = new AlertDialog.Builder(this);
        dialog_WareHouse.setView(view);
        dialog_WareHouse.setCancelable(true);
        AlertDialog alertDialog_wareH = dialog_WareHouse.create();
        alertDialog_wareH.show();


        WareH_RView.setLayoutManager(new LinearLayoutManager(this));
        Cursor cursor = helper.GetWarehouse();

        wareHouseList.clear();
        if(cursor!=null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                if(!cursor.getString(cursor.getColumnIndex("Name")).equals(" ")) {
                    WareHouse wareHouseClass = new WareHouse(cursor.getString(cursor.getColumnIndex("MasterId")),
                            cursor.getString(cursor.getColumnIndex("Name")));
                    wareHouseList.add(wareHouseClass);
                }
                cursor.moveToNext();
                if (cursor.getCount() == i + 1) {
                    WareH_RView.setAdapter(wareHouseAdapter);
                }

                wareHouseAdapter.setOnClickListener(new WareHouseAdapter.OnClickListener() {
                    @Override
                    public void OnItemClick(WareHouse warehouseClass, int position) {
                        warehouseE.setText(warehouseClass.getName());
                        iWareHouse=warehouseClass.getMasterId();
                        alertDialog_wareH.dismiss();
                    }
                });

            }
        }
    }

    private void productAlert() {
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_product,null,false);
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setView(view);
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

    public void onRadioButtonClicked(View view) {
        radioButtonId=radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioButtonId);
        if(radioButton.getText().equals("Overall")){
            iType="0";
        }
        else {
            iType="1";
        }

    }
}