package com.sangsolutions.powerbear.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.sangsolutions.powerbear.R;

public class MaintenanceService extends Service {
    public static final String CHANNEL_ID = "MaintenanceForegroundService";
    int progress = 0,maxProgress=1000*10;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
      /*Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);

         builder.setContentTitle("Maintenance!");
         builder.setContentText("Maintenance Service is running..");
         builder.setSmallIcon(R.drawable.logo);
         builder.setProgress(maxProgress/1000,progress,true);
         // builder.setContentIntent(pendingIntent);


        new CountDownTimer(maxProgress, 1000) {

            public void onTick(long millisUntilFinished) {
                progress = (int) millisUntilFinished/1000;
            }

            public void onFinish() {
               stopForeground(true);
            }
        }.start();

        Notification notification = builder.build();
        startForeground(1, notification);
        return START_NOT_STICKY;
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
