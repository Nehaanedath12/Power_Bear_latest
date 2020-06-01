package com.sangsolutions.powerbear;

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

}
