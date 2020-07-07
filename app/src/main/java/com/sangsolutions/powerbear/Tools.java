package com.sangsolutions.powerbear;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tools {

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
        return targetFormat.format(date);
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
        return targetFormat.format(date);
    }


}
