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
    Cursor cursor;
    String sDeviceId="";

/*    public void UploadStockCount(final HashMap<String,String> map){
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
                list.add(new BasicNameValuePair("sRemarks",map.get("sNarration")));
                list.add(new BasicNameValuePair("iUser",helper.GetUserId()));
                list.add(new BasicNameValuePair("dProcessedDate",map.get("dProcessedDate")));
                list.add(new BasicNameValuePair("iStatus",map.get("iStatus")));
                list.add(new BasicNameValuePair("sDeviceId", sDeviceId));

                connection = new AsyncConnection(list, "http://"+new Tools().getIP(PostStockCount.this)+URLs.PostProductStock);


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

    }*/


public void uploadJsonStockCount(JSONObject jsonObject){

    AndroidNetworking.post("http://"+new Tools().getIP(PostStockCount.this)+URLs.PostProductStock)
            .addJSONObjectBody(jsonObject)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsString(new StringRequestListener() {
                @Override
                public void onResponse(String response) {

            Log.d("Upload",response);

                }

                @Override
                public void onError(ANError anError) {

                    Log.d("error", anError.toString());
                }
            });

}



    public void GetDataToUpload(){

        JSONObject mainJsonObject = new JSONObject();
        if(cursor!=null) {
            try {
                mainJsonObject.put("iVoucherNo", cursor.getString(cursor.getColumnIndex("iVoucherNo")));
                mainJsonObject.put("iDocDate", Tools.dateFormat(cursor.getString(cursor.getColumnIndex("dProcessedDate"))));
                mainJsonObject.put("iWarehouse", cursor.getString(cursor.getColumnIndex("iWarehouse")));
                mainJsonObject.put("iUser", helper.GetUserId());
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
                        mainJsonObject.put("sRemarks","");
                    }else {
                        mainJsonObject.put("sRemarks", cursor.getString(cursor.getColumnIndex("sRemarks")));
                    }
                    jsonArray.put(jsonObject);
                    cursor.moveToNext();
                    if(cursor.getCount()==i+1){
                        mainJsonObject.put("stockBody",jsonObject);
                        uploadJsonStockCount(mainJsonObject);
                        Log.d("upload",mainJsonObject.toString());
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
        this.params = params;
         cursor = helper.GetAllStockCount();
        if(cursor!=null) {
            cursor.moveToFirst();
        }
        GetDataToUpload();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
