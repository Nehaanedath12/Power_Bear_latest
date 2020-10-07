package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.Warehouse;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetWareHouse extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public void GetProduct() {
        AndroidNetworking.get("http://"+new Tools().getIP(GetWareHouse.this)+URLs.GetWarehouse)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        asyncPOST(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                Log.d("error",anError.getErrorDetail());
                    }
                });
    }


    private void asyncPOST(final JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                Warehouse w = new Warehouse();
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Warehouse"));

                    //TODO Change to warehouse

                    if(helper.DeleteWarehouse()) {
                        Log.d("Warehouse","true");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            w.setName(jsonObject.getString("Name"));
                            w.setMasterId(jsonObject.getString("MasterId"));


                            boolean status = helper.InsertWarehouse(w);
                            if (status) {
                                Log.d("Saved ", "Success");
                            } else {
                                Log.d("failed", "error");
                            }



                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                Log.d("Status:", "data synced");
                editor.putBoolean("WarehouseFinished",true).apply();
                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();
        AndroidNetworking.initialize(getApplicationContext());
        this.params = params;
        GetProduct();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
