package com.sangsolutions.powerbear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.sangsolutions.powerbear.Adapter.POAdapter.PO;
import com.sangsolutions.powerbear.Adapter.POAdapter.POAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingPO extends AppCompatActivity {

    AsyncConnection connection;
    POAdapter poAdapter;
    String response = "";
    ProgressDialog pd;
    List<PO> list;
    RecyclerView rv_po;

    private void asyncPOST() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(PendingPO.this);
                pd.setMessage("please wait..");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                response = connection.execute();
                try {
                    JSONArray jsonArray = new JSONArray(new JSONObject(response).getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String DocNo = jsonObject.getString("DocNo");
                        String DocDate = jsonObject.getString("DocDate");
                        String Cusomer = jsonObject.getString("Cusomer");
                        String HeaderId = jsonObject.getString("HeaderId");
                        String SINo = jsonObject.getString("SINo");
                        String Product = jsonObject.getString("Product");
                        String Qty = jsonObject.getString("Qty");
                        String unit = jsonObject.getString("unit");


                        list.add(new PO(DocNo,DocDate,Cusomer,HeaderId,SINo,Product,Qty,unit));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (pd.isShowing()) {
                    pd.dismiss();

                }
                if (list.size() > 0) {
                    rv_po.setAdapter(poAdapter);
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (list.size() <= 0)
            asyncPOST();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_p_o);
        connection = new AsyncConnection(URLs.GetPendingPO);
        list = new ArrayList<>();
        poAdapter = new POAdapter(this,list);
        rv_po = findViewById(R.id.recyclerView);
        rv_po.setLayoutManager(new LinearLayoutManager(this));

        poAdapter.setOnClickListener(new POAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, PO po, int pos) {

            }
        });

    }
}