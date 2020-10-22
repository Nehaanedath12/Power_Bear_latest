package com.sangsolutions.powerbear.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.DamagedPhotoAdapter.DamagedPhotoAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProduct;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProductAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.Adapter.MinorDamagedPhotoAdapter.MinorDamagedPhotoAdapter;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksType;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksTypeAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;
import com.sangsolutions.powerbear.Singleton.StockCountProductSingleton;
import com.sangsolutions.powerbear.Tools;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class GoodsReceiptBodyFragment extends Fragment {

    private FloatingActionButton add_fab, fab_delete, fab_close_all;
    private  RecyclerView rv_products;
    // to load main recyclerView
    private  GoodsReceiptBodyAdapter goodsReceiptBodyAdapter;
    private List<GoodsReceiptBody> listMain;

    //To load Alert With product recyclerView
    private GoodsPOProductAdapter goodsPOProductAdapter;
    private  List<GoodsPOProduct> listPOProducts;

    //photo minor
    private List<String> listMinorImage;
    private MinorDamagedPhotoAdapter minorDamagedPhotoAdapter;

    //photo damaged
    private List<String> listDamagedImage;
    private DamagedPhotoAdapter damagedPhotoAdapter;

    //remarks type
    private List<RemarksType> listRemarks;
    private RemarksTypeAdapter remarksTypeAdapter;


    private DatabaseHelper helper;
    private AlertDialog mainAlertDialog;
    boolean selection_active = false,selection_active_main = false;

    private AlertDialog CameraAlertDialog;

    ImageView img_minor,img_damaged,img_close,img_forward,img_backward,img_save;
    private TextView tv_doc_no,tv_product,tv_code,tv_unit,tv_po_qty;
    private EditText et_regular_remarks,et_regular_qty,et_minor_remarks,et_minor_qty,et_damaged_remarks,et_damaged_qty;
    private Spinner sp_warehouse,sp_minor_type,sp_damage_type;
    RecyclerView rv_minor,rv_damaged;
    boolean EditMode = false;
    String DocNo = "";

    int current_position = 0;

    Animation move_down_anim, move_up_anim;

    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};


    // methods for selecting product from DocNo
    public void closeProductSelection(){
        goodsPOProductAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleProductSelection(int position) {
        goodsPOProductAdapter.toggleSelection(position);
        int count = goodsPOProductAdapter.getSelectedItemCount();
        if(count==0){
            closeProductSelection();
        }
    }
    private void enableProductActionMode(int position) {
        toggleProductSelection(position);
    }
    ///////////////////////////////////////////


    // methods for selecting main list item
    public void closeMainSelection(){
        goodsReceiptBodyAdapter.clearSelections();
        add_fab.setVisibility(View.VISIBLE);
        add_fab.startAnimation(move_up_anim);


        fab_delete.startAnimation(move_down_anim);
        fab_close_all.startAnimation(move_down_anim);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_delete.setVisibility(View.GONE);
                fab_close_all.setVisibility(View.GONE);
            }
        }, 300);
        selection_active_main = false;
    }
    private void toggleMainSelection(int position) {
        goodsReceiptBodyAdapter.toggleSelection(position);
        int count = goodsReceiptBodyAdapter.getSelectedItemCount();
        if(count==0){
            closeMainSelection();
        }

        if (count == 1 && fab_delete.getVisibility() != View.VISIBLE) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    add_fab.startAnimation(move_down_anim);
                    add_fab.setVisibility(View.GONE);

                    fab_delete.startAnimation(move_up_anim);
                    fab_close_all.startAnimation(move_up_anim);
                    fab_delete.setVisibility(View.VISIBLE);
                    fab_close_all.setVisibility(View.VISIBLE);
                }
            }, 300);
        }
    }
    private void enableMainActionMode(int position) {
        toggleMainSelection(position);
    }
    private void initFab(View view) {
        move_down_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_down);
        move_up_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_up);
        fab_delete = view.findViewById(R.id.fab_delete);
        fab_close_all = view.findViewById(R.id.fab_close_all);
        fab_delete.setVisibility(View.GONE);
        fab_close_all.setVisibility(View.GONE);
        add_fab.setVisibility(View.VISIBLE);
    }
    ///////////////////////////////////////////



    public void deleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete " + goodsReceiptBodyAdapter.getSelectedItemCount() + " items?")
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

    private void DeleteItems() {
        List<Integer> listSelectedItem = goodsReceiptBodyAdapter.getSelectedItems();

        for (int i = listSelectedItem.size() - 1; i >= 0; i--) {
            for (int j = listMain.size() - 1; j >= 0; j--) {
                if (listSelectedItem.get(i) == j)
                    listMain.remove(j);
            }
            if (i + 1 == listSelectedItem.size()) {
                goodsReceiptBodyAdapter.notifyDataSetChanged();
                GoodsReceiptBodySingleton.getInstance().setList(listMain);
                closeMainSelection();
            }
        }

    }


    //Image DeleteAlert
    public void ImageDeleteAlert(final String type, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete Image!")
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("minor")){
                            try {
                             listMinorImage.remove(pos);
                             minorDamagedPhotoAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if(type.equals("damaged")){
                            try {
                                listDamagedImage.remove(pos);
                                damagedPhotoAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }
    ///////////////////////////////////////////////////////////



    //set's main recyclerView
    public void setMainRecyclerView(){
     if(goodsPOProductAdapter.getSelectedItemCount()>0){
         List<Integer> listSelected = goodsPOProductAdapter.getSelectedItems();
         for(int i=0;i<listSelected.size();i++){
             for (int j=0;j<listPOProducts.size();j++){


                 if(listSelected.get(i)==j){
                     listMain.add(new GoodsReceiptBody(
                             listPOProducts.get(j).getDocNo(),
                             listPOProducts.get(j).getName(),
                             listPOProducts.get(j).getCode(),
                             listPOProducts.get(j).getProduct(),
                             "",
                             "",
                             helper.GetBarcodeFromIProduct(listPOProducts.get(j).getProduct()),
                             listPOProducts.get(j).getPOQty(),
                             "",
                             listPOProducts.get(j).getTempQty(),
                             listPOProducts.get(j).getUnit(),
                             "",
                             "",
                             "",
                             "",
                             "",
                             "",
                             "",
                             "",
                             ""
                     ));
                 }
             }
             if(i+1==listSelected.size()){
                 goodsReceiptBodyAdapter.notifyDataSetChanged();
                 GoodsReceiptBodySingleton.getInstance().setList(listMain);
                 closeProductSelection();
             }
         }
     }

    }
    ///////////////////////////////////////////


    @Override
    public void onPause() {
        super.onPause();
        if(CameraAlertDialog!=null&&CameraAlertDialog.isShowing()){
            CameraAlertDialog.dismiss();
        }
    }

    //Alert for selecting products from DocNo
    public void POProductSelectAlert(){
        List<String> list = GoodsReceiptPoSingleton.getInstance().getList();
        if(list!=null&&list.size()>0) {
            List<String> ListPONos = new ArrayList<>(list);
            try {
            Cursor cursor = helper.GetGoodsPOProdcut(ListPONos);

            if(cursor !=null && cursor.moveToFirst()){
                listPOProducts.clear();
                for(int i = 0; i<cursor.getCount();i++){
                    if(listMain.size()>0) {
                        for (int j = 0; j < listMain.size(); j++) {
                            if (listMain.get(j).getsPONo().equals(cursor.getString(cursor.getColumnIndex("DocNo")))
                            && listMain.get(j).getBarcode().equals(helper.GetBarcodeFromIProduct( cursor.getString(cursor.getColumnIndex("Product"))))
                            && listMain.get(j).getfPOQty().equals(cursor.getString(cursor.getColumnIndex("Qty")))
                            ) {
                                break;
                            }
                            if (listMain.size() == j + 1) {
                                if(!cursor.getString(cursor.getColumnIndex("Qty")).equals(cursor.getString(cursor.getColumnIndex("TempQty"))))
                                listPOProducts.add(new GoodsPOProduct(
                                        cursor.getString(cursor.getColumnIndex("DocNo")),
                                        cursor.getString(cursor.getColumnIndex("Name")),
                                        cursor.getString(cursor.getColumnIndex("Code")),
                                        cursor.getString(cursor.getColumnIndex("Product")),
                                        cursor.getString(cursor.getColumnIndex("Qty")),
                                        cursor.getString(cursor.getColumnIndex("TempQty")),
                                        cursor.getString(cursor.getColumnIndex("Unit"))
                                ));
                            }
                        }
                    }else {
                        if(!cursor.getString(cursor.getColumnIndex("Qty")).equals(cursor.getString(cursor.getColumnIndex("TempQty"))))
                        listPOProducts.add(new GoodsPOProduct(
                                cursor.getString(cursor.getColumnIndex("DocNo")),
                                cursor.getString(cursor.getColumnIndex("Name")),
                                cursor.getString(cursor.getColumnIndex("Code")),
                                cursor.getString(cursor.getColumnIndex("Product")),
                                cursor.getString(cursor.getColumnIndex("Qty")),
                                cursor.getString(cursor.getColumnIndex("TempQty")),
                                cursor.getString(cursor.getColumnIndex("Unit"))
                        ));
                    }
                    cursor.moveToNext();

                    if(cursor.getCount()==i+1){
                        goodsPOProductAdapter.notifyDataSetChanged();
                    }
                }
            }else {
                Log.d("list","cursor is empty");
            }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    View view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_product_select_alert,null,false);
        Button btn_apply = view.findViewById(R.id.apply);
        RecyclerView rv_product_select = view.findViewById(R.id.rv_product_selection);
        ImageView ic_close = view.findViewById(R.id.close);

        rv_product_select.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_product_select.setAdapter(goodsPOProductAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodsPOProductAdapter.getSelectedItemCount()<=0){
                    Toast.makeText(getActivity(), "Select at least one product to add!", Toast.LENGTH_SHORT).show();
                }else {
                    setMainRecyclerView();
                    alertDialog.dismiss();
                }
            }
        });

  goodsPOProductAdapter.setOnClickListener(new GoodsPOProductAdapter.OnClickListener() {
      @Override
      public void onItemClick(int pos) {
          if (selection_active) {
              enableProductActionMode(pos);
          } else {
              selection_active = true;
              enableProductActionMode(pos);
          }
      }
  });


    }
    ///////////////////////////////////////////


    //permission method
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    /////////////////////////////////////////


@SuppressLint("SetTextI18n")
public void LoadDataToMainAlert(int pos, List<Warehouse> list){
        if(mainAlertDialog!=null&&mainAlertDialog.isShowing()) {
            if (listMain.get(pos) != null) {
                try {
                    tv_doc_no.setText("Doc No : " + listMain.get(pos).getsPONo());
                    tv_product.setText("Name : " + listMain.get(pos).getName());
                    tv_code.setText("Code : " + listMain.get(pos).getCode());
                    tv_unit.setText("Unit : " + listMain.get(pos).getUnit());
                    if(listMain.get(pos).getTempQty().equals("0")) {
                        tv_po_qty.setText("Qty : " + listMain.get(pos).getfPOQty());
                    }else {try {
                        int qty=0;
                        if(EditMode) {
                            qty = Integer.parseInt(listMain.get(pos).getfPOQty());
                        }else {
                             qty = Integer.parseInt(listMain.get(pos).getfPOQty()) - Integer.parseInt(listMain.get(pos).getTempQty());
                        }
                        tv_po_qty.setText("Qty : " +qty);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    }

                    et_regular_qty.setText(listMain.get(pos).getfQty());
                    et_minor_qty.setText(listMain.get(pos).getfMinorDamageQty());
                    et_damaged_qty.setText(listMain.get(pos).getfDamagedQty());


                    et_regular_remarks.setText(listMain.get(pos).getsRemarks());
                    et_minor_remarks.setText(listMain.get(pos).getsMinorRemarks());
                    et_damaged_remarks.setText(listMain.get(pos).getsDamagedRemarks());

                        listMinorImage.clear();
                    if (!listMain.get(pos).getsMinorAttachment().equals("")) {
                        listMinorImage.addAll(Arrays.asList(listMain.get(pos).getsMinorAttachment().split(",")));
                    }else {
                        listMinorImage.clear();
                    }
                    minorDamagedPhotoAdapter.notifyDataSetChanged();

                        listDamagedImage.clear();
                    if (!listMain.get(pos).getsDamagedAttachment().equals("")) {
                        listDamagedImage.addAll(Arrays.asList(listMain.get(pos).getsDamagedAttachment().split(",")));
                    }else {
                        listDamagedImage.clear();
                    }
                    damagedPhotoAdapter.notifyDataSetChanged();


                    if(list!=null)
                    if (!listMain.get(pos).getiWarehouse().isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getMasterId().equals(listMain.get(pos).getiWarehouse())) {
                                sp_warehouse.setSelection(i);
                            }
                        }
                    }else {
                        sp_warehouse.setSelection(0);
                    }


                    if(listRemarks!=null)
                        if (!listMain.get(pos).getRemarksMinorType().isEmpty()) {
                            for (int i = 0; i < listRemarks.size(); i++) {
                                if(!listMain.get(pos).getRemarksMinorType().equals("0"))
                                if (listRemarks.get(i).getiId().equals(listMain.get(pos).getRemarksMinorType())) {
                                    sp_minor_type.setSelection(i);
                                }
                            }
                        }else {
                            sp_minor_type.setSelection(0);
                        }
                    if(listRemarks!=null)
                        if (!listMain.get(pos).getRemarksDamagedType().isEmpty()) {
                            for (int i = 0; i < listRemarks.size(); i++) {
                                if(!listMain.get(pos).getRemarksDamagedType().equals("0"))
                                if (listRemarks.get(i).getiId().equals(listMain.get(pos).getRemarksDamagedType())) {
                                    sp_damage_type.setSelection(i);
                                }
                            }
                        }else {
                            sp_damage_type.setSelection(0);
                        }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
}



    //Alert for main
    @SuppressLint("SetTextI18n")
    public void GoodsBodyMainAlert(final List<GoodsReceiptBody> listMain, final int pos){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_body_main_alert,null,false);

            img_close = view.findViewById(R.id.close_alert);
            img_forward = view.findViewById(R.id.forward);
            img_backward = view.findViewById(R.id.backward);
            img_minor = view.findViewById(R.id.minor_img);
            img_damaged = view.findViewById(R.id.damaged_img);
            img_save = view.findViewById(R.id.add_save);


            tv_doc_no = view.findViewById(R.id.doc_no);
            tv_product = view.findViewById(R.id.product);
            tv_code = view.findViewById(R.id.code);
            tv_unit = view.findViewById(R.id.unit);
            tv_po_qty = view.findViewById(R.id.po_qty);

            et_regular_remarks = view.findViewById(R.id.regular_remarks);
            et_regular_qty = view.findViewById(R.id.regular_qty);

            et_minor_remarks = view.findViewById(R.id.minor_remarks);
            et_minor_qty = view.findViewById(R.id.minor_qty);

            et_damaged_remarks = view.findViewById(R.id.damaged_remarks);
            et_damaged_qty = view.findViewById(R.id.damaged_qty);

            sp_warehouse = view.findViewById(R.id.warehouse);

            sp_minor_type = view.findViewById(R.id.minor_type);
            sp_damage_type = view.findViewById(R.id.damaged_type);

            rv_minor = view.findViewById(R.id.rv_minor);
            rv_damaged = view.findViewById(R.id.rv_damaged);

            rv_minor.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.HORIZONTAL,false));
            rv_minor.setAdapter(minorDamagedPhotoAdapter);

            rv_damaged.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.HORIZONTAL,false));
            rv_damaged.setAdapter(damagedPhotoAdapter);

            listRemarks = new ArrayList<>();
            remarksTypeAdapter = new RemarksTypeAdapter(listRemarks,requireActivity());
            sp_minor_type.setAdapter(remarksTypeAdapter);
            sp_damage_type.setAdapter(remarksTypeAdapter);


        //set remarks type
        try {
            listRemarks.clear();
            listRemarks.add(new RemarksType("0","--Select Type--"));
            Cursor cursor = helper.GetGoodsType();
            if (cursor != null && cursor.moveToFirst())
                for (int i = 0; i < cursor.getCount(); i++) {
                        listRemarks.add(new RemarksType(
                                cursor.getString(cursor.getColumnIndex("iId")),
                                cursor.getString(cursor.getColumnIndex("sName"))
                        ));

                    cursor.moveToNext();
                    if (cursor.getCount() == i + 1) {
                        remarksTypeAdapter.notifyDataSetChanged();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

            AlertDialog.Builder builder= new AlertDialog.Builder(requireActivity() ,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
            builder.setView(view);
            builder.setCancelable(false);

              mainAlertDialog = builder.create();
              mainAlertDialog.show();

              final List<Warehouse> list = new ArrayList<>();
              final WarehouseAdapter warehouseAdapter = new WarehouseAdapter(list);
              sp_warehouse.setAdapter(warehouseAdapter);

            //set warehouse
            try {
                Cursor cursor = helper.GetWarehouse();
                if (cursor != null && cursor.moveToFirst())
                    for (int i = 0; i < cursor.getCount(); i++) {
                        if (!cursor.getString(cursor.getColumnIndex("Name")).equals(" ")
                                || !cursor.getString(cursor.getColumnIndex("Name")).equals(" ")) {
                            list.add(new Warehouse(
                                    cursor.getString(cursor.getColumnIndex("MasterId")),
                                    cursor.getString(cursor.getColumnIndex("Name"))
                            ));
                        }
                        cursor.moveToNext();
                        if (cursor.getCount() == i + 1) {
                            warehouseAdapter.notifyDataSetChanged();
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

              LoadDataToMainAlert(pos,list);


            img_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listMain.size() > 1) {
                        if (current_position < listMain.size()) {
                            LoadDataToMainAlert(current_position,list);
                            current_position++;
                        }
                    }
                }
            });

            img_backward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listMain.size() > 1) {
                        if (current_position > 0) {
                            current_position--;
                            LoadDataToMainAlert(current_position,list);
                        }
                    }
                }
            });

            img_minor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraAlert("minor");
                }
            });

            img_damaged.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraAlert("damaged");
                }
            });

            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GeneralAlert("Do you want to close?","Close!","close",0);
                }
            });

            img_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   try {
                     int regular = et_regular_qty.getText().toString().isEmpty()?0:Integer.parseInt(et_regular_qty.getText().toString());
                     int minor = et_minor_qty.getText().toString().isEmpty()?0:Integer.parseInt(et_minor_qty.getText().toString());
                     int damaged = et_damaged_qty.getText().toString().isEmpty()?0:Integer.parseInt(et_damaged_qty.getText().toString());



                       boolean condition;
                       if(!EditMode){
                           condition = (regular+minor+damaged) > Integer.parseInt(listMain.get(pos).getfPOQty())-Integer.parseInt(listMain.get(pos).getTempQty());
                           Log.d("data",""+(regular+minor+damaged));
                       }else {
                           condition = Integer.parseInt(listMain.get(pos).getfPOQty()) <  (regular+minor+damaged);
                       }
                        if((regular+minor+damaged)!=0) {
                            if (condition) {
                                if (!et_regular_qty.getText().toString().isEmpty()) {
                                    et_regular_qty.setError("Error in entered Quantity");
                                }
                                if (!et_minor_qty.getText().toString().isEmpty()) {
                                    et_minor_qty.setError("Error in entered Quantity");
                                }
                                if (!et_damaged_qty.getText().toString().isEmpty()) {
                                    et_damaged_qty.setError("Error in entered Quantity");
                                }

                                Toast.makeText(getActivity(), "Quantity can't higher then PO Qty", Toast.LENGTH_SHORT).show();

                            } else {
                                String minorImage, damagedImage;

                                minorImage = TextUtils.join(",", listMinorImage);

                                damagedImage = TextUtils.join(",", listDamagedImage);


                                listMain.set(pos, new GoodsReceiptBody(
                                        listMain.get(pos).getsPONo(),
                                        listMain.get(pos).getName(),
                                        listMain.get(pos).getCode(),
                                        listMain.get(pos).getiProduct(),
                                        list.get(sp_warehouse.getSelectedItemPosition()).getName(),
                                        list.get(sp_warehouse.getSelectedItemPosition()).getMasterId(),
                                        helper.GetBarcodeFromIProduct(listMain.get(pos).getiProduct()),
                                        listMain.get(pos).getfPOQty(),
                                        et_regular_qty.getText().toString().trim(),
                                        listMain.get(pos).getTempQty(),
                                        listMain.get(pos).getUnit(),
                                        et_regular_remarks.getText().toString().trim(),
                                        et_minor_qty.getText().toString().trim(),
                                        et_minor_remarks.getText().toString().trim(),
                                        minorImage,
                                        et_damaged_qty.getText().toString().trim(),
                                        et_damaged_remarks.getText().toString().trim(),
                                        damagedImage,
                                        listRemarks.get(sp_minor_type.getSelectedItemPosition()).getiId(),
                                        listRemarks.get(sp_damage_type.getSelectedItemPosition()).getiId()
                                ));
                                GoodsReceiptBodySingleton.getInstance().setList(listMain);
                                goodsReceiptBodyAdapter.notifyDataSetChanged();
                                PublicData.clearDataIgnoreHeader();
                                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                                current_position = 0;
                                mainAlertDialog.dismiss();
                            }

                        }else {
                            Toast.makeText(getActivity(), "Enter quantity first!", Toast.LENGTH_SHORT).show();
                        }
                   }catch (Exception e){
                       e.printStackTrace();
                   }

                }
            });

            minorDamagedPhotoAdapter.setOnClickListener(new MinorDamagedPhotoAdapter.OnClickListener() {
                @Override
                public void OnDeleteListener(String photo, int position) {
                    ImageDeleteAlert("minor", position);
                }

                @Override
                public void OnImageClickListener(ImageView view, List<String> photo, int potions) {
                    }

            });


        damagedPhotoAdapter.setOnClickListener(new DamagedPhotoAdapter.OnClickListener() {
            @Override
            public void OnDeleteListener(String photo, int position) {
                ImageDeleteAlert("damaged", position);
            }
        });
    }
   //////////////////////////////////////////

    public void DeleteItem(int pos) {
        try {
            if (listMain != null) {
                listMain.remove(pos);
            }
            GoodsReceiptBodySingleton.getInstance().setList(listMain);
            goodsReceiptBodyAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //General Alert
    public void GeneralAlert(String message, String title, final String type, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("close")){
                            if (mainAlertDialog.isShowing()){
                                mainAlertDialog.dismiss();
                                current_position = 0;
                                listDamagedImage.clear();
                                listMinorImage.clear();
                            }
                        }else if(type.equals("delete_item")){
                            DeleteItem(pos);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
    /////////////////////////////////////////



    //Camera Alert
    public void CameraAlert(final String type){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.camera_layout,null,false);
        CameraView cameraView = view.findViewById(R.id.camera);
        ImageView close = view.findViewById(R.id.close);
        ImageView Click = view.findViewById(R.id.click);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        CameraAlertDialog = builder.create();
        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 100);
        }else {
            CameraAlertDialog.show();
        }
        final Fotoapparat fotoapparat;
        fotoapparat = new Fotoapparat(Objects.requireNonNull(getActivity()), cameraView);

        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 100);
        }else {
            fotoapparat.start();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoapparat.stop();
                CameraAlertDialog.dismiss();
            }
        });
        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PhotoResult photoResult = fotoapparat.takePicture();

                if (type.equals("minor")) {
                    try {
                        photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                            @Override
                            public Unit invoke(BitmapPhoto bitmapPhoto) {
                                listMinorImage.add(Tools.savePhoto(requireActivity(), photoResult));
                                CameraAlertDialog.dismiss();
                                Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                                minorDamagedPhotoAdapter.notifyDataSetChanged();
                                return Unit.INSTANCE;
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(type.equals("damaged")){
                    try {
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            listDamagedImage.add(Tools.savePhoto(requireActivity(), photoResult));
                            CameraAlertDialog.dismiss();
                            Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                            damagedPhotoAdapter.notifyDataSetChanged();
                            return Unit.INSTANCE;
                        }
                    });
                }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    ////////////////////////////////////////



    //Load values for editing operation


    private void LoadBodyValues() {
        try {
           Cursor cursor =  helper.GetGoodsBodyData(DocNo);

           if(cursor!=null&&cursor.moveToFirst()) {
               for (int i = 0; i < cursor.getCount(); i++) {
                   listMain.add(new GoodsReceiptBody(
                           cursor.getString(cursor.getColumnIndex("sPONo")),
                           helper.GetProductName(cursor.getString(cursor.getColumnIndex("iProduct"))),
                           helper.GetProductCode(cursor.getString(cursor.getColumnIndex("iProduct"))),
                           cursor.getString(cursor.getColumnIndex("iProduct")),
                           helper.GetWarehouse(cursor.getString(cursor.getColumnIndex("iWarehouse"))),
                           cursor.getString(cursor.getColumnIndex("iWarehouse")),
                           cursor.getString(cursor.getColumnIndex("Barcode")),
                           cursor.getString(cursor.getColumnIndex("fPOQty")),
                           cursor.getString(cursor.getColumnIndex("fQty")),
                           helper.GetPendingPOTempQty(cursor.getString(cursor.getColumnIndex("sPONo")),cursor.getString(cursor.getColumnIndex("iProduct"))),
                           cursor.getString(cursor.getColumnIndex("Unit")),
                           cursor.getString(cursor.getColumnIndex("sRemarks")),
                           cursor.getString(cursor.getColumnIndex("fMinorDamageQty")),
                           cursor.getString(cursor.getColumnIndex("sMinorRemarks")),
                           cursor.getString(cursor.getColumnIndex("sMinorAttachment")),
                           cursor.getString(cursor.getColumnIndex("fDamagedQty")),
                           cursor.getString(cursor.getColumnIndex("sDamagedRemarks")),
                           cursor.getString(cursor.getColumnIndex("sDamagedAttachment")),
                           cursor.getString(cursor.getColumnIndex("iMinorId")),
                           cursor.getString(cursor.getColumnIndex("iDamagedId"))

                   ));
                   cursor.moveToNext();
                   if(i+1==cursor.getCount()){
                       goodsReceiptBodyAdapter.notifyDataSetChanged();
                       GoodsReceiptBodySingleton.getInstance().setList(listMain);
                   }
               }


           }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.good_reseipt_body_fragment,container,false);
       add_fab = view.findViewById(R.id.fab_controller);
       rv_products = view.findViewById(R.id.rv_product);

        initFab(view);

       listMain = new ArrayList<>();
       listPOProducts = new ArrayList<>();

       goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(), listMain);
       goodsPOProductAdapter = new GoodsPOProductAdapter(listPOProducts,requireActivity());
       listMinorImage = new ArrayList<>();
       listDamagedImage = new ArrayList<>();

       minorDamagedPhotoAdapter = new MinorDamagedPhotoAdapter(getActivity(),listMinorImage);
       damagedPhotoAdapter = new DamagedPhotoAdapter(getActivity(),listDamagedImage);

       helper = new DatabaseHelper(requireActivity());

       rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
       rv_products.setAdapter(goodsReceiptBodyAdapter);



       Bundle bundle = getArguments();

       if(bundle!=null){
           EditMode = bundle.getBoolean("EditMode");
           DocNo = bundle.getString("DocNo");

           if(EditMode){
               LoadBodyValues();
           }
       }


       add_fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(GoodsReceiptPoSingleton.getInstance().getList()!=null&&GoodsReceiptPoSingleton.getInstance().getList().size()>0){
                   POProductSelectAlert();
               }else {
                   Toast.makeText(getActivity(), "Select PONo's", Toast.LENGTH_SHORT).show();
               }

           }
       });

       goodsReceiptBodyAdapter.setOnClickListener(new GoodsReceiptBodyAdapter.OnClickListener() {
           @Override
           public void onItemClick(GoodsReceiptBody goodsReceiptBody, int pos) {
               if (!selection_active_main) {
                   current_position = pos;
                   GoodsBodyMainAlert(listMain,pos);
               } else {
                   enableMainActionMode(pos);
               }

           }

           @Override
           public void ItemDeleteClick(GoodsReceiptBody goodsReceiptBody, int pos) {
               if (!selection_active_main) {
                   GeneralAlert("Delete this item?", "Delete!", "delete_item", pos);
               }
           }

           @Override
           public void onItemLongClick(int pos) {
               enableMainActionMode(pos);
               selection_active_main = true;
           }
       });

        fab_close_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMainSelection();
            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });

        return view;
    }



    private static class Warehouse {

        final String MasterId;
        final String Name;

        public Warehouse(String masterId, String name) {
            this.MasterId = masterId;
            this.Name = name;
        }

        public String getMasterId() {
            return MasterId;
        }

        public String getName() {
            return Name;
        }
    }


    private class WarehouseAdapter extends BaseAdapter {
        final List<Warehouse> list;

        public WarehouseAdapter(List<Warehouse> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();


        }

        @Override
        public Object getItem(int position) {
            return list.get(position).MasterId;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(requireActivity()).inflate(R.layout.warehouse_item,parent,false);
            TextView warehouse = view.findViewById(R.id.warehouse);
            try {
                warehouse.setText(list.get(position).getName());
            }catch (Exception e){
                e.printStackTrace();
            }
            return view;
        }
    }


}
