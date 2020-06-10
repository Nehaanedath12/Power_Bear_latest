package com.sangsolutions.powerbear.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PowerBear.db";
    private static final String TABLE_PRODUCT = "tbl_Product";
    private static final String TABLE_PENDING_SO = "tbl_PendingSO";
    private static final String TABLE_DELIVERY_NOTE = "tbl_DeliveryNote";
    private static final String TABLE_USER = "user";
    private static final String TABLE_CURRENT_LOGIN = "current_login";
    private static final String TABLE_WAREHOUSE = "tbl_warehouse";
    private static final String TABLE_STOCK_COUNT = "tbl_StockCount";
    private static final String TABLE_PENDING_PO = "tbl_PendingPO";
    private static final String TABLE_GOODS_RECEIPT = "tbl_GoodsReceipt";

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
    private static  final String S_REMARKS = "sRemarks";
    private static  final String D_PROCESSED_DATE ="dProcessedDate";




//create table Product
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " (" +
            "" + MASTER_ID + " INTEGER PRIMARY KEY ," +
            "" + NAME + " TEXT(65) DEFAULT null ," +
            "" + CODE + "  TEXT(40) DEFAULT null," +
            "" + BARCODE + "  TEXT(30) DEFAULT null," +
            "" + UNIT + " TEXT(20) DEFAULT null" +
            ")";
    private static final String CREATE_CURRENT_LOGIN = "CREATE TABLE " + TABLE_CURRENT_LOGIN + " (" +
            "" + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" + USER_ID + " INTEGER DEFAULT null)";


    //create table Product
    private static final String CREATE_TABLE_PENDING_SO = "CREATE TABLE " + TABLE_PENDING_SO + " (" +
            "" + DOC_NO + " TEXT(30) DEFAULT null ," +
            "" + DOC_DATE + " TEXT(10) DEFAULT null ," +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + CUSTOMER + "  TEXT(20) DEFAULT null," +
            "" + UNIT + " TEXT(15) DEFAULT null" +
            ")";

    //create table User
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " (" +
            "" + I_ID + " INTEGER DEFAULT 0, " +
            "" + S_LOGIN_NAME + " TEXT DEFAULT null," +
            "" + S_PASSWORD + " TEXT DEFAULT null" + ");";

    //create table DeliveryNote
    private static final String CREATE_TABLE_DELIVERY_NOTE = "CREATE TABLE " + TABLE_DELIVERY_NOTE + " (" +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + I_STATUS + "  INTEGER DEFAULT 0" +
            ")";

    //create table warehouse
    private static final String CREATE_TABLE_WAREHOUSE = "CREATE TABLE " + TABLE_WAREHOUSE + " (" +
            "" + MASTER_ID + " TEXT DEFAULT null," +
            "" + NAME + " TEXT(60) DEFAULT null" + ");";


    //create table StockCount
    private static final String CREATE_TABLE_STOCK_COUNT = "CREATE TABLE " + TABLE_STOCK_COUNT + " (" +
            "" + I_VOUCHER_NO + "  INTEGER DEFAULT 0," +
            "" + D_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_WAREHOUSE + "  INTEGER DEFAULT 0," +
            "" + I_PRODUCT + "  INTEGER DEFAULT 0," +
            "" + F_QTY + "  TEXT(10) DEFAULT null," +
            "" + S_UNIT + "  TEXT(10) DEFAULT null," +
            "" + S_REMARKS + "  TEXT(50) DEFAULT null," +
            "" + D_PROCESSED_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_STATUS + "  TEXT(10) DEFAULT null" +
            ")";


    private static final String CREATE_TABLE_GOODS_RECEIPT = "CREATE TABLE " + TABLE_GOODS_RECEIPT + " (" +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + I_STATUS + "  INTEGER DEFAULT 0" +
            ")";


    private static final String CREATE_TABLE_PENDING_PO = "CREATE TABLE " + TABLE_PENDING_PO + " (" +
            "" + DOC_NO + " TEXT(30) DEFAULT null ," +
            "" + DOC_DATE + " TEXT(10) DEFAULT null ," +
            "" + HEADER_ID + "  INTEGER DEFAULT 0," +
            "" + SI_NO + "  INTEGER DEFAULT 0," +
            "" + PRODUCT + "  INTEGER DEFAULT 0," +
            "" + QTY + "  TEXT(10) DEFAULT null," +
            "" + CUSTOMER + "  TEXT(20) DEFAULT null," +
            "" + UNIT + " TEXT(15) DEFAULT null" +
            ")";

    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_PENDING_SO);
        db.execSQL(CREATE_TABLE_DELIVERY_NOTE);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_CURRENT_LOGIN);
        db.execSQL(CREATE_TABLE_WAREHOUSE);
        db.execSQL(CREATE_TABLE_STOCK_COUNT);
        db.execSQL(CREATE_TABLE_PENDING_PO);
        db.execSQL(CREATE_TABLE_GOODS_RECEIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




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

    public boolean InsertCurrentLoginUser(User u) {
        this.db =getReadableDatabase();
        this.db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select " + I_ID + " from " + TABLE_USER + " where " + S_LOGIN_NAME + "='" + u.getsLoginName() + "' and " + S_PASSWORD + "='" + u.getsPassword() + "'", null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {

            cv.put(USER_ID, cursor.getInt(0));
        }
        float status = db.insert(TABLE_CURRENT_LOGIN, null, cv);

        return status != -1;
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
        if (cursor.moveToFirst())
            return cursor.getString(cursor.getColumnIndex(USER_ID));
        else {
            return null;
        }
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
      Cursor cursor = db.rawQuery("select "+NAME+","+CODE+","+BARCODE+" from "+TABLE_PRODUCT+" where instr(upper(Code),upper(?)) limit 10",new String[]{keyword});

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }


    //Pending SO
    public boolean DeleteOldPendingSO() {
        this.db = getWritableDatabase();
         db.execSQL("delete from "+ TABLE_PENDING_SO);
    return true;
    }

    public boolean InsertPendingSO(PendingSO p){
        this.db = getWritableDatabase();
        float status = -1;

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
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+DOC_NO+","+CUSTOMER+","+DOC_DATE+" FROM "+TABLE_PENDING_SO,null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    public Cursor GetAllPendingDN(String DocNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from tbl_PendingSO LEFT JOIN tbl_Product WHERE tbl_PendingSO.Product = tbl_Product.MasterId and DocNo = ? ", new String[]{DocNo});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    //Delivery Note
    public boolean InsertDelivery(DeliveryNote d){
        this.db = getWritableDatabase();
        float status = -1;
            ContentValues cv = new ContentValues();
            cv.put(HEADER_ID,d.getHeaderId() );
            cv.put(SI_NO, d.getSiNo());
            cv.put(PRODUCT, d.getProduct());
            cv.put(QTY,d.getQty());
            cv.put(I_STATUS, d.getiStatus());

            status = db.insert(TABLE_DELIVERY_NOTE, null, cv);

        return status != -1;
    }

    public Cursor GetDeliveryNote(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_DELIVERY_NOTE,null);
        if(cursor.moveToFirst()){
            return cursor;
        }
        return null;
    }

    public Cursor GetDeliveryNoteList() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sum(Qty) as Qty,HeaderId from tbl_DeliveryNote  GROUP BY HeaderId",null);
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public boolean DeleteDeliveryNote(String id){
        this.db = getWritableDatabase();
        db.delete(TABLE_DELIVERY_NOTE,HEADER_ID+" = ? ",new String[]{id});
        return true;
    }


    public Cursor GetAllDeliveryNote(String DocNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT d.HeaderId,d.SiNo,p.MasterId as Product,p.Name,p.Code,p.Unit, d.Qty PickedQty,so.Qty Qty FROM tbl_DeliveryNote d " +
                "INNER JOIN tbl_PendingSO so on d.HeaderId = so.HeaderId and d.sino=so.sino " +
                "INNER JOIN tbl_Product p on d.product=p.masterid " +
                " " +
                "WHERE d.HeaderId = ? " +
                "", new String[]{DocNo});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    //Warehouse
    public boolean InsertWarehouse(Warehouse w){
        this.db = getWritableDatabase();
        float status = -1;
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
    public boolean InsertStockCount(StockCount s){
    this.db = getWritableDatabase();
    float status = -1;
    ContentValues cv = new ContentValues();
    cv.put(I_VOUCHER_NO,s.getiVoucherNo());
    cv.put(D_DATE,s.getdDate());
    cv.put(I_WAREHOUSE,s.getiWarehouse());
    cv.put(I_PRODUCT,s.getiProduct());
    cv.put(F_QTY,s.getfQty());
    cv.put(S_UNIT,s.getsUnit());
    cv.put(S_REMARKS,s.getsRemarks());
    cv.put(D_PROCESSED_DATE,s.getdProcessedDate());
    cv.put(I_STATUS,s.getiStatus());

    status = db.insert(TABLE_STOCK_COUNT, null, cv);

    return status != -1;

}


public boolean DeleteStockCount(String voucherNo){
        this.db = getWritableDatabase();
        db.delete(TABLE_STOCK_COUNT,I_VOUCHER_NO+" = ? ",new String[]{voucherNo});
        return true;
}



    public String GetNewVoucherNo(){
    this.db = getReadableDatabase();
    Cursor cursor = db.rawQuery("select "+I_VOUCHER_NO+" from "+TABLE_STOCK_COUNT  ,null);
    if(cursor.moveToFirst()){
        cursor.moveToLast();
        return String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(I_VOUCHER_NO)))+1);
    }
    return "1";
}


    public Cursor GetStockCountList() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT iVoucherNo,iWarehouse,dDate,sum(fQty) as SumQty FROM tbl_StockCount GROUP BY iVoucherNo",null);
        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
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
        Cursor cursor = db.rawQuery("SELECT "+D_DATE+","+I_WAREHOUSE+","+I_VOUCHER_NO+","+S_REMARKS+" FROM "+TABLE_STOCK_COUNT+" where "+I_VOUCHER_NO+" = ? ", new String[]{voucherNo});
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


    // insert goods receipt
    public boolean InsertGoodsReceipt(DeliveryNote d){
        this.db = getWritableDatabase();
        float status = -1;
        ContentValues cv = new ContentValues();
        cv.put(HEADER_ID,d.getHeaderId() );
        cv.put(SI_NO, d.getSiNo());
        cv.put(PRODUCT, d.getProduct());
        cv.put(QTY,d.getQty());
        cv.put(I_STATUS, d.getiStatus());

        status = db.insert(TABLE_DELIVERY_NOTE, null, cv);

        return status != -1;
    }

    // Insert Pending PO

    public boolean InsertPendingPO(PendingSO p){
        this.db = getWritableDatabase();
        float status = -1;

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

    public boolean DeleteOldPendingPO() {
        this.db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_PENDING_PO);
        return true;
    }

    public Cursor GetDocNoForPO(){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+DOC_NO+","+CUSTOMER+","+DOC_DATE+" FROM "+TABLE_PENDING_PO,null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }
    public Cursor GetAllPendingPO(String DocNo) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from tbl_PendingPO LEFT JOIN tbl_Product WHERE tbl_PendingPO.Product = tbl_Product.MasterId and DocNo = ? ", new String[]{DocNo});
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }


    public boolean InsertGoodsReceipt(GoodsReceipt g){
        this.db = getWritableDatabase();
        float status = -1;
        ContentValues cv = new ContentValues();
        cv.put(HEADER_ID,g.getHeaderId() );
        cv.put(SI_NO, g.getSiNo());
        cv.put(PRODUCT, g.getProduct());
        cv.put(QTY,g.getQty());
        cv.put(I_STATUS, g.getiStatus());

        status = db.insert(TABLE_GOODS_RECEIPT, null, cv);

        return status != -1;
    }
}
