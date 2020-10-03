package com.sangsolutions.powerbear.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProduct;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProductAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POList;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;
import com.sangsolutions.powerbear.Tools;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

public class GoodsReceiptBodyFragment extends Fragment {
    FloatingActionButton add_fab;
    RecyclerView rv_products;
    // to load main recyclerView
    GoodsReceiptBodyAdapter goodsReceiptBodyAdapter;
    List<GoodsReceiptBody> listMain;

    //To load Alert With product recyclerView
    GoodsPOProductAdapter goodsPOProductAdapter;
    List<GoodsPOProduct> listPOProducts;

    DatabaseHelper helper;
    AlertDialog mainAlertDialog;
    boolean selection_active = false;

    ImageView img_minor ;
    ImageView img_damaged;

    String[] PERMISSIONS = {Manifest.permission.CAMERA};


    // methods for selecting product from DocNo
    public void closeSelection(){
        goodsPOProductAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleSelection(int position) {
        goodsPOProductAdapter.toggleSelection(position);
        int count = goodsPOProductAdapter.getSelectedItemCount();
        if(count==0){
            closeSelection();
        }
    }
    private void enableActionMode(int position) {
        toggleSelection(position);
    }
    ///////////////////////////////////////////


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
                             "",
                             helper.GetBarcodeFromIProduct(listPOProducts.get(j).getProduct()),
                             listPOProducts.get(j).getPOQty(),
                             "0",
                             listPOProducts.get(j).getUnit(),
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
                 closeSelection();
             }
         }
     }

    }
    ///////////////////////////////////////////


    //Alert for selecting products from DocNo
    public void POProductSelectAlert(){
        List<POList> list = GoodsReceiptPoSingleton.getInstance().getList();
        List<String> ListPONos = new ArrayList<>();
        if(list!=null&&list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                ListPONos.add(list.get(i).getDocNo());
            }
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
                                listPOProducts.add(new GoodsPOProduct(
                                        cursor.getString(cursor.getColumnIndex("DocNo")),
                                        cursor.getString(cursor.getColumnIndex("Name")),
                                        cursor.getString(cursor.getColumnIndex("Code")),
                                        cursor.getString(cursor.getColumnIndex("Product")),
                                        cursor.getString(cursor.getColumnIndex("Qty")),
                                        cursor.getString(cursor.getColumnIndex("Unit"))
                                ));
                            }
                        }
                    }else {
                        listPOProducts.add(new GoodsPOProduct(
                                cursor.getString(cursor.getColumnIndex("DocNo")),
                                cursor.getString(cursor.getColumnIndex("Name")),
                                cursor.getString(cursor.getColumnIndex("Code")),
                                cursor.getString(cursor.getColumnIndex("Product")),
                                cursor.getString(cursor.getColumnIndex("Qty")),
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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
              enableActionMode(pos);
          } else {
              selection_active = true;
              enableActionMode(pos);
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

    //Alert for main
    @SuppressLint("SetTextI18n")
    public void GoodsBodyMainAlert(List<GoodsReceiptBody> listMain, int pos){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_body_main_alert,null,false);

        ImageView img_close = view.findViewById(R.id.close_alert);
        ImageView img_forward = view.findViewById(R.id.forward);
        ImageView img_backward = view.findViewById(R.id.backward);
         img_minor = view.findViewById(R.id.minor_img);
         img_damaged = view.findViewById(R.id.damaged_img);


        TextView tv_doc_no = view.findViewById(R.id.doc_no);
        TextView tv_product = view.findViewById(R.id.product);
        TextView tv_code = view.findViewById(R.id.code);
        TextView tv_unit = view.findViewById(R.id.unit);
        TextView tv_po_qty = view.findViewById(R.id.po_qty);

        EditText et_regular_remarks = view.findViewById(R.id.regular_remarks);
        EditText et_regular_qty = view.findViewById(R.id.regular_qty);

        EditText et_minor_remarks = view.findViewById(R.id.minor_remarks);
        EditText et_minor_qty = view.findViewById(R.id.minor_qty);

        EditText et_damaged_remarks = view.findViewById(R.id.damaged_remarks);
        EditText et_damaged_qty = view.findViewById(R.id.damaged_qty);

        Spinner sp_warehouse = view.findViewById(R.id.warehouse);

        AlertDialog.Builder builder= new AlertDialog.Builder(requireActivity() ,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(view);
        builder.setCancelable(false);

          mainAlertDialog = builder.create();
          mainAlertDialog.show();

          List<Warehouse> list = new ArrayList<>();
          WarehouseAdapter warehouseAdapter = new WarehouseAdapter(list);
          sp_warehouse.setAdapter(warehouseAdapter);

          if(listMain.get(pos)!=null){
              tv_doc_no.setText("Doc No : "+listMain.get(pos).getsPONo());
              tv_product.setText("Name : "+listMain.get(pos).getName());
              tv_code.setText("Code : "+listMain.get(pos).getCode());
              tv_unit.setText("Unit : "+listMain.get(pos).getUnit());
              tv_po_qty.setText("PO Qty : "+listMain.get(pos).getfPOQty());


              //set warehouse
              try {
                  Cursor cursor = helper.GetWarehouse();
                  if(cursor!=null&&cursor.moveToFirst())
                  for (int i = 0; i<cursor.getCount();i++){
                      if(!cursor.getString(cursor.getColumnIndex("Name")).equals(" ")
                              || !cursor.getString(cursor.getColumnIndex("Name")).equals(" ")){
                      list.add(new Warehouse(
                         cursor.getString(cursor.getColumnIndex("MasterId")),
                              cursor.getString(cursor.getColumnIndex("Name"))
                      ));}
                      cursor.moveToNext();
                      if(cursor.getCount()==i+1){
                          warehouseAdapter.notifyDataSetChanged();
                      }
                  }
              }catch (Exception e){
                  e.printStackTrace();
              }
              //////////////////////////////////////////////////
          }


        img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        img_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                GeneralAlert("Do you want to close?","Close!","close");
            }
        });

    }
   ///////////////////////////////////////////


    //General Alert
    public void GeneralAlert(String message, String title, final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("close")){
                            if (mainAlertDialog.isShowing()){
                                mainAlertDialog.dismiss();
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
    /////////////////////////////////////////


    //Camera Alert
    public void CameraAlert(final String type){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.camera_layout,null,false);
        CameraView cameraView = view.findViewById(R.id.camera);
        ImageView close = view.findViewById(R.id.close);
        ImageView Click = view.findViewById(R.id.click);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 100);
        }else {
            alertDialog.show();
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
                alertDialog.dismiss();
            }
        });
        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoResult photoResult = fotoapparat.takePicture();
                if (type.equals("minor")) {
                    String filePath = Tools.savePhoto(requireActivity(), photoResult);
                    Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), 100, 100);
                     img_minor.setImageBitmap(bm);


                }else if(type.equals("damaged")){
                    String filePath = Tools.savePhoto(requireActivity(), photoResult);
                    Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), 100, 100);
                        img_damaged.setImageBitmap(bm);

                }
            }
        });

    }

    ////////////////////////////////////////


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.good_reseipt_body_fragment,container,false);
       add_fab = view.findViewById(R.id.fab_controller);
       rv_products = view.findViewById(R.id.rv_product);
       listMain = new ArrayList<>();
       listPOProducts = new ArrayList<>();

       goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(), listMain);
       goodsPOProductAdapter = new GoodsPOProductAdapter(listPOProducts,requireActivity());

       helper = new DatabaseHelper(requireActivity());

       rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
       rv_products.setAdapter(goodsReceiptBodyAdapter);
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
               GoodsBodyMainAlert(listMain,pos);
           }
       });
       return view;
    }

    private static class Warehouse {

        final String MasterId;
        final String Name;

        public Warehouse(String masterId, String name) {
            MasterId = masterId;
            Name = name;
        }

        public String getMasterId() {
            return MasterId;
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
            warehouse.setText(list.get(position).Name);
            return view;
        }
    }


}
