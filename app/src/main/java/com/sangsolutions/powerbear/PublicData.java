package com.sangsolutions.powerbear;

public class PublicData {

    public static String narration="";
    public static String date="";
    public static String warehouse="";
    public static String supplier="";


    public static String image_minor="";
    public static String image_damaged="";
    public static String POs="";
    public static String voucher="";
    public static boolean Syncing = false;
    public static String contactPerson = "";

    public static void clearDataIgnoreHeader(){
        warehouse = "";
        image_minor ="";
        image_damaged="";
    }

    public static void clearData(){
        narration = "";
        date = "";
        warehouse = "";
        image_minor ="";
        image_damaged="";
        POs = "";
        supplier = "";
        voucher ="";
        contactPerson = "";
    }
}
