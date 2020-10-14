package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.fotoapparat.result.Photo;
import io.fotoapparat.result.PhotoResult;

public class Tools {
SharedPreferences preferences;
SharedPreferences.Editor editor;
    public static String ConvertDate(String date){
        String year =  date.substring(0,4);
        String month =  date.substring(4,6);
        String day =  date.substring(6,8);
        return year + "-"+ month + "-" + day;

    }

    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public static String dateFormat(String dateToFormat){
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(Objects.requireNonNull(date));
    }

    public static String dateFormat2(String dateToFormat){
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(Objects.requireNonNull(date));
    }


    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {

        String deviceId;
        try {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("deviceid", deviceId);
        }catch (Exception e){
            e.printStackTrace();
            deviceId="";
        }
        return deviceId;
    }

    public String getIP(Context context){
    preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
    if(preferences!=null){
        return preferences.getString("IP","");
    }
    return "";
    }

    @SuppressWarnings("SameReturnValue")
    public boolean setIP(Context context, String IP){
        preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(editor!=null){
        editor.putString("IP",IP).apply();
        }
        return true;
    }

    public static boolean isValidIP(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches());
    }


    public static String savePhoto(Context context, PhotoResult photoResult) {
        String fileName = "IMG_"+System.currentTimeMillis() + ".jpg";
        File folder = new File(context.getExternalFilesDir(null) + File.separator +"temp");

        if (!folder.exists()) {
            Log.d("folder created:", "" + folder.mkdir());
        }
            File file = new File(folder, fileName);
            photoResult.saveToFile(file);
        return file.getAbsolutePath();
    }

    public int GetPixels(int dp,Context context){
        float px = dp;
        try {
            Resources r = context.getResources();
             px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );
        }catch (Exception e){
            e.printStackTrace();
        }
      return (int)px;
    }


}
