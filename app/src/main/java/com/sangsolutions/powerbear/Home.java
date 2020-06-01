package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {
ImageButton sync_btn,delivery_btn,stock_count;
DatabaseHelper helper;
HashMap<String,String> map;
int count = 0;

String response = "";
ProgressDialog pd;
    AsyncConnection connection;
public void UploadDeliveryNote(final HashMap<String,String> map){
    @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
            Date c = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("iHeaderId",map.get("iHeaderId")));
            list.add(new BasicNameValuePair("iLinkId",map.get("SiNo")));
            list.add(new BasicNameValuePair("iRowId","0"));
            list.add(new BasicNameValuePair("iProduct",map.get("iProduct")));
            list.add(new BasicNameValuePair("fQty",map.get("fQty")));
            list.add(new BasicNameValuePair("iStatus",map.get("iStatus")));
            list.add(new BasicNameValuePair("dDate", df.format(c)));
            list.add(new BasicNameValuePair("iUser",helper.GetUserId()));

            connection = new AsyncConnection(list,URLs.PostGoodsReceiptNote);

            pd=new ProgressDialog(Home.this);
            pd.setMessage("please wait..");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            response = connection.execute();

          /*  try {
                JSONArray jsonArray = new JSONArray(new JSONObject(response).getString("data"));
                if(helper.DeleteUser()) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        u.setsId(jsonObject.getString("iId"));
                        u.setsLoginName(jsonObject.getString("sLoginName"));
                        u.setsPassword(jsonObject.getString("sPassword"));

                        boolean status = helper.InsertUsers(u);
                        if (status) {

                            Log.d("Saved ", "Success");
                        } else {
                            Log.d("failed", "error");
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(pd.isShowing()) {
                pd.dismiss();

            }
            Log.d("Status:", "data synced"+response);

        }
    };
    asyncTask.execute();


}

public void syncDeliveryNote(){
    Cursor cursor = helper.GetDeliveryNote();
    if(cursor!=null) {
        cursor.moveToFirst();
        if (cursor.getCount()>count) {
            map.put("iHeaderId", cursor.getString(cursor.getColumnIndex("HeaderId")));
            //map.put("iRowId",cursor.getString(cursor.getColumnIndex("iRowId")));
            map.put("iRowId", "0");
            map.put("iProduct", cursor.getString(cursor.getColumnIndex("Product")));
            map.put("fQty", cursor.getString(cursor.getColumnIndex("Qty")));
            map.put("iStatus", cursor.getString(cursor.getColumnIndex("iStatus")));
            map.put("SiNo", cursor.getString(cursor.getColumnIndex("SiNo")));
            UploadDeliveryNote(map);
            count++;
            cursor.moveToNext();
        }
    }

}


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sync_btn= findViewById(R.id.sync);
        delivery_btn= findViewById(R.id.delivery);
        stock_count = findViewById(R.id.stock_count);
        helper = new DatabaseHelper(this);
        map = new HashMap<>();
    stock_count.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Home.this,StockCount.class));
        }
    });

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncDeliveryNote();
            }
        });

        delivery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,SelectDN.class);
                startActivity(intent);
            }
        });
    }
}
