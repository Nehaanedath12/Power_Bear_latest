package com.sangsolutions.powerbear;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sangsolutions.powerbear.Services.GetPendingPOService;
import com.sangsolutions.powerbear.Services.GetPendingSoService;
import com.sangsolutions.powerbear.Services.GetProductService;
import com.sangsolutions.powerbear.Services.GetUserService;
import com.sangsolutions.powerbear.Services.GetWareHouse;
import com.sangsolutions.powerbear.Services.PostDeliveryNote;
import com.sangsolutions.powerbear.Services.PostGoodsReceipt;
import com.sangsolutions.powerbear.Services.PostStockCount;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScheduleJob {
 private static int MY_BACKGROUND_JOB = 0;


   public void SyncProductData(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, GetProductService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }


    public void SyncPendingSOData(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, GetPendingSoService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
    public void SyncUserData(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, GetUserService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    public void SyncWarehouse(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, GetWareHouse.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
    public void SyncPendingPO(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, GetPendingPOService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    public void SyncDeliveryNote(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, PostDeliveryNote.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    public void SyncGoodsReceipt(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, PostGoodsReceipt.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
    public void SyncStockCount(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                MY_BACKGROUND_JOB,
                new ComponentName(context, PostStockCount.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
}
