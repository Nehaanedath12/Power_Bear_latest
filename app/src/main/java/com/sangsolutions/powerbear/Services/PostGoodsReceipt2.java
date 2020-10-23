package com.sangsolutions.powerbear.Services;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;

import android.os.Build;


import androidx.annotation.RequiresApi;

import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.GoodReceiptHeader;
import com.sangsolutions.powerbear.Tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostGoodsReceipt2 extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    HashMap<String,String> map;
    Cursor cursor;
    String sDeviceId="";
    List<GoodReceiptHeader> list;





    private void InitializeHeader(Cursor cursor){
        cursor.moveToFirst();
        for (int i = 0;i<cursor.getCount();i++){
            list.add(new GoodReceiptHeader(
                    cursor.getString(cursor.getColumnIndex("DocNo")),
                    cursor.getString(cursor.getColumnIndex("DocDate")),
                    cursor.getString(cursor.getColumnIndex("sSupplier")),
                    cursor.getString(cursor.getColumnIndex("sPONo")),
                    cursor.getString(cursor.getColumnIndex("sNarration"))
            ));
            cursor.moveToNext();
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        sDeviceId = Tools.getDeviceId(getApplicationContext());
        helper = new DatabaseHelper(this);
        this.params = params;
         cursor = helper.GetAllGoodsHeader();
        if(cursor!=null&&cursor.moveToFirst()) {
            InitializeHeader(cursor);
        }
        map = new HashMap<>();
        list = new ArrayList<>();
        return true;
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
