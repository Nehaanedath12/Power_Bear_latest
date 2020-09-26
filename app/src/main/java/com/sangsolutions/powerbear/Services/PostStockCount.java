package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EdgeEffect;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.powerbear.AsyncConnection;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostStockCount extends JobService {

    JobParameters params;
    DatabaseHelper helper;
    String sDeviceId="";
    List<String> list;
    int upload_list_portion = 0;
    Cursor cursor;
    String UserId="";
    private void vouchersToBeUploaded() {
        try {
            if (upload_list_portion < list.size()) {
                Log.d("list", String.valueOf(upload_list_portion));
                cursor = helper.GetAllStockCountFromVoucher(list.get(upload_list_portion));
                upload_list_portion++;
                if (cursor != null) {
                    GetDataToUpload(cursor);
                }

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
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d("error", anError.toString());
                    }
                });
        vouchersToBeUploaded();
}



    public void GetDataToUpload(Cursor cursor){

        JSONObject mainJsonObject = new JSONObject();
        if(cursor!=null) {
            try {
                mainJsonObject.put("iVoucherNo", cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                mainJsonObject.put("iDocDate", cursor.getString(cursor.getColumnIndex("dProcessedDate")));
                mainJsonObject.put("iWarehouse", cursor.getString(cursor.getColumnIndex("iWarehouse")));
                mainJsonObject.put("iUser", UserId);
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
        sDeviceId = Tools.getDeviceId(getApplicationContext());
        helper = new DatabaseHelper(this);
        list = new ArrayList<>();
        this.params = params;
        Cursor cursor = helper.GetAllStockCountVoucher();
        UserId = helper.GetUserId();
        if(cursor!=null) {
            cursor.moveToFirst();
           for(int i=0;i<cursor.getCount();i++){
               list.add(cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                cursor.moveToNext();
               if(i+1==cursor.getCount()){
                   vouchersToBeUploaded();
                   cursor.close();
               }
           }
        }
        return true;
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
