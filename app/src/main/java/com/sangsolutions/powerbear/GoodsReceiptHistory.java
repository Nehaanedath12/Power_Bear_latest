package com.sangsolutions.powerbear;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistoryAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptHistorySingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptHistory extends AppCompatActivity {
    ImageView add_new;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView date,title_selection;
    GoodsReceiptHistoryAdapter adapter;
    DatabaseHelper helper;
    ImageView img_home;
    ImageView close,delete;
    AppBarLayout appbar;
    Toolbar toolbar;
    List<com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory> list;
    boolean selection_active=false;

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    public void DeleteItems(){
        List<Integer> listSelectedItem = adapter.getSelectedItems();
        for(int i = 0 ; i<listSelectedItem.size();i++) {
            for(int j = 0 ;j<list.size();j++) {
                if(listSelectedItem.get(i)==j)
                    if (helper.deleteGoodsBodyItem(list.get(j).getDocNo())) {
                        helper.DeleteGoodsHeaderItem(list.get(j).getDocNo());
                        Log.d("StockCount", "deleted!");
                    }
            }
            if(i+1 == listSelectedItem.size()){
                setRecyclerView();
                closeSelection();
            }
        }

    }


    public void deleteAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete "+adapter.getSelectedItemCount()+" items?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        DeleteItems();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create()
                .show();
    }


    private void initToolbar() {
        appbar = findViewById(R.id.appbar);
        close = findViewById(R.id.goodsReceipt);
        delete = findViewById(R.id.delete);
        toolbar = findViewById(R.id.toolbar);
        title_selection = findViewById(R.id.title_selection);
        setSupportActionBar(toolbar);
        close.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        appbar.setVisibility(View.GONE);
    }


    public void closeSelection(){
        adapter.clearSelections();
        appbar.setVisibility(View.GONE);
        selection_active=false;
    }

    private void toggleSelection(int position) {
        appbar.setVisibility(View.VISIBLE);
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if(count==0){
            closeSelection();
        }
        title_selection.setText("Selected "+count+" item's");
        close.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    public void setRecyclerView(){
        list.clear();

        Cursor cursor = helper.GetAllGoodsReceipt();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String DocNo,TotalQty,DocDate;

                DocNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                TotalQty = cursor.getString(cursor.getColumnIndex("sumQty"));
                DocDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                list.add(new com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory(DocNo,TotalQty,DocDate));
                cursor.moveToNext();

                if (i + 1 == cursor.getCount()) {
                    rv.setAdapter(adapter);
                    GoodsReceiptHistorySingleton.getInstance().setList(list);
                    cursor.close();
                }
            }
        }else {
            empty_frame.setVisibility(View.VISIBLE);
            rv.setAdapter(adapter);
        }

    }

    private void DeleteGoodsReceiptItemAlert(final com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                 if(helper.deleteGoodsBodyItem(goodsReceiptHistory.getDocNo())) {
                     if (helper.DeleteGoodsHeaderItem(goodsReceiptHistory.getDocNo())) {
                         list.remove(pos);
                         adapter.notifyDataSetChanged();
                         setRecyclerView();
                         Toast.makeText(GoodsReceiptHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        }
                     }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_receipt_history);

        add_new = findViewById(R.id.add_save);
        date = findViewById(R.id.title);
        empty_frame = findViewById(R.id.empty_frame);
        img_home = findViewById(R.id.home);
        helper = new DatabaseHelper(this);

        initToolbar();

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoodsReceiptHistory.this,Home.class));
                finishAffinity();
            }
        });

       // date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        date.setText("Goods Receipt");
        list = new ArrayList<>();
        adapter = new GoodsReceiptHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));



        //setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GoodsReceiptHistory.this,GoodsReceipt.class));
            }
        });




adapter.setOnClickListener(new GoodsReceiptHistoryAdapter.OnClickListener() {
    @Override
    public void onEditItemClick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, int position) {
        if(!selection_active) {
            Intent intent1 = new Intent(GoodsReceiptHistory.this, GoodsReceipt.class);
            intent1.putExtra("DocNo", goodsReceiptHistory.getDocNo());
            intent1.putExtra("EditMode", true);
            intent1.putExtra("Position",position);
            startActivity(intent1);
        }
    }

    @Override
    public void onDeleteItemClick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, int pos) {
        DeleteGoodsReceiptItemAlert(goodsReceiptHistory, pos);
    }

    @Override
    public void onItemClick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, int pos) {
        if(selection_active) {
            enableActionMode(pos);
        }
    }

    @Override
    public void onItemLongClick(int pos) {
        enableActionMode(pos);
        selection_active = true;
    }

    @Override
    public void onPDFclick(com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory goodsReceiptHistory, int position) {
        if (ActivityCompat.checkSelfPermission(GoodsReceiptHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GoodsReceiptHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            pdfLoading(goodsReceiptHistory.getDocNo());

        }
    }
});

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSelection();
            }
        });

    }

    private void pdfLoading(String docNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pdf!")
                .setMessage("Do you want to save pdf?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pdfGeneration(docNo);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void pdfGeneration(String docNo) {
        File file;
        Bitmap bmp,scalebm;

        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        scalebm=Bitmap.createScaledBitmap(bmp,100,30,false);
        List<GoodsReceiptBody> listMain = new ArrayList<>();
        Cursor cursorHeader=helper.GetGoodsHeaderData(docNo);

        String DocDate = cursorHeader.getString(cursorHeader.getColumnIndex("DocDate"));
        String iVoucherNo = cursorHeader.getString(cursorHeader.getColumnIndex("DocNo"));
        String PONos=cursorHeader.getString(cursorHeader.getColumnIndex("sPONo"));
        String iCustomer=cursorHeader.getString(cursorHeader.getColumnIndex("iSupplier"));

        Cursor cursorCustomer=helper.GetSupplierName(iCustomer);
        String customer=cursorCustomer.getString(cursorCustomer.getColumnIndex("Cusomer"));

        Cursor cursorBody=helper.GetGoodsBodyData(docNo);
        if(cursorBody!=null&&cursorBody.moveToFirst()) {
            for (int i = 0; i < cursorBody.getCount(); i++) {
                listMain.add(new GoodsReceiptBody(
                        cursorBody.getString(cursorBody.getColumnIndex("sPONo")),
                        helper.GetProductName(cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        helper.GetProductCode(cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        cursorBody.getString(cursorBody.getColumnIndex("iProduct")),
                        helper.GetWarehouse(cursorBody.getString(cursorBody.getColumnIndex("iWarehouse"))),
                        cursorBody.getString(cursorBody.getColumnIndex("iWarehouse")),
                        cursorBody.getString(cursorBody.getColumnIndex("Barcode")),
                        cursorBody.getString(cursorBody.getColumnIndex("fPOQty")),
                        cursorBody.getString(cursorBody.getColumnIndex("fQty")),
                        helper.GetPendingPOTempQty(cursorBody.getString(cursorBody.getColumnIndex("sPONo")),cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        cursorBody.getString(cursorBody.getColumnIndex("Unit")),
                        cursorBody.getString(cursorBody.getColumnIndex("sRemarks")),
                        cursorBody.getString(cursorBody.getColumnIndex("fMinorDamageQty")),
                        cursorBody.getString(cursorBody.getColumnIndex("sMinorRemarks")),
                        cursorBody.getString(cursorBody.getColumnIndex("sMinorAttachment")),
                        cursorBody.getString(cursorBody.getColumnIndex("fDamagedQty")),
                        cursorBody.getString(cursorBody.getColumnIndex("sDamagedRemarks")),
                        cursorBody.getString(cursorBody.getColumnIndex("sDamagedAttachment")),
                        cursorBody.getString(cursorBody.getColumnIndex("iMinorId")),
                        cursorBody.getString(cursorBody.getColumnIndex("iDamagedId"))

                ));
                cursorBody.moveToNext();
            }
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo myPageinfo = new PdfDocument
                .PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(myPageinfo);
        Canvas canvas = myPage.getCanvas();

        int startYPosition = 20;

        canvas.drawBitmap(scalebm,400,startYPosition,paint);
        paint.setTextSize(25f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("GOODS RECEIPT ",20, startYPosition += 20, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Date", 20, startYPosition += 50, paint);
        canvas.drawText(": "+DocDate, 120, startYPosition , paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Doc No ",20, startYPosition += 30, paint);
        canvas.drawText(": "+iVoucherNo,120, startYPosition , paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Vendor Name", 20, startYPosition += 30, paint);
        canvas.drawText(": "+customer, 120, startYPosition, paint);

        int endXPosition = myPageinfo.getPageWidth() - 20;
        paint.setTextSize(10);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("S.no", 20, startYPosition += 50, paint);
        canvas.drawText("Code", 60, startYPosition, paint);
        canvas.drawText("Unit", 200, startYPosition, paint);
        canvas.drawText("Description", 250, startYPosition, paint);
        canvas.drawText("Quantity", myPageinfo.getPageWidth()-70, startYPosition, paint);
        canvas.drawLine(20,startYPosition+3,endXPosition,startYPosition+3,paint);

        paint.setFakeBoldText(false);
        if(listMain.size()<20){
            loadFirstPage(listMain.size(),startYPosition,canvas,paint,listMain,myPageinfo, pdfDocument, myPage);
        }
        else if(listMain.size()>20) {
            loadFirstPage(20, startYPosition, canvas, paint, listMain, myPageinfo, pdfDocument, myPage);
            int page = (int) Math.ceil(listMain.size()/ 25.0);
            int start = 20;
            Log.d("pagee", page + "");
            for (int j = 1; j <= page; j++) {
                PdfDocument.PageInfo myPageinfo1 = new PdfDocument
                        .PageInfo.Builder(595, 842, 1).create();

                PdfDocument.Page myPage1 = pdfDocument.startPage(myPageinfo1);
                Canvas canvas1 = myPage1.getCanvas();

                int stop = start + 25;
                Log.d("pagee", page + " " + start + " " + stop);
                if (stop > listMain.size()) {
                    stop = listMain.size();
                }
                startYPosition = 20;
                for (int i = start; i < stop; i++) {
                    int startXPosition = 20;

                    canvas1.drawText(String.valueOf(i + 1), startXPosition, startYPosition+=30, paint);
                    canvas1.drawText(listMain.get(i).getCode(), 60, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getUnit(), 200, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getsRemarks(), 250, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getTempQty(), myPageinfo.getPageWidth() - 70, startYPosition, paint);
                }

                pdfDocument.finishPage(myPage1);
                start += 25;
            }
        }






        file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/GoodsReceiptPDF.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF Not Created", Toast.LENGTH_SHORT).show();

        }
        pdfDocument.close();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkURI = FileProvider.getUriForFile(this,getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        startActivity(intent);

    }

    private void loadFirstPage(int length, int startYPosition, Canvas canvas, Paint paint, List<GoodsReceiptBody> listMain, PdfDocument.PageInfo myPageinfo, PdfDocument pdfDocument, PdfDocument.Page myPage) {
        for (int i=0;i<length;i++){
            //size 20
            int startXPosition = 20;
            startYPosition += 30;
            canvas.drawText(String.valueOf(i + 1), startXPosition, startYPosition, paint);
            canvas.drawText(listMain.get(i).getCode(), 60, startYPosition, paint);
            canvas.drawText(listMain.get(i).getUnit(), 200, startYPosition, paint);
            canvas.drawText(listMain.get(i).getsRemarks(), 250, startYPosition, paint);
            canvas.drawText(listMain.get(i).getTempQty(), myPageinfo.getPageWidth()-70, startYPosition, paint);
//            canvas.drawLine(20, startYPosition + 3, endXPosition, startYPosition + 3, paint);
        }
        pdfDocument.finishPage(myPage);
    }
}