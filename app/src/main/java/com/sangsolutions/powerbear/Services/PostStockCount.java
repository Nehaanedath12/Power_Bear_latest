package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sangsolutions.powerbear.AsyncConnection;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostStockCount extends JobService {

    JobParameters params;
    DatabaseHelper helper;
    HashMap<String,String> map;
    Cursor cursor;
    String response = "";
    AsyncConnection connection;
    int stockCount  = 0;
    String sDeviceId="";

    public void UploadStockCount(final HashMap<String,String> map){
        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                Date c = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("iVoucherNo",map.get("iVoucherNo")));
                list.add(new BasicNameValuePair("dDate", df.format(c)));
                list.add(new BasicNameValuePair("iWarehouse",map.get("iWarehouse")));
                list.add(new BasicNameValuePair("iProduct",map.get("iProduct")));
                list.add(new BasicNameValuePair("fQty",map.get("fQty")));
                list.add(new BasicNameValuePair("sUnit",map.get("sUnit")));
                list.add(new BasicNameValuePair("sRemarks",map.get("sRemarks")));
                list.add(new BasicNameValuePair("iUser",helper.GetUserId()));
                list.add(new BasicNameValuePair("dProcessedDate",map.get("dProcessedDate")));
                list.add(new BasicNameValuePair("iStatus",map.get("iStatus")));
                list.add(new BasicNameValuePair("sDeviceId", sDeviceId));

                connection = new AsyncConnection(list, "http://"+new Tools().getIP(PostStockCount.this)+URLs.PostStockCount);


            }

            @Override
            protected Void doInBackground(Void... voids) {


                response = connection.execute();
                if(response.equals("1"))
                if(helper.DeleteStockCount(map.get("iVoucherNo"),map.get("iProduct"))){
                  Log.d("status change","true");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d("Stock:", "data synced"+response);
                syncStockCount();
            }
        };
        asyncTask.execute();


    }



    public void syncStockCount(){

        if(cursor!=null) {
            if (cursor.getCount()>stockCount) {
                map.put("iVoucherNo", cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                map.put("iWarehouse", cursor.getString(cursor.getColumnIndex("iWarehouse")));
                map.put("iProduct", cursor.getString(cursor.getColumnIndex("iProduct")));
                map.put("fQty", cursor.getString(cursor.getColumnIndex("fQty")));
                map.put("sUnit", cursor.getString(cursor.getColumnIndex("sUnit")));
                map.put("sRemarks", cursor.getString(cursor.getColumnIndex("sRemarks")));
                map.put("dProcessedDate", cursor.getString(cursor.getColumnIndex("dProcessedDate")));
                map.put("iStatus", cursor.getString(cursor.getColumnIndex("iStatus")));

                UploadStockCount(map);
                cursor.moveToNext();
                stockCount++;
                if(cursor.getCount()<=stockCount){
                    jobFinished(params,false);
                    Toast.makeText(this, "Stock count uploaded!", Toast.LENGTH_SHORT).show();

                }
            }
        }else {
            jobFinished(params,false);
        }

    }





    @Override
    public boolean onStartJob(JobParameters params) {
        sDeviceId = Tools.getDeviceId(getApplicationContext());
        helper = new DatabaseHelper(this);
        this.params = params;
         cursor = helper.GetAllStockCount();
        if(cursor!=null) {
            cursor.moveToFirst();
        }
        map = new HashMap<>();
        syncStockCount();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
