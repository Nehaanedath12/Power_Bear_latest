package com.sangsolutions.powerbear.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.powerbear.Commons;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostStockCount extends JobService {

    DatabaseHelper helper;
    List<String> list;
    int upload_list_portion = 0,successCounter=0;
    Cursor cursor;
    String UserId="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    JobParameters params;
    private void vouchersToBeUploaded() {
        try {
            if (upload_list_portion < list.size()) {
                Log.d("list", String.valueOf(upload_list_portion));

                    cursor = helper.GetAllStockCountFromVoucher(list.get(upload_list_portion));
                    if (cursor != null) {
                        GetDataToUpload(cursor);
                    }


            }else {
                Log.d("stockCount",successCounter+":"+list.size());
                if(successCounter==list.size()) {
                    editor.putString(Commons.STOCK_COUNT_FINISHED,"true").apply();
                }else if(successCounter<list.size()){
                    editor.putString(Commons.STOCK_COUNT_FINISHED,"error").apply();
                    Toast.makeText(this, "Stock Count Sync exited with an error!"+successCounter, Toast.LENGTH_SHORT).show();
                }
                onStopJob(params);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadJsonStockCount(final JSONObject jsonObject) {
        AndroidNetworking.post("http://" + new Tools().getIP(PostStockCount.this) + URLs.PostProductStock)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Upload", response);
                        try {
                            if (Integer.parseInt(response) > 0) {
                                helper.DeleteStockCount(jsonObject.getString("iVoucherNo"));
                                successCounter++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        upload_list_portion++;
                        vouchersToBeUploaded();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.toString());
                        upload_list_portion++;
                        vouchersToBeUploaded();
                    }
                });

}



    public void GetDataToUpload(Cursor cursor){

        JSONObject mainJsonObject = new JSONObject();
        if(cursor!=null) {
            try {
                mainJsonObject.put("iVoucherNo", cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                mainJsonObject.put("iDocDate", cursor.getString(cursor.getColumnIndex("dProcessedDate")));
                mainJsonObject.put("iWarehouse", cursor.getString(cursor.getColumnIndex("iWarehouse")));
                mainJsonObject.put("iUser", cursor.getString(cursor.getColumnIndex("iUser")));
                mainJsonObject.put("iProcessDate",cursor.getString(cursor.getColumnIndex("dProcessedDate")));
                if(cursor.getString(cursor.getColumnIndex("sNarration"))==null){
                    mainJsonObject.put("sNarration","");
                }else {
                    mainJsonObject.put("sNarration", cursor.getString(cursor.getColumnIndex("sNarration")));
                }
                mainJsonObject.put("sDeviceId", Tools.getDeviceId(this));



                JSONArray jsonArray = new JSONArray();
                for(int i = 0 ; i<cursor.getCount();i++){
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("iProduct", cursor.getString(cursor.getColumnIndex("iProduct")));
                    jsonObject.put("fQty", cursor.getString(cursor.getColumnIndex("fQty")));
                    jsonObject.put("sUnit", cursor.getString(cursor.getColumnIndex("sUnit")));
                    if(cursor.getString(cursor.getColumnIndex("sRemarks"))==null){
                        jsonObject.put("sRemarks","");
                    }else {
                        jsonObject.put("sRemarks", cursor.getString(cursor.getColumnIndex("sRemarks")));
                    }
                    jsonArray.put(jsonObject);
                    cursor.moveToNext();
                    if(cursor.getCount()==i+1){
                        mainJsonObject.put("stockBody",jsonArray);
                        Log.d("data", String.valueOf(mainJsonObject));
                        uploadJsonStockCount(mainJsonObject);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        list = new ArrayList<>();
        this.params = params;
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Commons.STOCK_COUNT_FINISHED,"init").apply();

        Cursor cursor = helper.GetAllStockCountVoucher();
        UserId = helper.GetUserId();
        if(cursor!=null&&cursor.moveToFirst()) {
           for(int i=0;i<cursor.getCount();i++){
               list.add(cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                cursor.moveToNext();
               if(i+1==cursor.getCount()){
                   vouchersToBeUploaded();
                   cursor.close();
               }
           }
        }else {
            editor.putString(Commons.STOCK_COUNT_FINISHED,"false").apply();
           onStopJob(params);
        }
        return true;
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters,false);
        String Goods="",Delivery="",Stocks="";
        Goods = preferences.getString(Commons.GOODS_RECEIPT_FINISHED,"false");
        Delivery = preferences.getString(Commons.DELIVERY_NOTE_FINISHED,"false");
        Stocks = preferences.getString(Commons.STOCK_COUNT_FINISHED,"false");


        Log.d("GDS",Goods+" "+Delivery+" "+Stocks);

        if(Goods!=null&&Delivery!=null&&Stocks!=null)
        if((Goods.equals("true")||Goods.equals("false"))
           &&(Delivery.equals("true")||Delivery.equals("false"))
           &&(Stocks.equals("true")||Stocks.equals("false"))
        ){
            ScheduleJob scheduleJob = new ScheduleJob();

            scheduleJob.SyncUserData(this);
            scheduleJob.SyncWarehouse(this);
            scheduleJob.SyncPendingSOData(this);
            scheduleJob.SyncPendingPO(this);
            scheduleJob.SyncProductData(this);
            scheduleJob.SyncGoodsReceiptType(this);
            editor.putString(Commons.WAREHOUSE_FINISHED, "false").apply();
            editor.putString(Commons.PENDING_PO_FINISHED, "false").apply();
            editor.putString(Commons.PENDING_SO_FINISHED, "false").apply();
            editor.putString(Commons.PRODUCT_FINISHED, "false").apply();
            editor.putString(Commons.REMARKS_FINISHED,"false").apply();
            editor.putString(Commons.SYNC_DATE, "").apply();
        }
        return true;
    }
}
