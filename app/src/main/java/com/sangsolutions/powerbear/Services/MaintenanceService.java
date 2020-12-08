package com.sangsolutions.powerbear.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.FileUtils;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.google.gson.internal.$Gson$Preconditions;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.R;

import java.io.File;

public class MaintenanceService extends Service {
    public static final String CHANNEL_ID = "MaintenanceForegroundService";
    int progress = 0, maxProgress = 0;
    DatabaseHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        helper = new DatabaseHelper(this);
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);

         builder.setContentTitle("Maintenance!");
         builder.setContentText("Maintenance Service is running..");
         builder.setSmallIcon(R.drawable.logo);
         builder.setProgress(maxProgress/1000,progress,true);


        /*new CountDownTimer(maxProgress, 1000) {

            public void onTick(long millisUntilFinished) {
                progress = (int) millisUntilFinished/1000;
            }

            public void onFinish() {
               stopForeground(true);
            }
        }.start();*/

       /* try {
            Cursor cursor = helper.GetAllGoodsWithoutPOBody();
            Cursor cursor1 = helper.GetGoodsBodyData();
            Cursor cursor2 = helper.GetDeliveryBodyData();
            if ((cursor==null&&cursor1==null&&cursor2==null)||!(cursor.moveToFirst()||cursor1.moveToFirst()||cursor2.moveToFirst())){

                if(hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    File folder = new File(this.getExternalFilesDir(null) + File.separator +"temp");

                    if(folder.exists()) {

                        if (folder.isDirectory())
                            for (File child : folder.listFiles()){
                                if (child.delete()) {
                                    Log.d("deleted", child.getName());
                                }

                    }
                        stopForeground(true);
                        stopSelf();
                    }
                }
                }else {
                stopForeground(true);
                Toast.makeText(this, "Not empty", Toast.LENGTH_SHORT).show();
                stopSelf();
            }

        }catch (Exception e){
            stopForeground(true);
            stopSelf();
            e.printStackTrace();
        }*/
        Notification notification = builder.build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Maintenance Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
