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
import com.sangsolutions.powerbear.AsyncConnection;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.Product;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.URLs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetProductService extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    Product p;
    String response = "";
    AsyncConnection connection;

    private void asyncProduct() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                connection = new AsyncConnection(URLs.GeProducts);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                response = connection.execute();
                try {
                    int dataCount = 0;
                    JSONArray jsonArray = new JSONArray(new JSONObject(response).getString("data"));
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
new ScheduleJob().SyncPendingSOData(getApplicationContext());
                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }







    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        p = new Product();
        asyncProduct();
        this.params = params;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;


    }
}
