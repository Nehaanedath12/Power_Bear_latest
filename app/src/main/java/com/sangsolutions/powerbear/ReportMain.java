package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportMain extends AppCompatActivity implements View.OnClickListener {
    ImageView img_home;
    TextView title;
    DatabaseHelper helper;
    GridView gridView;
    ReportMainMenuAdapter reportMainMenuAdapter;
    List<ReportMainMenu> list;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main);
    helper = new DatabaseHelper(this);
    title = findViewById(R.id.title);
    img_home = findViewById(R.id.home);
    gridView = findViewById(R.id.menu_grid);
    list = new ArrayList<>();
    reportMainMenuAdapter = new ReportMainMenuAdapter(list);
    title.setText("Reports");

        img_home.setOnClickListener(this);


    String LoginUserMenu = helper.GetLoginMenuIDs();
    list.clear();
    if(!LoginUserMenu.equals("")) {
        List<String> listMenu = Arrays.asList(LoginUserMenu.split(","));

        if(listMenu.contains("5")){
            list.add(new ReportMainMenu(R.drawable.ic_stock_count,"Pending PO"));
        }
        if(listMenu.contains("6")){
            list.add(new ReportMainMenu(R.drawable.ic_stock_count,"Pending SO"));
        }
        if(listMenu.contains("7")){
            list.add(new ReportMainMenu(R.drawable.ic_report,"Delivery Note"));
        }
        if(listMenu.contains("8")){
            list.add(new ReportMainMenu(R.drawable.ic_report,"Goods Receipt"));
        }
        if(listMenu.contains("9")){
            list.add(new ReportMainMenu(R.drawable.ic_report,"Stock count"));
        }
        if(listMenu.contains("10")){
            list.add(new ReportMainMenu(R.drawable.ic_stock_count,"Stock Details"));
        }

        if(list.size()>0){
            gridView.setAdapter(reportMainMenuAdapter);
        }

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (list.get(position).text){
             case "Pending PO":
                Intent intent = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent.putExtra("report_type","pending_po");
                startActivity(intent);
                break;
            case "Pending SO":
                Intent intent2 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent2.putExtra("report_type","pending_so");
                startActivity(intent2);
                break;
            case "Delivery Note":
                Intent intent3 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent3.putExtra("report_type","delivery_note");
                startActivity(intent3);
                break;
            case "Goods Receipt":
                Intent intent4 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent4.putExtra("report_type","goods_receipt");
                startActivity(intent4);
                break;
            case "Stock count":
                Intent intent5 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent5.putExtra("report_type","stock_count");
                startActivity(intent5);
                break;

         case "Stock Details":
                Intent intent6 = new Intent(ReportMain.this, StockDetailsReportSelectActivity.class);
                startActivity(intent6);
                finish();
                break;
            }
        }
    });

    }


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home:
                startActivity(new Intent(ReportMain.this,Home.class));
                finishAffinity();
                break;

        }
    }


    class ReportMainMenu{
    int Image;
    String text;

        public ReportMainMenu(int image, String text) {
            Image = image;
            this.text = text;
        }

        public int getImage() {
            return Image;
        }

        public void setImage(int image) {
            Image = image;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    class ReportMainMenuAdapter extends BaseAdapter {

    List<ReportMainMenu> list;

        public ReportMainMenuAdapter(List<ReportMainMenu> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(ReportMain.this).inflate(R.layout.report_menu_item,parent,false);
            TextView text = view.findViewById(R.id.text);
            ImageView image = view.findViewById(R.id.image);

            text.setText(list.get(position).getText());
            image.setImageResource(list.get(position).getImage());
            return view;
        }
    }
}