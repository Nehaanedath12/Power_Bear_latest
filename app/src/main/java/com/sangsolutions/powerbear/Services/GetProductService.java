package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.Product;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetProductService extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    Product p;



    public void GetProduct() {
        AndroidNetworking.get("http://"+new Tools().getIP(this)+URLs.GeProducts)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        asyncProduct(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error",anError.getErrorBody());
                    }
                });
    }

    private void asyncProduct(final JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    int dataCount = 0;
                    JSONArray jsonArray = new JSONArray(response.getString("Products"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        p.setMasterId(jsonObject.getString("MasterId"));
                        p.setCode(jsonObject.getString("Code"));
                        p.setName(jsonObject.getString("Name"));
                        p.setBarcode(jsonObject.getString("Barcode"));
                        p.setUnit(jsonObject.getString("unit"));
                        boolean status = helper.InsertProduct(p);
                        if(jsonArray.length()==i+1){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(GetProductService.this, "Product Synced", Toast.LENGTH_SHORT).show();

                                }
                            });
                                }

                        if (status) {
                            dataCount++;
                            Log.d("data", dataCount + " : " + jsonArray.length());
                        } else {
                            Log.d("error", "Data Already added");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }







    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        AndroidNetworking.initialize(getApplicationContext());
        p = new Product();
        GetProduct();
        this.params = params;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;


    }
}
