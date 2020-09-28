package com.sangsolutions.powerbear.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.User;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetUserService extends JobService {
    JobParameters params;
    DatabaseHelper helper;


    private void asyncPOST(final JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                User u = new User();
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Users"));
                    if(helper.DeleteUser()) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            u.setsId(jsonObject.getString("iId"));
                            u.setsLoginName(jsonObject.getString("sLoginName"));
                            u.setsPassword(jsonObject.getString("sPassword"));

                            boolean status = helper.InsertUsers(u);
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

                new ScheduleJob().SyncProductData(getApplicationContext());

                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }

    public void GetProduct() {
        AndroidNetworking.get("http://"+new Tools().getIP(GetUserService.this)+URLs.GetUser)
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
    @Override
    public boolean onStartJob(JobParameters params) {
        AndroidNetworking.initialize(getApplicationContext());
        helper = new DatabaseHelper(this);
        this.params = params;
        GetProduct();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
