package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
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
import com.sangsolutions.powerbear.Commons;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.PendingSO;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

@SuppressWarnings("ALL")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetPendingSoService extends JobService {
    DatabaseHelper helper;
    PendingSO p;
    JobParameters params;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private void asyncPendingSO(final JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    int dataCount = 0;
                    JSONArray jsonArray = new JSONArray(response.getString("PendingSO"));
                    if(helper.DeleteOldPendingSO()) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            p.setDocNo(jsonObject.getString("DocNo"));
                            p.setDocDate(jsonObject.getString("DocDate"));
                            p.setHeaderId(jsonObject.getString("HeaderId"));
                            p.setSiNo(jsonObject.getString("SINo"));
                            p.setCustomer(jsonObject.getString("Customer"));
                            p.setiCustomer(jsonObject.getString("iCustomer"));
                            p.setProduct(jsonObject.getString("Product"));
                            p.setQty(jsonObject.getString("Qty"));
                            p.setUnit(jsonObject.getString("unit"));
                            boolean status = helper.InsertPendingSO(p);

                            if(jsonArray.length()==i+1){
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        jobFinished(params,false);

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
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                editor.putString(Commons.PENDING_SO_FINISHED,"true").apply();
            }
        };
        asyncTask.execute();
    }

    public void GetProduct() {
        AndroidNetworking.get("http://"+new Tools().getIP(GetPendingSoService.this)+URLs.GetPendingSo)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        asyncPendingSO(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error",anError.getErrorDetail());
                        editor.putString(Commons.PENDING_SO_FINISHED,"error").apply();
                    }
                });


    }


    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();
        AndroidNetworking.initialize(getApplicationContext());
        p = new PendingSO();
        GetProduct();
        this.params = params;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobFinished(params,false);
        return true;
    }
}
