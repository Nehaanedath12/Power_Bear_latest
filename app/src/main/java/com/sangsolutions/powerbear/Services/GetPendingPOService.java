package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.PendingSO;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetPendingPOService extends JobService {
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

            @SuppressWarnings("SpellCheckingInspection")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    int dataCount = 0;
                    JSONArray jsonArray = new JSONArray(response.getString("PendingPO"));
                    if(helper.DeleteOldPendingPO()) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            p.setDocNo(jsonObject.getString("DocNo"));
                            p.setDocDate(jsonObject.getString("DocDate"));
                            p.setHeaderId(jsonObject.getString("HeaderId"));
                            p.setSiNo(jsonObject.getString("SINo"));
                            p.setCustomer(jsonObject.getString("Cusomer"));
                            p.setProduct(jsonObject.getString("Product"));
                            p.setQty(jsonObject.getString("Qty"));
                            p.setUnit(jsonObject.getString("unit"));
                            boolean status = helper.InsertPendingPO(p);

                            if(jsonArray.length()==i+1){
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(GetPendingPOService.this, "Pending SO Synced", Toast.LENGTH_SHORT).show();


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
                editor.putBoolean("pendingPOFinished",true).apply();
                editor.putString("syncDate",String.valueOf(DateFormat.format("yyyy-MM-dd", new java.util.Date()))).apply();
                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }

    public void PendingSO() {
        AndroidNetworking.get("http://"+new Tools().getIP(this)+URLs.GetPendingPo)
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
                    }
                });


    }


    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();
        AndroidNetworking.initialize(getApplicationContext());
        p = new PendingSO();
        PendingSO();
        this.params = params;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
