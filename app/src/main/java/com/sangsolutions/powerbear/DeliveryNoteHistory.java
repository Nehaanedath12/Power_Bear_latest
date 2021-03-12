package com.sangsolutions.powerbear;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
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
import com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody;
import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistoryAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteBodySingleton;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteHistorySingleton;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryNoteHistory extends AppCompatActivity {
    ImageView add_new,img_home;
    RecyclerView rv;
    FrameLayout empty_frame;
    TextView date,title_selection;
    DeliveryNoteHistoryAdapter adapter;
    DatabaseHelper helper;
    List<com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory> list;
    private boolean selection_active = false ;
    ImageView close,delete;
    AppBarLayout appbar;
    Toolbar toolbar;


    public void DeleteItems(){
        List<Integer> listSelectedItem = adapter.getSelectedItems();
        for(int i = 0 ; i<listSelectedItem.size();i++) {
            for(int j = 0 ;j<list.size();j++) {
                if(listSelectedItem.get(i)==j)
                    if(helper.DeleteDeliveryNoteBodyItem(list.get(j).getiVoucherNo())) {
                        if (helper.DeleteDeliveryHeaderItem(list.get(j).getiVoucherNo())) {
                            Toast.makeText(DeliveryNoteHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        }
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


    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
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
        selection_active = false;

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

        Cursor cursor = helper.GetAllDeliveryNote();
        if (cursor != null) {
            cursor.moveToFirst();
            empty_frame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String DocDate,TotalQty,iVoucherNo;

               DocDate = cursor.getString(cursor.getColumnIndex("DocDate"));
               TotalQty = cursor.getString(cursor.getColumnIndex("sumQty"));
                iVoucherNo = cursor.getString(cursor.getColumnIndex("DocNo"));

                list.add(new com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory(DocDate,TotalQty,iVoucherNo));
                cursor.moveToNext();

                if (i + 1 == cursor.getCount()) {
                    rv.setAdapter(adapter);
                    DeliveryNoteHistorySingleton.getInstance().setList(list);
                    cursor.close();
                }
            }
        }else {
            empty_frame.setVisibility(View.VISIBLE);
            rv.setAdapter(adapter);
        }
    }

    private void DeleteStockCountItemAlert(final com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete this this entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(helper.DeleteDeliveryNoteBodyItem(deliveryNoteHistory.getiVoucherNo())) {
                                if (helper.DeleteDeliveryHeaderItem(deliveryNoteHistory.getiVoucherNo())) {
                                    list.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    setRecyclerView();
                                    Toast.makeText(DeliveryNoteHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
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
        setContentView(R.layout.activity_stock_count_list);
        initToolbar();
        add_new = findViewById(R.id.add_save);
        date = findViewById(R.id.title);
        empty_frame = findViewById(R.id.empty_frame);
        img_home = findViewById(R.id.home);
        helper = new DatabaseHelper(this);

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeliveryNoteHistory.this,Home.class));
                finishAffinity();
            }
        });


        date.setText("Delivery Note");
        list = new ArrayList<>();
        adapter = new DeliveryNoteHistoryAdapter(list,this);
        rv = findViewById(R.id.rv_summary);
        rv.setLayoutManager(new LinearLayoutManager(this));


        setRecyclerView();

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryNoteHistory.this, AddDeliveryNote.class));
            }
        });

adapter.setOnClickListener(new DeliveryNoteHistoryAdapter.OnClickListener() {
    @Override
    public void onEditItemClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int position) {
        if(!selection_active){
        Intent intent1 = new Intent(DeliveryNoteHistory.this, AddDeliveryNote.class);
        intent1.putExtra("DocNo",deliveryNoteHistory.getiVoucherNo());
        intent1.putExtra("EditMode", true);
        intent1.putExtra("Position",position);
        startActivity(intent1);}
    }

    @Override
    public void onDeleteItemClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int pos) {
       if(!selection_active)
        DeleteStockCountItemAlert(deliveryNoteHistory, pos);
    }

    @Override
    public void onItemClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int pos) {
        if(!selection_active) {

        }else {
            enableActionMode(pos);
        }
    }

    @Override
    public void onItemLongClick(int pos) {
        enableActionMode(pos);
        selection_active = true;
    }

    @Override
    public void onPDFClick(com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory deliveryNoteHistory, int position) {
        Toast.makeText(DeliveryNoteHistory.this, "pdf", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(DeliveryNoteHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryNoteHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            pdfLoading(deliveryNoteHistory.getiVoucherNo());

        }

    }
});
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
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

        bmp=BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        scalebm=Bitmap.createScaledBitmap(bmp,100,30,false);
        List<DeliveryNoteBody> listMain = new ArrayList<>();

        Cursor cursorHeader=helper.GetDeliveryHeaderData(docNo);
        String DocDate = cursorHeader.getString(cursorHeader.getColumnIndex("sDate"));
        String iVoucherNo = cursorHeader.getString(cursorHeader.getColumnIndex("DocNo"));
        String SONos=cursorHeader.getString(cursorHeader.getColumnIndex("sSONos"));
        String iCustomer=cursorHeader.getString(cursorHeader.getColumnIndex("iCustomerRef"));
        String date=String.valueOf(DateFormat.format("dd-MM-yyyy", new Date()));

        Cursor cursorBody=helper.GetDeliveryBodyData(docNo);


        if(cursorBody!=null&&cursorBody.moveToFirst()) {
            for (int i = 0; i < cursorBody.getCount(); i++) {
                listMain.add(new DeliveryNoteBody(
                        cursorBody.getString(cursorBody.getColumnIndex("sSONo")),
                        cursorBody.getString(cursorBody.getColumnIndex("sSOQty")),
                        helper.GetProductName(cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        helper.GetProductCode(cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        helper.GetPendingSOTempQty(cursorBody.getString(cursorBody.getColumnIndex("sSONo")),
                                cursorBody.getString(cursorBody.getColumnIndex("iProduct"))),
                        cursorBody.getString(cursorBody.getColumnIndex("iProduct")),
                        helper.GetWarehouse(String.valueOf(cursorBody.getColumnIndex("iWarehouse"))),
                        cursorBody.getString(cursorBody.getColumnIndex("iWarehouse")),
                        cursorBody.getString(cursorBody.getColumnIndex("sAttachment")),
                        cursorBody.getString(cursorBody.getColumnIndex("sRemarks")),
                        cursorBody.getString(cursorBody.getColumnIndex("fQty")),
                        cursorBody.getString(cursorBody.getColumnIndex("Unit"))));
                cursorBody.moveToNext();
            }
        }

        Cursor cursorCustomer=helper.GetPendingSOsName(iCustomer);
        Log.d("customerr",cursorCustomer.getString(cursorCustomer.getColumnIndex("Cusomer")));

        String customer=cursorCustomer.getString(cursorCustomer.getColumnIndex("Cusomer"));





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
        canvas.drawText("PICK SLIP", 20, startYPosition += 20, paint);


        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Pick slip No: "+iVoucherNo, 20, startYPosition += 30, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Sales Man: ",myPageinfo.getPageWidth()/2, startYPosition , paint);


        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Date: "+date, 20, startYPosition += 30, paint);

        if(SONos.length()<16) {
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Sales and Order No: " + SONos, myPageinfo.getPageWidth() / 2, startYPosition, paint);
        }
        else {
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Sales and Order No: " + SONos.substring(0,16), myPageinfo.getPageWidth() / 2, startYPosition, paint);
            canvas.drawText(SONos.substring(16), myPageinfo.getPageWidth() / 2, startYPosition += 30, paint);

        }



        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Requested by:", 20, startYPosition += 30, paint);

        if(customer.length()<16) {
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Customer Name: " + customer,
                    myPageinfo.getPageWidth() / 2, startYPosition, paint);
        }else
        {
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Customer Name: " + customer.substring(0,16),
                    myPageinfo.getPageWidth() / 2, startYPosition, paint);
            canvas.drawText(customer.substring(16),
                    myPageinfo.getPageWidth() / 2, startYPosition+=30, paint);
        }
        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Collector :", 20, startYPosition += 30, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Signature:", 20, startYPosition += 30, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Expected delivery Date: "+Tools.dateFormat2(DocDate),myPageinfo.getPageWidth()/2, startYPosition , paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(20,startYPosition += 30,myPageinfo.getPageWidth()-20,300,paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);


        paint.setTextSize(15f);
        canvas.drawText("Request Remarks:  ",30,startYPosition+20,paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Delivery Condition:  ", 20, startYPosition += 100, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Packing: ",
                myPageinfo.getPageWidth()/2, startYPosition , paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Branding by:  ", 20, startYPosition += 30, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Misc:",myPageinfo.getPageWidth()/2, startYPosition , paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Approved by:  ", 20, startYPosition += 30, paint);

        paint.setTextSize(15f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Signature: ",myPageinfo.getPageWidth()/2, startYPosition , paint);

        int endXPosition = myPageinfo.getPageWidth() - 20;
        paint.setTextSize(10);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("SL no", 20, startYPosition += 50, paint);
        paint.setTextSize(10);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Code", 60, startYPosition, paint);
        paint.setTextSize(10);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Item", 200, startYPosition, paint);
        paint.setTextSize(10);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Quantity", myPageinfo.getPageWidth()-70, startYPosition, paint);
        canvas.drawLine(20,startYPosition+3,endXPosition,startYPosition+3,paint);

        paint.setFakeBoldText(false);
        if(listMain.size()<=15){
            for (int i = 0; i < listMain.size(); i++) {
                int startXPosition = 20;
                startYPosition += 20;
                paint.setTextSize(10);
                canvas.drawText(String.valueOf(i + 1), startXPosition, startYPosition, paint);
                canvas.drawText(listMain.get(i).getsCode(), 60, startYPosition, paint);
                canvas.drawText(listMain.get(i).getsName(), 200, startYPosition, paint);
                canvas.drawText(listMain.get(i).getfQty(), myPageinfo.getPageWidth() - 50, startYPosition, paint);
                canvas.drawLine(20, startYPosition + 3, endXPosition, startYPosition + 3, paint);
            }
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Store keeper:  ", 20, startYPosition += 100, paint);

            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Signature:  ",myPageinfo.getPageWidth()/2, startYPosition , paint);

            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Receiver:  ", 20, startYPosition += 40, paint);

            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Signature: ",myPageinfo.getPageWidth()/2, startYPosition , paint);

            pdfDocument.finishPage(myPage);
        }
        else {
            for (int i = 0; i < 15; i++) {
                int startXPosition = 20;
                startYPosition += 20;
                paint.setTextSize(10);
                canvas.drawText(String.valueOf(i + 1), startXPosition, startYPosition, paint);
                canvas.drawText(listMain.get(i).getsCode(), 60, startYPosition, paint);
                canvas.drawText(listMain.get(i).getsName(), 200, startYPosition, paint);
                canvas.drawText(listMain.get(i).getfQty(), myPageinfo.getPageWidth() - 50, startYPosition, paint);
                canvas.drawLine(20, startYPosition + 3, endXPosition, startYPosition + 3, paint);
            }

            pdfDocument.finishPage(myPage);
        }

        int page=(int) Math.ceil(listMain.size()/35.0);
        if(listMain.size()>15){
            int start=15;
            Log.d("pagee",page+"");
            for (int j=1;j<=page;j++){
                PdfDocument.PageInfo myPageinfo1 = new PdfDocument
                        .PageInfo.Builder(595, 842, 1).create();

                PdfDocument.Page myPage1 = pdfDocument.startPage(myPageinfo1);
                Canvas canvas1 = myPage1.getCanvas();

                int stop=start+35;
                Log.d("pagee",page+" "+start+" "+stop);
                if(stop>listMain.size()){
                    stop=listMain.size();
                }

                startYPosition = 20;
                for (int i=start;i<=stop;i++){
                    int startXPosition = 20;
                    startYPosition += 20;
                    paint.setTextSize(10);
                    canvas1.drawText(String.valueOf(i + 1), startXPosition, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getsCode(), 60, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getsName(), 200, startYPosition, paint);
                    canvas1.drawText(listMain.get(i).getfQty(), myPageinfo.getPageWidth() - 50, startYPosition, paint);
                    canvas1.drawLine(20, startYPosition + 3, endXPosition, startYPosition + 3, paint);
                }

                if(j==page){
                    paint.setTextSize(15f);
                    paint.setTextAlign(Paint.Align.LEFT);
                    canvas1.drawText("Store keeper:  ", 20, startYPosition += 100, paint);

                    paint.setTextSize(15f);
                    paint.setTextAlign(Paint.Align.LEFT);
                    canvas1.drawText("Signature:  ",myPageinfo.getPageWidth()/2, startYPosition , paint);

                    paint.setTextSize(15f);
                    paint.setTextAlign(Paint.Align.LEFT);
                    canvas1.drawText("Receiver:  ", 20, startYPosition += 40, paint);

                    paint.setTextSize(15f);
                    paint.setTextAlign(Paint.Align.LEFT);
                    canvas1.drawText("Signature: ",myPageinfo.getPageWidth()/2, startYPosition , paint);
                }
                pdfDocument.finishPage(myPage1);
                start+=35;
            }
        }

//            file = new File(equireContext()r.getExternalFilesDir("/"), "PaymentReceiptPDF.pdf");
        file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/DeliveryNotePDF.pdf");
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
}