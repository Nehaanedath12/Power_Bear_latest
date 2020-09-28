package com.sangsolutions.powerbear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
   Handler handler;

    SharedPreferences preferences;

    private AnimationDrawable animationDrawable;
    private ImageView mProgressBar;
   public void LoadPO(){
       Cursor cursor = helper.GetHeaderIdForPO();
       list.clear();
       if(cursor!=null){
           for (int i = 0; i < cursor.getCount(); i++) {
               //noinspection SpellCheckingInspection
               list.add(new PO(cursor.getString(cursor.getColumnIndex("HeaderId")),
                       Tools.ConvertDate(cursor.getString(cursor.getColumnIndex("DocDate"))),cursor.getString(cursor.getColumnIndex("Cusomer"))));
               cursor.moveToNext();
               if(cursor.getCount()==i+1){
                   doc_no_lv.setAdapter(poAdapter);
                   mProgressBar.setVisibility(View.INVISIBLE);
                   animationDrawable.stop();
               }
           }
       }



   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_p_o);

        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        title.setText("Select Vendor");

        preferences = getSharedPreferences("sync",MODE_PRIVATE);

        mProgressBar = findViewById(R.id.main_progress);
        mProgressBar.setBackgroundResource(R.drawable.loading);

        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
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


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if(!preferences.getBoolean("pendingPOFinished",false)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    animationDrawable.start();
                    handler.postDelayed(this, 1000);
                }else {

                    LoadPO();
                     handler.removeCallbacksAndMessages(null);
                }
            }
        };
        handler.postDelayed(r, 1000);



        doc_no_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PendingPO.this, GoodsReceipt.class);
                intent.putExtra("HeaderId",parent.getItemAtPosition(position).toString());
                intent.putExtra("EditMode",false);
                startActivity(intent);

            }

        });

    }
}