package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sangsolutions.powerbear.AsyncConnection;
import com.sangsolutions.powerbear.Database.DatabaseHelper;

import com.sangsolutions.powerbear.ScheduleJob;
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
public class PostGoodsReceipt extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    int ReceiptCount = 0;
    HashMap<String,String> map;
    String response = "";
    AsyncConnection connection;
    Cursor cursor;


    public void UploadGoodsReceipt(final HashMap<String,String> map){
        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                Date c = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("iVoucherNo",map.get("iVoucherNo")));
                list.add(new BasicNameValuePair("iHeaderId",map.get("iHeaderId")));
                list.add(new BasicNameValuePair("iLinkId",map.get("SiNo")));
                list.add(new BasicNameValuePair("iRowId",map.get("iRowId")));
                list.add(new BasicNameValuePair("iProduct",map.get("iProduct")));
                list.add(new BasicNameValuePair("fQty",map.get("fQty")));
                list.add(new BasicNameValuePair("iStatus",map.get("iStatus")));
                list.add(new BasicNameValuePair("dDate", df.format(c)));
                list.add(new BasicNameValuePair("iUser",helper.GetUserId()));

                connection = new AsyncConnection(list, URLs.PostRecieptNote);


            }

            @Override
            protected Void doInBackground(Void... voids) {


                response = connection.execute();
                if(response.equals("1")) {
                    if (helper.DeleteGoods(map.get("iHeaderId"), map.get("SiNo"))) {
                        Log.d("status change", "true");
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d("Goods:", "data synced"+response);
                syncGoodsReceipt();
            }
        };
        asyncTask.execute();


    }



    public void syncGoodsReceipt(){
        if(cursor!=null) {
            if (cursor.getCount()>ReceiptCount) {
                map.put("iVoucherNo", cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                map.put("iHeaderId", cursor.getString(cursor.getColumnIndex("HeaderId")));
                //map.put("iRowId",cursor.getString(cursor.getColumnIndex("iRowId")));
                map.put("iRowId", String.valueOf(ReceiptCount));
                map.put("iProduct", cursor.getString(cursor.getColumnIndex("Product")));
                map.put("fQty", cursor.getString(cursor.getColumnIndex("Qty")));
                map.put("iStatus", cursor.getString(cursor.getColumnIndex("iStatus")));
                map.put("SiNo", cursor.getString(cursor.getColumnIndex("SiNo")));
                UploadGoodsReceipt(map);
                ReceiptCount++;
                cursor.moveToNext();
                if(cursor.getCount()<=ReceiptCount){
                    jobFinished(params,false);
                    new ScheduleJob().SyncDeliveryNote(getApplicationContext());
                }
            }
        }else {
            jobFinished(params,false);
            new ScheduleJob().SyncDeliveryNote(getApplicationContext());
        }

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        this.params = params;
         cursor = helper.GetGoodsReceipt();
        if(cursor!=null) {
            cursor.moveToFirst();
        }
        map = new HashMap<>();
        syncGoodsReceipt();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
