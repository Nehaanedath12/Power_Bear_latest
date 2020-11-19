package com.sangsolutions.powerbear.Services;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.powerbear.Commons;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.GoodReceiptHeader;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.ScheduleJob;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostGoodsReceipt2 extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    HashMap<String,String> map;
    Cursor cursor;
    String sDeviceId="";
    List<GoodReceiptHeader> list;
    int ReceiptCount = 0,successCounter=0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private void UploadGoodsReceipt() {

        JSONObject mainJsonObject = new JSONObject();
        Log.d("count", String.valueOf(ReceiptCount));
            try {
                if (ReceiptCount < list.size()) {
                    Log.d("index",ReceiptCount+" "+list.size());
                    mainJsonObject.put("sVoucherNo", list.get(ReceiptCount).getDocNo());
                    mainJsonObject.put("iDocDate", list.get(ReceiptCount).getDocDate());
                    mainJsonObject.put("iSupplier", Integer.parseInt(list.get(ReceiptCount).getiSupplier()));
                    mainJsonObject.put("iUser", list.get(ReceiptCount).getiUser());
                    mainJsonObject.put("sDeviceId", sDeviceId);
                    mainJsonObject.put("sNarration", list.get(ReceiptCount).getsNarration());
                    mainJsonObject.put("iProcessDate", list.get(ReceiptCount).getdProcessedDate());

                    List<File> file = new ArrayList<>();
                    Cursor cursor2 = helper.GetGoodsBodyData(list.get(ReceiptCount).getDocNo());
                    if (cursor2 != null && cursor2.moveToFirst()) {

                        JSONArray jsonArray = new JSONArray();
                        String SEPARATOR = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < cursor2.getCount(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("sPoNo", cursor2.getString(cursor2.getColumnIndex("sPONo")));
                            jsonObject.put("iProduct", cursor2.getInt(cursor2.getColumnIndex("iProduct")));
                            jsonObject.put("iWarehouse", cursor2.getInt(cursor2.getColumnIndex("iWarehouse")));
                            jsonObject.put("nQty", cursor2.getInt(cursor2.getColumnIndex("fQty")));
                            jsonObject.put("sUnit", cursor2.getString(cursor2.getColumnIndex("Unit")));
                            jsonObject.put("sRemarks", cursor2.getString(cursor2.getColumnIndex("sRemarks")));
                            jsonObject.put("nMQty", cursor2.getInt(cursor2.getColumnIndex("fMinorDamageQty")));
                            jsonObject.put("sMAttatchMent", Tools.getFileList(cursor2.getString(cursor2.getColumnIndex("sMinorAttachment"))));
                            jsonObject.put("iMRemarks", cursor2.getInt(cursor2.getColumnIndex("iMinorId")));
                            jsonObject.put("sMRemarks", cursor2.getString(cursor2.getColumnIndex("sMinorRemarks")));
                            jsonObject.put("nDQty", cursor2.getInt(cursor2.getColumnIndex("fDamagedQty")));
                            jsonObject.put("sDAttatchMent", Tools.getFileList(cursor2.getString(cursor2.getColumnIndex("sDamagedAttachment"))));
                            jsonObject.put("iDRemarks", cursor2.getInt(cursor2.getColumnIndex("iDamagedId")));
                            jsonObject.put("sDRemarks", cursor2.getString(cursor2.getColumnIndex("sDamagedRemarks")));
                            jsonArray.put(jsonObject);


                            String minorFilePaths = cursor2.getString(cursor2.getColumnIndex("sMinorAttachment"));
                            String damagedFilePaths = cursor2.getString(cursor2.getColumnIndex("sDamagedAttachment"));

                            String mainFilePaths = "";
                            if (!minorFilePaths.isEmpty() || !damagedFilePaths.isEmpty()) {
                                if (minorFilePaths.isEmpty() && !damagedFilePaths.isEmpty()) {
                                    mainFilePaths = damagedFilePaths;
                                }

                                if (!minorFilePaths.isEmpty() && damagedFilePaths.isEmpty()) {
                                    mainFilePaths = minorFilePaths;
                                }

                                if (!minorFilePaths.isEmpty() && !damagedFilePaths.isEmpty()) {
                                    mainFilePaths = minorFilePaths + "," + damagedFilePaths;
                                }
                                stringBuilder.append(SEPARATOR);
                                stringBuilder.append(mainFilePaths);
                                stringBuilder.append(",");


                            }
                            SEPARATOR = "";
                            cursor2.moveToNext();

                        }


                        mainJsonObject.put("GoodsRBody", jsonArray);

                        List<String> filePathList = Arrays.asList(stringBuilder.toString().split(","));


                        for (int i = 0; i < filePathList.size(); i++) {
                            File file1 = new File(filePathList.get(i));
                            if (file1.exists()) {
                                file.add(file1);
                            }
                        }


                        UploadData(mainJsonObject, file);

                    }

                }else {
                    Log.d("PostGoods",successCounter+":"+list.size());
                    if(successCounter==list.size()) {
                        editor.putString(Commons.GOODS_RECEIPT_FINISHED,"true").apply();
                    }else if(successCounter<list.size()){
                        editor.putString(Commons.GOODS_RECEIPT_FINISHED,"error").apply();
                        Toast.makeText(this, "GRN Sync exited with an error!", Toast.LENGTH_SHORT).show();
                    }
                   onStopJob(params);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

    }



    private void UploadData(final JSONObject mainJsonObject, List<File> files) {

       AndroidNetworking.upload("http://" + new Tools().getIP(PostGoodsReceipt2.this) + URLs.PostGR)
             .addMultipartParameter("json_content",mainJsonObject.toString())
                .setContentType("multipart/form-data")
                .addMultipartFileList("file",files)
                .setPriority(Priority.IMMEDIATE)
                .build()
               .setUploadProgressListener((bytesUploaded, totalBytes) ->
                       Log.d("Uploaded","Byte:"+totalBytes+":"+bytesUploaded))
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data",response);
                        if(response.contains("Created")){
                             try {
                                if (helper.deleteGoodsBodyItem(mainJsonObject.getString("sVoucherNo"))) {
                                    helper.DeleteGoodsHeaderItem(mainJsonObject.getString("sVoucherNo"));
                                    Log.d("goods", "deleted!");
                                    successCounter++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ReceiptCount++;
                        UploadGoodsReceipt();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error",anError.getErrorDetail());
                        ReceiptCount++;
                        UploadGoodsReceipt();

                    }
                });





   /*     try {
            AndroidNetworking.post("http://" + new Tools().getIP(PostGoodsReceipt2.this) + URLs.PostGR)
                    .addJSONObjectBody(mainJsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (helper.deleteGoodsBodyItem(mainJsonObject.getString("DocNo"))) {
                                    helper.deleteGoodsHeaderItem(mainJsonObject.getString("DocNo"));
                                    Log.d("goods", "deleted!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("data", response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error", anError.getErrorDetail());
                        }
                    });
            if (ReceiptCount + 1 == list.size()) {
                return;
            }
            ReceiptCount++;
            UploadGoodsReceipt();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }


    private void InitializeHeader(Cursor cursor){
        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(new GoodReceiptHeader(
                        cursor.getString(cursor.getColumnIndex("DocNo")),
                        cursor.getString(cursor.getColumnIndex("DocDate")),
                        cursor.getString(cursor.getColumnIndex("dProcessedDate")),
                        cursor.getString(cursor.getColumnIndex("iSupplier")),
                        cursor.getString(cursor.getColumnIndex("iUser")),
                        cursor.getString(cursor.getColumnIndex("sPONo")),
                        cursor.getString(cursor.getColumnIndex("sNarration"))
                ));
                cursor.moveToNext();

                if (cursor.getCount() == i + 1) {
                    cursor.close();
                    UploadGoodsReceipt();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    @Override
    public boolean onStartJob(JobParameters params) {
        sDeviceId = Tools.getDeviceId(getApplicationContext());
        helper = new DatabaseHelper(this);
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Commons.GOODS_RECEIPT_FINISHED,"init").apply();
        this.params = params;
        list = new ArrayList<>();
         cursor = helper.GetAllGoodsHeader();
        if(cursor!=null&&cursor.moveToFirst()) {
            InitializeHeader(cursor);
        }else {
            editor.putString(Commons.GOODS_RECEIPT_FINISHED,"false").apply();
            onStopJob(params);
        }
        map = new HashMap<>();

        return true;
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {  jobFinished(jobParameters,false);
        PublicData.Syncing = true;
        if(!Objects.equals(preferences.getString(Commons.GOODS_RECEIPT_FINISHED, "false"), "error"))
        new ScheduleJob().SyncDeliveryNote2(this);
        return true;
    }
}
