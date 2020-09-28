package com.sangsolutions.powerbear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
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

}
