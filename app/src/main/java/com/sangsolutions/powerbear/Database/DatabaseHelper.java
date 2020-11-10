package com.sangsolutions.powerbear.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class DatabaseHelper extends SQLiteOpenHelper {

    final Context context;

    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "PowerBear.db";
    private static final String TABLE_PRODUCT = "tbl_Product";
    private static final String TABLE_PENDING_SO = "tbl_PendingSO";
    private static final String TABLE_USER = "user";
    private static final String TABLE_CURRENT_LOGIN = "current_login";
    private static final String TABLE_WAREHOUSE = "tbl_warehouse";
    private static final String TABLE_STOCK_COUNT = "tbl_StockCount";
    private static final String TABLE_PENDING_PO = "tbl_PendingPO";
    private static final String TABLE_GOODS_RECEIPT_HEADER = "tbl_GoodsReceiptHeader";
    private static final String TABLE_GOODS_RECEIPT_BODY = "tbl_GoodsReceiptBody";
    private static final String TABLE_GOOD_RECEIPT_TYPE = "tbl_GoodsReceiptType";
    private static final String TABLE_DELIVERY_NOTE_HEADER = "tbl_DeliveryNoteHeader";
    private static final String TABLE_DELIVERY_NOTE_BODY = "tbl_DeliveryNoteBody";

    //Product
    private static final String MASTER_ID = "MasterId";
    private static final String NAME = "Name";
    private static final String CODE =  "Code";
    private static final String BARCODE = "Barcode";
    private static final String UNIT = "Unit";

    //PendingSO
    private static final String DOC_NO = "DocNo";
    private static final String DOC_DATE = "DocDate";
    private static final String HEADER_ID = "HeaderId";
    private static final String SI_NO = "SiNo";
    private static final String CUSTOMER = "Cusomer";//spelling is not right
    private static final String PRODUCT = "Product";
    private static final String QTY = "Qty";
    private static final String TEMP_QTY = "TempQty";

    //tbl_DeliveryNote
    private static final String I_STATUS = "iStatus";

    private static final String I_ID = "iId";
    private static final String S_LOGIN_NAME = "sLoginName";
    private static final String S_PASSWORD = "sPassword";

    //current_login
    private static  final String USER_ID = "uId";

    //stock count
    private static  final String I_VOUCHER_NO = "iVoucherNo";
    private static  final String D_DATE = "dDate";
    private static  final String I_WAREHOUSE = "iWarehouse";
    private static  final String I_PRODUCT = "iProduct";
    private static  final String F_QTY = "fQty";
    private static  final String S_UNIT = "sUnit";
    private static  final String I_USER = "iUser";
    private static  final String S_REMARKS = "sRemarks";
    private static  final String D_PROCESSED_DATE ="dProcessedDate";
    private static final String S_NARRATION = "sNarration";

    //goods receipt header
    private static final String I_SUPPLIER = "iSupplier";
    private static final String S_PONO = "sPONo";

    //goods receipt body
    private static final String S_MINOR_REMARKS = "sMinorRemarks";
    private static final String S_DAMAGED_REMARKS = "sDamagedRemarks";
    private static final String F_MINOR_DAMAGE_QTY = "fMinorDamageQty";
    private static final String F_DAMAGED_QTY = "fDamagedQty";
    private static final String F_PO_QTY = "fPOQty";
    private static final String S_MINOR_ATTACHMENT = "sMinorAttachment";
    private static final String S_DAMAGED_ATTACHMENT = "sDamagedAttachment";
    private static final String I_MINOR_TYPE = "iMinorId";
    private static final String I_DAMAGED_TYPE = "iDamagedId";

    //goods receipt damage type
    private static final String S_NAME  = "sName";

    //delivery note header
    private static final String S_SALESMAN = "sSalesman";
    private static final String S_CONTACT_PERSON = "sContactPerson";
    private static final String S_SO_NOS = "sSONos";
    private static final String I_CUSTOMER = "iCustomerRef";
    private static final String S_DATE = "sDate";


    //delivery note body
    private static final String S_ITEM_CODE = "iItemCode";
    private static final String S_DESCRIPTION = "sDescription";
    private static final String S_ATTACHMENT = "sAttachment";
    private static final String S_SONo = "sSONo";
    private static final String F_SO_QTY = "sSOQty";
    private static final String S_SONO = "sSONo";



    //create table Product
    private static final String CREATE_TABLE_PRODUCT = "create table if not exists  " + TABLE_PRODUCT + " (" +
            "" + MASTER_ID + " INTEGER PRIMARY KEY ," +
            "" + NAME + " TEXT(65) DEFAULT null ," +
            "" + CODE + "  TEXT(40) DEFAULT null," +
            "" + BARCODE + "  TEXT(30) DEFAULT null," +
            "" + UNIT + " TEXT(20) DEFAULT null" +
            ")";

    private static final String CREATE_CURRENT_LOGIN = "create table if not exists  " + TABLE_CURRENT_LOGIN + " (" +
            "" + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" + USER_ID + " INTEGER DEFAULT null)";


    //create table User
    private static final String CREATE_TABLE_USER = "create table if not exists  " + TABLE_USER + " (" +
            "" + I_ID + " INTEGER DEFAULT 0, " +
            "" + S_LOGIN_NAME + " TEXT DEFAULT null," +
            "" + S_PASSWORD + " TEXT DEFAULT null" + ");";


    //create table warehouse
    private static final String CREATE_TABLE_WAREHOUSE = "create table if not exists  " + TABLE_WAREHOUSE + " (" +
            "" + MASTER_ID + " TEXT DEFAULT null," +
            "" + NAME + " TEXT(60) DEFAULT null" + ");";


    //create table StockCount
    private static final String CREATE_TABLE_STOCK_COUNT = "create table if not exists " + TABLE_STOCK_COUNT + " (" +
            "" + I_VOUCHER_NO + "  INTEGER DEFAULT 0," +
            "" + D_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_USER + " INTEGER DEFAULT 0,"+
            "" + I_WAREHOUSE + "  INTEGER DEFAULT 0," +
            "" + I_PRODUCT + "  INTEGER DEFAULT 0," +
            "" + F_QTY + "  TEXT(10) DEFAULT null," +
            "" + S_UNIT + "  TEXT(10) DEFAULT null," +
            "" + S_NARRATION + "  TEXT(50) DEFAULT null," +
            "" + S_REMARKS + "  TEXT(50) DEFAULT null," +
            "" + D_PROCESSED_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_STATUS + "  TEXT(10) DEFAULT null" +
            ")";


    //create table Pending SO
    private static final String CREATE_TABLE_PENDING_SO = "create table if not exists  " + TABLE_PENDING_SO + " (" +
            "" + DOC_NO + " TEXT(30) DEFAULT null ," +
            "" + DOC_DATE + " TEXT(10) DEFAULT null ," +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + CUSTOMER + "  TEXT(20) DEFAULT null," +
            "" + TEMP_QTY + " TEXT(10) DEFAULT '0',"+
            "" + UNIT + " TEXT(15) DEFAULT null" +
            ")";
    //create table Pending PO
    private static final String CREATE_TABLE_PENDING_PO = "create table if not exists " + TABLE_PENDING_PO + " (" +
            "" + DOC_NO + " TEXT(30) DEFAULT null ," +
            "" + DOC_DATE + " TEXT(10) DEFAULT null ," +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + TEMP_QTY + " TEXT(10) DEFAULT '0',"+
            "" + CUSTOMER + "  TEXT(20) DEFAULT null," +
            "" + UNIT + " TEXT(15) DEFAULT null" +
            ")";

    //create goods receipt header
    private static final String CREATE_TABLE_GOODS_RECEIPT_HEADER = "create table if not exists "+TABLE_GOODS_RECEIPT_HEADER+" (" +
            "" + DOC_NO + " TEXT(30) DEFAULT null ," +
            "" + DOC_DATE + " TEXT(10) DEFAULT null ," +
            "" + I_SUPPLIER + " INTEGER DEFAULT 0 ," +
            "" + I_USER + " INTEGER DEFAULT 0 ,"+
            "" + S_PONO + "  TEXT(150) DEFAULT null," +
            "" + D_PROCESSED_DATE+ " TEXT(10) DEFAULT null ,"+
            "" + S_NARRATION + "  TEXT(50) DEFAULT null" +
            ")";

    //create goods receipt body
    private static final String CREATE_TABLE_GOODS_RECEIPT_BODY = "create table if not exists "+TABLE_GOODS_RECEIPT_BODY+" (" +
            "" + DOC_NO + " TEXT(20) DEFAULT null ," +
            "" + S_PONO + " TEXT(10) DEFAULT null ," +
            "" + I_PRODUCT + " INTEGER DEFAULT 0, "+
            "" + I_WAREHOUSE + " INTEGER DEFAULT 0,"  +
            "" + I_USER + " INTEGER DEFAULT 0 ,"+
            "" + BARCODE + " TEXT(30) DEFAULT null ," +
            "" + F_PO_QTY + "  TEXT(10) DEFAULT null," +
            "" + F_QTY + "  TEXT(10) DEFAULT null," +
            "" + UNIT + "  TEXT(15) DEFAULT null," +
            "" + S_REMARKS + "  TEXT(50) DEFAULT null," +
            "" + F_MINOR_DAMAGE_QTY + "  TEXT(10) DEFAULT null," +
            "" + S_MINOR_REMARKS+ "  TEXT(50) DEFAULT null," +
            "" + S_MINOR_ATTACHMENT + "  TEXT DEFAULT null," +
            "" + F_DAMAGED_QTY + "  TEXT(10) DEFAULT null," +
            "" + S_DAMAGED_REMARKS + "  TEXT(50) DEFAULT null," +
            "" + S_DAMAGED_ATTACHMENT+ "  TEXT DEFAULT null," +
            "" + I_MINOR_TYPE + "  INTEGER DEFAULT 0," +
            "" + I_DAMAGED_TYPE+ " INTEGER DEFAULT 0" +
            ")";

    //create goods receipt body type
    private static final String CREATE_TABLE_GOODS_RECEIPT_TYPE = "create table if not exists "+TABLE_GOOD_RECEIPT_TYPE+" (" +
            "" + I_ID + " INTEGER DEFAULT 0," +
            "" + S_NAME + " TEXT(50) DEFAULT null )";

    //create Delivery note header
    private static final String CREATE_TABLE_DELIVERY_NOTE_HEADER = "create table if not exists "+TABLE_DELIVERY_NOTE_HEADER+" (" +
            "" + DOC_NO + " TEXT(20) DEFAULT null ," +
            "" + S_DATE + " TEXT(10) DEFAULT null ," +
            "" + D_PROCESSED_DATE + " TEXT(10) DEFAULT null ," +
            "" + S_SO_NOS + " TEXT DEFAULT null," +
            "" + I_USER +" INTEGER DEFAULT null,"+
            "" + I_CUSTOMER + " INTEGER DEFAULT 0," +
            "" + S_NARRATION + " TEXT(50) DEFAULT null " +
            ")";



    //create Delivery note body
    private static final String CREATE_TABLE_DELIVERY_NOTE_BODY = "create table if not exists "+TABLE_DELIVERY_NOTE_BODY+" (" +
            "" + DOC_NO + " TEXT(20) DEFAULT null ," +
            "" + S_SONO + " TEXT(10) DEFAULT null ,"+
            "" + I_WAREHOUSE + " INTEGER DEFAULT 0,"  +
            "" + I_PRODUCT + " INTEGER DEFAULT 0, "+
            "" + S_ATTACHMENT + " TEXT DEFAULT null," +
            "" + S_REMARKS + " TEXT DEFAULT null ," +
            "" + F_QTY + "  TEXT(10) DEFAULT null ," +
            "" + F_SO_QTY + " TEXT(10) DEFAULT null," +
            "" + I_USER +" INTEGER DEFAULT null,"+
            "" + UNIT + "  TEXT(15) DEFAULT null" +
            ")";


    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_PENDING_SO);
        db.execSQL(CREATE_TABLE_PENDING_PO);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_CURRENT_LOGIN);
        db.execSQL(CREATE_TABLE_WAREHOUSE);
        db.execSQL(CREATE_TABLE_STOCK_COUNT);
        db.execSQL(CREATE_TABLE_GOODS_RECEIPT_HEADER);
        db.execSQL(CREATE_TABLE_GOODS_RECEIPT_BODY);
        db.execSQL(CREATE_TABLE_GOODS_RECEIPT_TYPE);
        db.execSQL(CREATE_TABLE_DELIVERY_NOTE_HEADER);
        db.execSQL(CREATE_TABLE_DELIVERY_NOTE_BODY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
            Log.d("databasehelper", "Looking for migration file: " + migrationName);
            readAndExecuteSQLScript(db, context, migrationName);
        }
        onCreate(db);
    }




//Tools for sqlite exicution

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    statement.append(line);
                    statement.append("\n");
                    if (line.endsWith(";")) {
                        db.execSQL(statement.toString());
                        statement = new StringBuilder();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        Log.d("Databasehelper", "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e("Databasehelper", "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Databasehelper", "IOException:", e);
                }
            }
        }

    }

/////////////////////////////////////////////////////




//user
    public boolean InsertUsers(User u){
        this.db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(I_ID,u.getsId());
        cv.put(S_LOGIN_NAME, u.getsLoginName());
        cv.put(S_PASSWORD, u.getsPassword());
        float status = db.insert(TABLE_USER, null, cv);
        return status != -1;
    }

    public boolean DeleteUser() {
        this.db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_USER);
        return true;
    }

    public String GetLoginUser(){
        this.db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT u.sLoginName username from user u " +
                "INNER join current_login c " +
                "on u.iId = c.uId",null);
        if(cursor!=null){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("username"));
        }
        return "";
    }

    public boolean InsertCurrentLoginUser(User u) {
        this.db = getReadableDatabase();
        this.db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select " + I_ID + " from " + TABLE_USER + " where " + S_LOGIN_NAME + "='" + u.getsLoginName() + "' and " + S_PASSWORD + "='" + u.getsPassword() + "'", null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {

            cv.put(USER_ID, cursor.getInt(0));
        }
        float status = db.insert(TABLE_CURRENT_LOGIN, null, cv);

        return status != -1;
    }

    public boolean DeleteCurrentUser(){
        this.db = getWritableDatabase();
        float status = db.delete(TABLE_CURRENT_LOGIN,null,null);
        return status!=-1;
    }

    public boolean LoginUser(User u) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + S_LOGIN_NAME + "='" + u.getsLoginName() + "' and " + S_PASSWORD + "='" + u.getsPassword() + "'", null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean GetLoginStatus() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CURRENT_LOGIN + "", null);
        return cursor.getCount() > 0;

    }

    public String GetUserId() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CURRENT_LOGIN, null);
        if (cursor.moveToFirst()) {
            String UserId = cursor.getString(cursor.getColumnIndex(USER_ID));
            cursor.close();
            return UserId;
        }
        else {
            cursor.close();
            return null;
        }
    }

    public boolean GetUser() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER, null);
        return cursor.moveToFirst();
    }

    //Product
    public boolean CheckProduct(Product p) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PRODUCT + " where " + MASTER_ID + " =  ? ", new String[]{p.getMasterId()});
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else {
            cursor.close();
            return false;
        }
    }

    public boolean InsertProduct(Product p){
        this.db = getWritableDatabase();
        float status = -1;
        if(!CheckProduct(p)) {
            ContentValues cv = new ContentValues();
            cv.put(MASTER_ID, p.getMasterId());
            cv.put(NAME, p.getName());
            cv.put(CODE, p.getCode());
            cv.put(BARCODE,p.getBarcode());
            cv.put(UNIT, p.getUnit());

            status = db.insert(TABLE_PRODUCT, null, cv);
        }
        return status != -1;
    }

    public Cursor GetProductInfo(String barcode){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+NAME+","+CODE+","+UNIT+","+MASTER_ID+" from "+TABLE_PRODUCT+" where "+BARCODE+" = ? ",new String[]{barcode});
        if (cursor.moveToFirst()) {

            return cursor;
        }else {
            return  null;
        }
    }

    public Cursor GetProduct(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+NAME+","+CODE+","+MASTER_ID+" from "+TABLE_PRODUCT,null);
        if (cursor.moveToFirst()) {
            return cursor;
        }else {
            return  null;
        }
    }
    public String GetProductUnit(String Barcode){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+UNIT+" FROM "+TABLE_PRODUCT+" WHERE "+BARCODE+" = ? ",new String[]{Barcode});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(UNIT));
        }else {
            return  "";
        }
    }
    public String GetProductName(String iProduct){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+NAME+" from "+TABLE_PRODUCT+" where "+MASTER_ID+" = ? ",new String[]{iProduct});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(NAME));
        }else {
            return  "";
        }
    }

    public String GetProductCode(String iProduct){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+CODE+" from "+TABLE_PRODUCT+" where "+MASTER_ID+" = ? ",new String[]{iProduct});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(CODE));
        }else {
            return  "";
        }
    }

    public String GetBarcodeFromIProduct(String iProduct) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+BARCODE+" from "+TABLE_PRODUCT+" where "+MASTER_ID+" = ? ",new String[]{iProduct});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(BARCODE));
        }else {
            return  "";
        }
    }


    public Cursor SearchProduct(String keyword) {
     this.db = getReadableDatabase();
      Cursor cursor = db.rawQuery("select "+NAME+","+CODE+","+BARCODE+" from "+TABLE_PRODUCT+"  where "+CODE+" like '"+keyword+"%' or "+NAME+" like '"+keyword+"%' limit 10",null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor SearchProduct2(String keyword) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+NAME+","+CODE+","+MASTER_ID+" from "+TABLE_PRODUCT+" where "+CODE+" like '"+keyword+"%' limit 10",null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

   public Cursor SearchProductPendingPO(String keyword,String HeaderId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select Name,Code,Barcode from tbl_Product p " +
                "INNER JOIN tbl_PendingPO po " +
                "on po.Product = p.MasterId " +
                "where instr(upper(Code),upper(?)) and po.HeaderId = ? limit 10",new String[]{keyword,HeaderId});

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor SearchProductPendingSO(String keyword,String HeaderId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select Name,Code,Barcode from tbl_Product p " +
                "INNER JOIN tbl_PendingSO so " +
                "on so.Product = p.MasterId " +
                "where instr(upper(Code),upper(?)) and so.HeaderId = ? limit 10",new String[]{keyword,HeaderId});

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }


    public Cursor SearchProductDeliveryNote(String keyword,String HeaderId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select Name,Code,Barcode from tbl_Product p " +
                "INNER JOIN tbl_DeliveryNote d " +
                "on d.Product = p.MasterId " +
                "where instr(upper(Code),upper( ? )) and d.HeaderId = ? limit 10",new String[]{keyword,HeaderId});

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }


    public String GetProductBarcode(String barcode) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+ BARCODE +"  from "+TABLE_PRODUCT+" where "+CODE+" = ? ",new String[]{barcode});

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("Barcode"));
        } else {
            return "";
        }

    }



    //Pending SO
    public boolean DeleteOldPendingSO() {
        this.db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_PENDING_SO);
         db.execSQL("delete from "+ TABLE_PENDING_SO);
    return true;
    }

    public boolean InsertPendingSO(PendingSO p){
        this.db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_PENDING_SO);
        float status;

            ContentValues cv = new ContentValues();
            cv.put(DOC_NO, p.getDocNo());
            cv.put(DOC_DATE, p.getDocDate());
            cv.put(HEADER_ID, p.getHeaderId());
            cv.put(SI_NO, p.getSiNo());
            cv.put(PRODUCT,p.getProduct());
            cv.put(CUSTOMER,p.getCustomer());
            cv.put(QTY,p.getQty());
            cv.put(UNIT,p.getUnit());
            status = db.insert(TABLE_PENDING_SO, null, cv);

        return status != -1;
    }

    public Cursor GetDocNo(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+DOC_NO+","+CUSTOMER+","+DOC_DATE+","+HEADER_ID+" FROM "+TABLE_PENDING_SO,null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    public Cursor GetSOs(String HeaderId){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DocNo from tbl_PendingSO WHERE HeaderId = ? and TempQty != Qty GROUP by HeaderId ",new String[]{HeaderId});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }




    public String GetPendingSOTempQty(String sSONo,String iProduct){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " +TEMP_QTY+" from " + TABLE_PENDING_SO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? ", new String[]{sSONo, iProduct});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(TEMP_QTY));
        }
        return "0";
    }


    public Cursor GetCustomer(String keyword) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Cusomer,HeaderId from tbl_PendingSO where Cusomer like '"+keyword+"%'  GROUP BY Cusomer ",null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }


    public Cursor GetAllPendingDN(String HeaderId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from tbl_PendingSO LEFT JOIN tbl_Product WHERE tbl_PendingSO.Product = tbl_Product.MasterId and HeaderId = ? ", new String[]{HeaderId});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }


    /*public Cursor GetDeliveryNoteList() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sum(Qty) as Qty,HeaderId,iVoucherNo from tbl_DeliveryNote  GROUP BY "+I_VOUCHER_NO,null);
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }
    */
    public Cursor GetAllDeliveryNote(String HeaderId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT d.HeaderId,d.SiNo,p.MasterId as Product,p.Name,p.Code,p.Unit,d.iVoucherNo iVoucherNo , d.Qty PickedQty,so.Qty Qty FROM tbl_DeliveryNote d " +
                "INNER JOIN tbl_PendingSO so on d.HeaderId = so.HeaderId and d.sino=so.sino " +
                "INNER JOIN tbl_Product p on d.product=p.masterid " +
                " " +
                "WHERE d.HeaderId = ? " +
                "", new String[]{HeaderId});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    //Warehouse
    public boolean InsertWarehouse(Warehouse w){
        this.db = getWritableDatabase();
        float status;
        ContentValues cv = new ContentValues();
        cv.put(NAME,w.getName() );
        cv.put(MASTER_ID, w.getMasterId());
        status = db.insert(TABLE_WAREHOUSE, null, cv);
        return status != -1;
    }

    public boolean DeleteWarehouse() {
        this.db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_WAREHOUSE);
        return true;
    }
    public String GetWarehouseById(String id){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+NAME+" from "+TABLE_WAREHOUSE + " where "+MASTER_ID+"= ? " ,new String[]{id});
        if(cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(NAME));
        }
        return "";
    }


    public Cursor GetWarehouse(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_WAREHOUSE,null);
        if(cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

//stock count
    public void InsertStockCount(StockCount s){
        this.db = getWritableDatabase();
        float status;

    ContentValues cv = new ContentValues();
    cv.put(I_VOUCHER_NO,s.getiVoucherNo());
    cv.put(D_DATE,s.getdDate());
    cv.put(I_WAREHOUSE,s.getiWarehouse());
    cv.put(I_PRODUCT,s.getiProduct());
    cv.put(F_QTY,s.getfQty());
    cv.put(S_UNIT,s.getsUnit());
    cv.put(I_USER,s.getiUser());
    cv.put(S_NARRATION,s.getsNarration());
    cv.put(S_REMARKS,s.getsRemarks());
    cv.put(D_PROCESSED_DATE,s.getdProcessedDate());
    cv.put(I_STATUS,s.getiStatus());

    status = db.insert(TABLE_STOCK_COUNT, null, cv);

    }


public boolean DeleteStockCount(String voucherNo){
        this.db = getWritableDatabase();
        db.delete(TABLE_STOCK_COUNT,I_VOUCHER_NO+" = ? ",new String[]{voucherNo});
        return true;
}



    public String GetNewVoucherNo(){
    this.db = getReadableDatabase();
    Cursor cursor = db.rawQuery("select "+I_VOUCHER_NO+" from "+TABLE_STOCK_COUNT +" ORDER BY "+I_VOUCHER_NO ,null);
    if(cursor.moveToFirst()){
        cursor.moveToLast();
        return String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(I_VOUCHER_NO)))+1);
    }
    return "1";
}


    public Cursor GetStockCountList() {
        this.db = getReadableDatabase();
        Cursor cursor;
        if(GetUserId().equals("1")) {
             cursor = db.rawQuery("SELECT iVoucherNo,iWarehouse,dDate,sum(fQty) as SumQty FROM tbl_StockCount  GROUP BY iVoucherNo",null);
        }else {
            cursor = db.rawQuery("SELECT iVoucherNo,iWarehouse,dDate,sum(fQty) as SumQty FROM tbl_StockCount where iUser = ? GROUP BY iVoucherNo", new String[]{GetUserId()});
        }
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public Cursor GetAllStockCount() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_StockCount where "+I_STATUS+" = ?  GROUP BY iVoucherNo ",new String[]{"0"});
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }


    public Cursor GetAllStockCountFromVoucher(String voucherNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_STOCK_COUNT+" where "+I_STATUS+" = 0  and iVoucherNo = ? ",new String[]{voucherNo});
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public Cursor GetAllStockCountVoucher() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT iVoucherNo FROM tbl_StockCount where iStatus = 0 GROUP BY iVoucherNo ",null);
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public boolean DeleteStockCount(String vno, String product){
        this.db = getWritableDatabase();
        float status;
        status = db.delete(TABLE_STOCK_COUNT,I_VOUCHER_NO+" =  ? and "+I_PRODUCT+" = ?",new String[]{vno,product});
        return status != -1;

    }

    public String GetWarehouse(String id) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + NAME + " from " + TABLE_WAREHOUSE + " where " + MASTER_ID + " = ?", new String[]{id});

        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return "";
        }

    }


    public Cursor GetHeaderData(String voucherNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+D_DATE+","+I_WAREHOUSE+","+I_VOUCHER_NO+","+S_NARRATION+" FROM "+TABLE_STOCK_COUNT+" where "+I_VOUCHER_NO+" = ? ", new String[]{voucherNo});
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor GetBodyData(String voucherNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_STOCK_COUNT+" where "+I_VOUCHER_NO+" = ? ", new String[]{voucherNo});
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    //  goods receipt header
    public boolean InsertGoodsReceiptHeader(GoodReceiptHeader gh){
        this.db = getWritableDatabase();
        this.db = getReadableDatabase();
        float status;
        ContentValues cv = new ContentValues();
        cv.put(DOC_NO, gh.getDocNo());
        cv.put(DOC_DATE, gh.getDocDate());
        cv.put(I_USER,gh.getiUser());
        cv.put(D_PROCESSED_DATE,gh.getdProcessedDate());
        cv.put(I_SUPPLIER, gh.getiSupplier());
        cv.put(S_PONO,gh.getsPONo());
        cv.put(S_NARRATION,gh.getsNarration());

        Cursor cursor = db.rawQuery("select "+DOC_NO+" from "+TABLE_GOODS_RECEIPT_HEADER+" Where "+DOC_NO+" = ? ",new String[]{gh.getDocNo()});

        if(cursor!=null&&cursor.moveToFirst()){
            status = db.update(TABLE_GOODS_RECEIPT_HEADER,cv,DOC_NO+" = ? ",new String[]{gh.getDocNo()});
        }else {
            status = db.insert(TABLE_GOODS_RECEIPT_HEADER, null, cv);
        }
        return status != -1;
    }


    public String GetNewVoucherNoGoodsReceipt(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+DOC_NO+" from "+TABLE_GOODS_RECEIPT_HEADER +" ORDER BY "+DOC_NO ,null);
        if(cursor.moveToFirst()){
            cursor.moveToLast();
            return String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(I_VOUCHER_NO)))+1);
        }
        return "1";
    }

    public boolean DeleteBodyItems(List<String> iProduct,List<String> PoNo,String DocNo){
        this.db = getWritableDatabase();

        for(int i = 0; i<iProduct.size();i++){
            ContentValues cv = new ContentValues();
            cv.put(TEMP_QTY,"0");
            db.update(TABLE_PENDING_PO,cv,  DOC_NO + " = ? and " + PRODUCT + " = ? ", new String[]{PoNo.get(i), iProduct.get(i)});
        }
        float status =  db.delete(TABLE_GOODS_RECEIPT_BODY,DOC_NO+" = ? ",new String[]{DocNo});
        return status == -1;
        }

    //goods receipt body
    public boolean InsertGoodsReceiptBody(GoodsReceiptBody gb){
        this.db = getWritableDatabase();
        this.db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select " + DOC_NO + "," + S_PONO + "," + F_QTY + "," + I_PRODUCT + " from " + TABLE_GOODS_RECEIPT_BODY + " where " + DOC_NO + " = ? and " + S_PONO + " = ? and " + I_PRODUCT + " = ? ", new String[]{gb.getDocNo(), gb.getsPONo(), gb.getiProduct()});
        Cursor cursor2 = db.rawQuery("select " + DOC_NO + "," + PRODUCT + "," + TEMP_QTY + "," + QTY + " from " + TABLE_PENDING_PO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? ", new String[]{gb.getsPONo(), gb.getiProduct()});

        float status;
        int qty = 0,poQty= 0;
        if(cursor2!=null&&cursor2.moveToFirst()){
            qty = cursor2.getInt(cursor2.getColumnIndex(TEMP_QTY))+Integer.parseInt(gb.getfQty().isEmpty()?"0":gb.getfQty())
                    +Integer.parseInt(gb.getfMinorDamageQty().isEmpty()?"0":gb.getfMinorDamageQty())
                    +Integer.parseInt(gb.getfDamagedQty().isEmpty()?"0":gb.getfDamagedQty());
            poQty = cursor2.getInt(cursor2.getColumnIndex(QTY));
        }

        ContentValues cv = new ContentValues();
        cv.put(DOC_NO,gb.getDocNo());
        cv.put(S_PONO, gb.getsPONo());
        cv.put(I_PRODUCT,gb.getiProduct());
        cv.put(I_WAREHOUSE, gb.getiWarehouse());
        cv.put(BARCODE, gb.getBarcode());
        cv.put(F_PO_QTY, gb.getfPOQty());
        cv.put(F_QTY, gb.getfQty());
        cv.put(I_USER,gb.getiUser());
        cv.put(UNIT, gb.getUnit());
        cv.put(S_REMARKS, gb.getsRemarks());
        cv.put(F_MINOR_DAMAGE_QTY, gb.getfMinorDamageQty());
        cv.put(S_MINOR_REMARKS, gb.getsMinorRemarks());
        cv.put(S_MINOR_ATTACHMENT, gb.getsMinorAttachment());
        cv.put(F_DAMAGED_QTY, gb.getfDamagedQty());
        cv.put(S_DAMAGED_REMARKS, gb.getsDamagedRemarks());
        cv.put(S_DAMAGED_ATTACHMENT, gb.getsDamagedAttachment());
        cv.put(I_MINOR_TYPE, gb.getiMinorType());
        cv.put(I_DAMAGED_TYPE, gb.getiDamageType());

        if(cursor!=null&&cursor.moveToFirst()){
            status = db.update(TABLE_GOODS_RECEIPT_BODY, cv, DOC_NO+" = ? and "+S_PONO+" = ? ",new String[]{gb.getDocNo(),gb.getsPONo()});
        }else {
            status = db.insert(TABLE_GOODS_RECEIPT_BODY, null, cv);
        }

        ContentValues cv2 = new ContentValues();

        cv2.put(TEMP_QTY,String.valueOf(qty));
        float status2 = -1;
        if(status != -1) {
            status2 = db.update(TABLE_PENDING_PO, cv2, DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ?", new String[]{gb.getsPONo(), gb.getiProduct(), gb.getUnit()});
        }
        cursor.close();
        cursor2.close();
        return status2 != -1;
    }

    public boolean deleteGoodsBodyItem(String DocNo){
        float status = -1;
        try {
            this.db = getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_GOODS_RECEIPT_BODY + " where " + DOC_NO + " = ?", new String[]{DocNo});
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String sPONO = cursor.getString(cursor.getColumnIndex(S_PONO));
                    String iProduct = cursor.getString(cursor.getColumnIndex(I_PRODUCT));
                    String unit = cursor.getString(cursor.getColumnIndex(UNIT));

                    int qty = cursor.getString(cursor.getColumnIndex(F_QTY)).isEmpty()?0:Integer.parseInt(cursor.getString(cursor.getColumnIndex(F_QTY)));
                    int minor = cursor.getString(cursor.getColumnIndex(F_MINOR_DAMAGE_QTY)).isEmpty()?0:Integer.parseInt(cursor.getString(cursor.getColumnIndex(F_MINOR_DAMAGE_QTY)));
                    int damaged = cursor.getString(cursor.getColumnIndex(F_DAMAGED_QTY)).isEmpty()?0:Integer.parseInt(cursor.getString(cursor.getColumnIndex(F_DAMAGED_QTY)));

                    int totalQty =  (qty+minor+damaged);


                    Cursor cursor2 = db.rawQuery("select " + DOC_NO + "," + PRODUCT + "," + TEMP_QTY + "," + QTY + " from " + TABLE_PENDING_PO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ?", new String[]{sPONO, iProduct, unit});


                    if (cursor2 != null && cursor2.moveToFirst()) {
                        ContentValues cv2 = new ContentValues();

                        int tempQty = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex(TEMP_QTY)));
                          int restQty =   tempQty - totalQty;
                        if (restQty<0) {
                            cv2.put(TEMP_QTY, "0");
                        }else {
                            cv2.put(TEMP_QTY, String.valueOf(restQty));
                        }

                        db.update(TABLE_PENDING_PO, cv2, DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ?", new String[]{sPONO, iProduct, unit});
                        cursor2.close();
                    }


                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        status = db.delete(TABLE_GOODS_RECEIPT_BODY, DOC_NO + " = ? ", new String[]{DocNo});
                        cursor.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status != -1;
    }

    public boolean deleteGoodsHeaderItem(String DocNo){
        this.db = getWritableDatabase();
        float status = db.delete(TABLE_GOODS_RECEIPT_HEADER,DOC_NO+" = ? ",new String[]{DocNo});
        return status != -1;
    }

    public Cursor GetGoodsPOProdcut(List<String> list){
        this.db = getReadableDatabase();

        String docNo = TextUtils.join(",", list
                .stream()
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String name) {
                        return ("'" + name + "'");
                    }
                })
                .collect(Collectors.toList()));


        Cursor cursor = db.rawQuery("SELECT po.Unit as Unit,po.Qty as Qty,po.TempQty as TempQty,p.Name as Name,p.Code as Code,po.Product as Product,po.DocNo as DocNo FROM tbl_Product p " +
                "INNER JOIN tbl_PendingPO po on p.MasterId = po.Product " +
                "WHERE po.DocNo in ( "+docNo+" )",null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    public Cursor GetAllGoodsReceipt(){
        this.db = getReadableDatabase();
        Cursor cursor;
        if(GetUserId().equals("1")){
             cursor = db.rawQuery("SELECT h.DocNo as DocNo ,h.DocDate as DocDate, sum(b.fQty+b.fMinorDamageQty+fDamagedQty) as sumQty from tbl_GoodsReceiptHeader h " +
                    "INNER join tbl_GoodsReceiptBody b on h.DocNo =b.DocNo " +
                    " GROUP by h.DocNo",null);
        }else {
             cursor = db.rawQuery("SELECT h.DocNo as DocNo ,h.DocDate as DocDate, sum(b.fQty+b.fMinorDamageQty+fDamagedQty) as sumQty from tbl_GoodsReceiptHeader h " +
                    "INNER join tbl_GoodsReceiptBody b on h.DocNo =b.DocNo where h.iUser = ? " +
                    " GROUP by h.DocNo", new String[]{GetUserId()});
        }
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }else
        return null;
    }

    public Cursor GetAllGoodsDamagedAttachment(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("WITH RECURSIVE split(sDamagedAttachment,str) AS (" +
                "    SELECT '',sDamagedAttachment||',' FROM tbl_GoodsReceiptBody WHERE DocNo = "+DocNo+" " +
                "    UNION SELECT " +
                "    substr(str, 0, instr(str,','))," +
                "    substr(str, instr(str, ',')+1)" +
                "    FROM split WHERE str!=''" +
                ") " +
                "SELECT sDamagedAttachment " +
                "FROM split where sDamagedAttachment !=''",null);
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetAllGoodsMinorAttachment(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("WITH RECURSIVE split(sMinorAttachment,str) AS (" +
                "    SELECT '',sMinorAttachment||',' FROM tbl_GoodsReceiptBody WHERE DocNo = "+DocNo+" " +
                "    UNION SELECT " +
                "    substr(str, 0, instr(str,','))," +
                "    substr(str, instr(str, ',')+1)" +
                "    FROM split WHERE str!=''" +
                ") " +
                "SELECT sMinorAttachment " +
                "FROM split where sMinorAttachment !=''",null);
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetGoodsHeaderData(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_GOODS_RECEIPT_HEADER+" where "+DOC_NO+" = ? ",new String[]{DocNo});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetAllGoodsHeader(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_GOODS_RECEIPT_HEADER,null);
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetGoodsBodyData(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_GOODS_RECEIPT_BODY+" where "+DOC_NO+" = ? ",new String[]{DocNo});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }


    public Cursor GetPOs(String HeaderId){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DocNo from tbl_PendingPO WHERE HeaderId = ? and TempQty != Qty GROUP by HeaderId ",new String[]{HeaderId});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }


    // Insert Pending PO
    public boolean InsertPendingPO(PendingSO p){
        this.db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_PENDING_PO);
        float status;

        ContentValues cv = new ContentValues();
        cv.put(DOC_NO, p.getDocNo());
        cv.put(DOC_DATE, p.getDocDate());
        cv.put(HEADER_ID, p.getHeaderId());
        cv.put(SI_NO, p.getSiNo());
        cv.put(PRODUCT,p.getProduct());
        cv.put(CUSTOMER,p.getCustomer());
        cv.put(QTY,p.getQty());
        cv.put(UNIT,p.getUnit());
        status = db.insert(TABLE_PENDING_PO, null, cv);

        return status != -1;
    }


    public String GetPendingPOTempQty(String sPONo,String iProduct){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " +TEMP_QTY+" from " + TABLE_PENDING_PO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? ", new String[]{sPONo, iProduct});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(TEMP_QTY));
        }
        return "0";
    }

    public boolean DeleteOldPendingPO() {
        this.db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_PENDING_PO);
        db.delete(TABLE_PENDING_PO,null,null);
        return true;
    }

    public Cursor GetHeaderIdForPO(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+HEADER_ID+","+CUSTOMER+","+DOC_DATE+" FROM "+TABLE_PENDING_PO,null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }
    public Cursor GetAllPendingPO(String DocNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from tbl_PendingPO " +
                "LEFT JOIN tbl_Product " +
                "WHERE " +
                "tbl_PendingPO.Product = tbl_Product.MasterId and HeaderId = ? ", new String[]{DocNo});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }


    public Cursor GetSupplier(String keyword) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Cusomer,HeaderId from tbl_PendingPO where Cusomer like '"+keyword+"%'  GROUP BY Cusomer ",null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }



    public boolean InsertGoodsType(GoodsDamageType gt){
        this.db = getWritableDatabase();
        float status = -1;
        ContentValues cv = new ContentValues();
        cv.put(S_NAME,gt.getsName());
        cv.put(I_ID,gt.getiId());
        status = db.insert(TABLE_GOOD_RECEIPT_TYPE,null,cv);
        return status!=-1;
    }


    public boolean DeleteGoodsType(){
        this.db = getWritableDatabase();
        float status = -1;
        status = db.delete(TABLE_GOOD_RECEIPT_TYPE,null,null);
        return status!=-1;
    }


    public Cursor GetGoodsType(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_GOOD_RECEIPT_TYPE,null);
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public String GetRemarksTypeName(String iId){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+S_NAME+" from "+TABLE_GOOD_RECEIPT_TYPE +" where "+I_ID+" = ?",new String[]{iId});
        String sName = "";
        if(cursor!=null&&cursor.moveToFirst()){
            sName = cursor.getString(cursor.getColumnIndex(S_NAME));
            cursor.close();
        }
        return sName;
    }




    //delivery note header
    public boolean InsertDeliverNoteHeader(DeliveryNoteHeader h){
    this.db = getWritableDatabase();
    this.db = getReadableDatabase();

    float status = -1;
        ContentValues cv = new ContentValues();
        cv.put(DOC_NO,h.getsVoucherNo());
        cv.put(S_DATE,h.getsDate());
        cv.put(S_SO_NOS,h.getsSOPNo());
        cv.put(I_USER,h.getiUser());
        cv.put(I_CUSTOMER,h.getiCustomer());
        cv.put(S_NARRATION,h.getsNarration());
        cv.put(D_PROCESSED_DATE,h.getdProposedDate());

        Cursor cursor = db.rawQuery("select "+DOC_NO+" from "+TABLE_DELIVERY_NOTE_HEADER+" Where "+DOC_NO+" = ? ",new String[]{h.getsVoucherNo()});

        if(cursor!=null&&cursor.moveToFirst()){
            status = db.update(TABLE_DELIVERY_NOTE_HEADER,cv,DOC_NO+" = ? ",new String[]{h.getsVoucherNo()});
        }else {
            status = db.insert(TABLE_DELIVERY_NOTE_HEADER, null, cv);
        }
    return status != -1;
    }





    //delivery note body
    public boolean InsertDeliverNoteBody(DeliveryNoteBody b){
        this.db = getWritableDatabase();
        this.db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select " + DOC_NO + "," + S_SONO + "," + F_QTY + "," + I_PRODUCT + " from " + TABLE_DELIVERY_NOTE_BODY + " where " + DOC_NO + " = ? and " + S_SONO + " = ? and " + I_PRODUCT + " = ? ", new String[]{b.getsVoucherNo(), b.getsSONo(), b.getiProduct()});
        Cursor cursor2 = db.rawQuery("select " + DOC_NO + "," + PRODUCT + "," + TEMP_QTY + "," + QTY + " from " + TABLE_PENDING_SO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? ", new String[]{b.getsSONo(), b.getiProduct()});

        float status = -1;
        int qty = 0,soQty= 0;


        if(cursor2!=null&&cursor2.moveToFirst()){
            qty = cursor2.getInt(cursor2.getColumnIndex(TEMP_QTY))+Integer.parseInt(b.getfQty().isEmpty()?"0":b.getfQty());
            soQty = cursor2.getInt(cursor2.getColumnIndex(QTY));
        }

        ContentValues cv = new ContentValues();
        cv.put(DOC_NO,b.getsVoucherNo());
        cv.put(S_SONO,b.getsSONo());
        cv.put(I_PRODUCT,b.getiProduct());
        cv.put(I_WAREHOUSE,b.getiWarehouse());
        cv.put(F_SO_QTY,b.getsSOQty());
        cv.put(S_REMARKS,b.getsRemarks());
        cv.put(F_QTY,b.getfQty());
        cv.put(S_ATTACHMENT,b.getsAttachment());
        cv.put(I_USER,b.getiUser());
        cv.put(UNIT,b.getUnit());

        if(cursor!=null&&cursor.moveToFirst()){
            status = db.update(TABLE_DELIVERY_NOTE_BODY, cv, DOC_NO+" = ? and "+S_SONO+" = ? ",new String[]{b.getsVoucherNo(),b.getsSONo()});
        }else {
            status = db.insert(TABLE_DELIVERY_NOTE_BODY, null, cv);
        }

        ContentValues cv2 = new ContentValues();

        cv2.put(TEMP_QTY,String.valueOf(qty));
        float status2 = -1;
        if(status != -1) {
            status2 = db.update(TABLE_PENDING_SO, cv2, DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ? ", new String[]{b.getsSONo(), b.getiProduct(), b.getUnit()});
        }
        cursor.close();
        cursor2.close();
        return status2 != -1;
    }



    public Cursor GetDeliveryBodyData(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_DELIVERY_NOTE_BODY+" where "+DOC_NO+" = ? ",new String[]{DocNo});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetDeliverySOProdcut(List<String> list){
        this.db = getReadableDatabase();

        String docNo = TextUtils.join(",", list
                .stream()
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String name) {
                        return ("'" + name + "'");
                    }
                })
                .collect(Collectors.toList()));


        Cursor cursor = db.rawQuery("SELECT so.Unit as Unit,so.Qty as Qty,so.TempQty as TempQty,p.Name as Name,p.Code as Code,so.Product as Product ,so.DocNo as DocNo FROM tbl_Product p " +
                "INNER JOIN tbl_PendingSO so on p.MasterId = so.Product " +
                "WHERE so.DocNo in ( "+docNo+" )",null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    public boolean DeleteDeliveryNoteBodyItem(String DocNo){
        float status = -1;
        try {
            this.db = getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_DELIVERY_NOTE_BODY + " where " + DOC_NO + " = ?", new String[]{DocNo});
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String sPONO = cursor.getString(cursor.getColumnIndex(S_SONO));
                    String iProduct = cursor.getString(cursor.getColumnIndex(I_PRODUCT));
                    String unit = cursor.getString(cursor.getColumnIndex(UNIT));

                    int qty = cursor.getString(cursor.getColumnIndex(F_QTY)).isEmpty()?0:Integer.parseInt(cursor.getString(cursor.getColumnIndex(F_QTY)));


                    int totalQty =  (qty);


                    Cursor cursor2 = db.rawQuery("select " + DOC_NO + "," + PRODUCT + "," + TEMP_QTY + "," + QTY + " from " + TABLE_PENDING_SO + " where " + DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ?", new String[]{sPONO, iProduct, unit});


                    if (cursor2 != null && cursor2.moveToFirst()) {
                        ContentValues cv2 = new ContentValues();

                        int tempQty = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex(TEMP_QTY)));
                        int restQty =   tempQty - totalQty;
                        if (restQty<0) {
                            cv2.put(TEMP_QTY, "0");
                        }else {
                            cv2.put(TEMP_QTY, String.valueOf(restQty));
                        }

                        db.update(TABLE_PENDING_SO, cv2, DOC_NO + " = ? and " + PRODUCT + " = ? and " + UNIT + " = ?", new String[]{sPONO, iProduct, unit});
                        cursor2.close();
                    }


                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        status = db.delete(TABLE_DELIVERY_NOTE_BODY, DOC_NO + " = ? ", new String[]{DocNo});
                        cursor.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status != -1;
    }
    public boolean DeleteDeliveryNoteHeaderItem(String DocNo){
        this.db = getWritableDatabase();
        float status = db.delete(TABLE_DELIVERY_NOTE_HEADER,DOC_NO+" = ? ",new String[]{DocNo});
        return status != -1;
    }


    public Cursor GetAllDeliveryNote(){
        this.db = getReadableDatabase();
        Cursor cursor;
        if(GetUserId().equals("1")){
            cursor = db.rawQuery("SELECT h.DocNo as DocNo ,h.sDate as DocDate, sum(b.fQty) as sumQty from tbl_DeliveryNoteHeader h " +
                    "INNER join tbl_DeliveryNoteBody b on h.DocNo =b.DocNo " +
                    " GROUP by h.DocNo",null);
        }else {
            cursor = db.rawQuery("SELECT h.DocNo as DocNo ,h.sDate as DocDate, sum(b.fQty) as sumQty from tbl_DeliveryNoteHeader h " +
                    "INNER join tbl_DeliveryNoteBody b on h.DocNo =b.DocNo where h.iUser = ? " +
                    " GROUP by h.DocNo", new String[]{GetUserId()});
        }
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }else
            return null;
    }
    public Cursor GetDeliveryHeaderData(String DocNo){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_DELIVERY_NOTE_HEADER+" where "+DOC_NO+" = ? ",new String[]{DocNo});
        if(cursor!=null&&cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }
}
