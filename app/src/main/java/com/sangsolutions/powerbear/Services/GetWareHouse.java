package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.sangsolutions.powerbear.AsyncConnection;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.Warehouse;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetWareHouse extends JobService {
    JobParameters params;
    String response;
    DatabaseHelper helper;
    AsyncConnection connection = new AsyncConnection(URLs.GetWarehouse);


    private void asyncPOST() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                response = connection.execute();
                Warehouse w = new Warehouse();
                try {
                    JSONArray jsonArray = new JSONArray(new JSONObject(response).getString("data"));

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


                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        this.params = params;
        asyncPOST();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
