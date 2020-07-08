package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class StockCount extends AppCompatActivity {
ListView lv_warehouse ;
List<Warehouse> list;
DatabaseHelper helper;
WarehouseAdapter adapter;
TextView title;
ImageView img_home;
Handler handler;
    private AnimationDrawable animationDrawable;
    private ImageView mProgressBar;

   public void LoadWarehouse(){
       Cursor cursor = helper.GetWarehouse();
       list.clear();
       if(cursor!=null){
           for (int i = 0; i < cursor.getCount(); i++) {
               list.add(new Warehouse(cursor.getString(cursor.getColumnIndex("MasterId")),cursor.getString(cursor.getColumnIndex("Name"))));

               cursor.moveToNext();
               if(cursor.getCount()==i+1){
                   lv_warehouse.setAdapter(adapter);
                   mProgressBar.setVisibility(View.INVISIBLE);
                   animationDrawable.stop();
               }

           }
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count);
        lv_warehouse = findViewById(R.id.select_warehouse);
        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        title.setText("Select warehouse");

        helper = new DatabaseHelper(this);
        list = new ArrayList<>();
        adapter = new WarehouseAdapter(list);

        mProgressBar = findViewById(R.id.main_progress);
        mProgressBar.setBackgroundResource(R.drawable.loading);

        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if(!PublicData.pendingPOFinished){
                    mProgressBar.setVisibility(View.VISIBLE);
                    animationDrawable.start();
                    handler.postDelayed(this, 1000);
                }else {
                    LoadWarehouse();
                    handler.removeCallbacksAndMessages(null);
                }
            }
        };
        handler.postDelayed(r, 1000);



        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockCount.this,Home.class));
                finishAffinity();
            }
        });




        lv_warehouse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(StockCount.this, StockCountWarehouse.class);
                intent.putExtra("warehouse",parent.getItemAtPosition(position).toString());
                intent.putExtra("EditMode",false);
                intent.putExtra("voucherNo","");
                startActivity(intent);


                Toast.makeText(StockCount.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }







private class Warehouse {

        String MasterId,Name;

    public Warehouse(String masterId, String name) {
        MasterId = masterId;
        Name = name;
    }

    public String getMasterId() {
        return MasterId;
    }

    public void setMasterId(String masterId) {
        MasterId = masterId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}


private class WarehouseAdapter extends BaseAdapter {
        List<Warehouse> list;

        public WarehouseAdapter(List<Warehouse> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).MasterId;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View view = LayoutInflater.from(StockCount.this).inflate(R.layout.warehouse_item,parent,false);
            TextView warehouse = view.findViewById(R.id.warehouse);
            warehouse.setText(list.get(position).Name);
           return view;
        }
    }

}
