package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sangsolutions.powerbear.Adapter.POAdapter.PO;
import com.sangsolutions.powerbear.Adapter.POAdapter.POAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PendingPO extends AppCompatActivity {

    POAdapter poAdapter;
    List<PO> list;
    ListView doc_no_lv;
   DatabaseHelper helper;
   TextView title;
   ImageView img_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_p_o);

        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        title.setText("Select customer");
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PendingPO.this,Home.class));
                finishAffinity();
            }
        });

        list = new ArrayList<>();
        poAdapter = new POAdapter(list,this);
        helper = new DatabaseHelper(this);
        doc_no_lv = findViewById(R.id.doc_no_lv);
        Cursor cursor = helper.GetDocNoForPO();
        list.clear();
        if(cursor!=null){
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(new PO(cursor.getString(cursor.getColumnIndex("DocNo")),
                        Tools.ConvertDate(cursor.getString(cursor.getColumnIndex("DocDate"))),cursor.getString(cursor.getColumnIndex("Cusomer"))));
                cursor.moveToNext();
                if(cursor.getCount()==i+1){
                    doc_no_lv.setAdapter(poAdapter);
                }
            }
        }

        doc_no_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PendingPO.this, GoodsReceipt.class);
                intent.putExtra("DocNo",parent.getItemAtPosition(position).toString());
                intent.putExtra("EditMode",false);
                startActivity(intent);

                Toast.makeText(PendingPO.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}