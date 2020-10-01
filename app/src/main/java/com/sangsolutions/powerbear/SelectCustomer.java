package com.sangsolutions.powerbear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sangsolutions.powerbear.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class SelectCustomer extends AppCompatActivity {

DatabaseHelper helper;
List<DONo> list;
SOAdapter adapter;
ListView doc_no_lv;
TextView title;
ImageView img_home;
Handler handler;
    private AnimationDrawable animationDrawable;
    private ImageView mProgressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public void LoadCustomer(){
        Cursor cursor = helper.GetDocNo();
        list.clear();
        if(cursor!=null){
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(new DONo(cursor.getString(cursor.getColumnIndex("DocNo")),
                        Tools.ConvertDate(cursor.getString(cursor.getColumnIndex("DocDate"))),
                        cursor.getString(cursor.getColumnIndex("Cusomer")),
                        cursor.getString(cursor.getColumnIndex("HeaderId"))));
                cursor.moveToNext();
                if(cursor.getCount()==i+1){
                    doc_no_lv.setAdapter(adapter);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    animationDrawable.stop();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer);
        helper = new DatabaseHelper(this);
        doc_no_lv = findViewById(R.id.doc_no_lv);
        list= new ArrayList<>();
        adapter = new SOAdapter(list);
        img_home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        title.setText("Select Customer");

        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();

        mProgressBar = findViewById(R.id.main_progress);
        mProgressBar.setBackgroundResource(R.drawable.loading);

        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if(!preferences.getBoolean("pendingSOFinished",false)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    animationDrawable.start();
                    handler.postDelayed(this, 1000);
                }else {
                    LoadCustomer();
                    handler.removeCallbacksAndMessages(null);
                }

            }
        };
        handler.postDelayed(r, 1000);

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectCustomer.this,Home.class));
                finishAffinity();
            }
        });



        doc_no_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SelectCustomer.this, AddDeliveryNote.class);
                    intent.putExtra("HeaderId",parent.getItemAtPosition(position).toString());
                    intent.putExtra("EditMode",false);
                    startActivity(intent);

                }

        });


    }
    @SuppressWarnings("SpellCheckingInspection")
    private static class DONo {
        String DocNo, DocDate, Cusomer,HeaderId;

        public DONo(String docNo, String docDate, String cusomer,String HeaderId) {
            this.DocNo = docNo;
            this.DocDate = docDate;
            this.Cusomer = cusomer;
            this.HeaderId = HeaderId;
        }

        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
        }

        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
        }

        public String getCusomer() {
            return Cusomer;
        }

        public void setCusomer(String cusomer) {
            Cusomer = cusomer;
        }

        public String getHeaderId() {
            return HeaderId;
        }

        public void setHeaderId(String headerId) {
            HeaderId = headerId;
        }
    }

    private class SOAdapter extends BaseAdapter{
        final List<DONo> list;

        public SOAdapter(List<DONo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).getHeaderId();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(SelectCustomer.this).inflate(R.layout.dno_item,parent,false);
            TextView doc_no = view.findViewById(R.id.dno);
            TextView date = view.findViewById(R.id.ll_1);
            TextView customer = view.findViewById(R.id.customer);
            RelativeLayout lyt_parent= view.findViewById(R.id.lay_parent);
            doc_no.setText("Doc No : "+ list.get(position).DocNo);
            date.setText(Tools.dateFormat2(list.get(position).DocDate));
            customer.setText(list.get(position).Cusomer);
            if (position % 2 == 0) {
                lyt_parent.setBackgroundColor(Color.rgb(234, 234, 234));
            } else {

                lyt_parent.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            return view ;
        }
    }

}

