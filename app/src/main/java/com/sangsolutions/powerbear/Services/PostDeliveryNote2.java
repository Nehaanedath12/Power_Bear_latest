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
import com.sangsolutions.powerbear.Database.DeliveryNoteHeader;
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
public class PostDeliveryNote2 extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    HashMap<String,String> map;
    Cursor cursor;
    String sDeviceId="";
    List<DeliveryNoteHeader> list;
    int DeliveryCount = 0,successCounter=0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    private void UploadGoodsReceipt() {

        JSONObject mainJsonObject = new JSONObject();
        Log.d("count", String.valueOf(DeliveryCount));
        try {
            if (DeliveryCount < list.size()) {
                mainJsonObject.put("sVoucherNo", list.get(DeliveryCount).getsVoucherNo());
                mainJsonObject.put("iDocDate", list.get(DeliveryCount).getsDate());
                mainJsonObject.put("iCustomer", Integer.parseInt(list.get(DeliveryCount).getiCustomer()));
                mainJsonObject.put("iUser", list.get(DeliveryCount).getiUser());
                mainJsonObject.put("sDeviceId", sDeviceId);
                mainJsonObject.put("sNarration", list.get(DeliveryCount).getsNarration());
                mainJsonObject.put("iProcessDate", list.get(DeliveryCount).getdProposedDate());

                List<File> file = new ArrayList<>();
                Cursor cursor2 = helper.GetDeliveryBodyData(list.get(DeliveryCount).getsVoucherNo());

                String mainFilePaths="";

                if (cursor2 != null && cursor2.moveToFirst()) {
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < cursor2.getCount(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sSoNo", cursor2.getString(cursor2.getColumnIndex("sSONo")));
                        jsonObject.put("iProduct", cursor2.getInt(cursor2.getColumnIndex("iProduct")));
                        jsonObject.put("iWarehouse", cursor2.getInt(cursor2.getColumnIndex("iWarehouse")));
                        jsonObject.put("nQty", cursor2.getInt(cursor2.getColumnIndex("fQty")));
                        jsonObject.put("sUnit", cursor2.getString(cursor2.getColumnIndex("Unit")));
                        jsonObject.put("sRemarks", cursor2.getString(cursor2.getColumnIndex("sRemarks")));
                        jsonObject.put("sAttatchMent", Tools.getFileList(cursor2.getString(cursor2.getColumnIndex("sAttachment"))));

                        jsonArray.put(jsonObject);


                        mainFilePaths = cursor2.getString(cursor2.getColumnIndex("sAttachment"));

                        cursor2.moveToNext();

                    }


                    mainJsonObject.put("DeliBody", jsonArray);

                    List<String> filePathList = Arrays.asList(mainFilePaths.split(","));


                    for (int i = 0; i < filePathList.size(); i++) {
                        File file1 = new File(filePathList.get(i));
                        if (file1.exists()) {
                            file.add(file1);
                        }
                    }


                    UploadData(mainJsonObject, file);

                }

            }else {
                Log.d("PostDelivery",successCounter+":"+list.size());
                if(successCounter==list.size()) {
                    editor.putString(Commons.DELIVERY_NOTE_FINISHED,"true").apply();
                }else if(successCounter<list.size()){
                    editor.putString(Commons.DELIVERY_NOTE_FINISHED,"error").apply();
                    Toast.makeText(this, "Delivery note Sync exited with an error!", Toast.LENGTH_SHORT).show();
                }
                onStopJob(params);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }


    private void UploadData(final JSONObject mainJsonObject, List<File> files) {

        AndroidNetworking.upload("http://" + new Tools().getIP(PostDeliveryNote2.this) + URLs.PostDelivery)
                .addMultipartParameter("json_content", mainJsonObject.toString())
                .setContentType("multipart/form-data")
                .addMultipartFileList("file", files)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .setUploadProgressListener((bytesUploaded, totalBytes) ->
                        Log.d("Uploaded", "Byte:" + totalBytes + ":" + bytesUploaded))
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", response);
                        if (response.contains("Created")) {
                            try {
                                if (helper.DeleteDeliveryNoteBodyItem(mainJsonObject.getString("sVoucherNo"))) {
                                    helper.DeleteDeliveryNoteHeaderItem(mainJsonObject.getString("sVoucherNo"));
                                    Log.d("Delivery", "deleted!");
                                    successCounter++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        DeliveryCount++;
                        UploadGoodsReceipt();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        DeliveryCount++;
                        UploadGoodsReceipt();

                    }
                });

    }
    private void InitializeHeader(Cursor cursor){
        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(new DeliveryNoteHeader(
                        cursor.getString(cursor.getColumnIndex("DocNo")),
                        cursor.getString(cursor.getColumnIndex("sDate")),
                        cursor.getString(cursor.getColumnIndex("dProcessedDate")),
                        cursor.getString(cursor.getColumnIndex("iUser")),
                        cursor.getString(cursor.getColumnIndex("sSONos")),
                        cursor.getString(cursor.getColumnIndex("iCustomerRef")),
                        cursor.getString(cursor.getColumnIndex("sNarration"))
                ));
                cursor.moveToNext();

                if (cursor.getCount() == i + 1) {
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
        editor.putString(Commons.DELIVERY_NOTE_FINISHED,"init").apply();
        this.params = params;
        list = new ArrayList<>();
        cursor = helper.GetAllDeliveryHeader();
        if(cursor!=null&&cursor.moveToFirst()) {
            InitializeHeader(cursor);
        }else {
            editor.putString(Commons.DELIVERY_NOTE_FINISHED,"false").apply();
            onStopJob(params);
        }
        map = new HashMap<>();

        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobFinished(params,false);
        if(!Objects.equals(preferences.getString(Commons.DELIVERY_NOTE_FINISHED, "false"), "error"))
            new ScheduleJob().SyncGoodsWithoutPO(this);
        return true;
    }
}
