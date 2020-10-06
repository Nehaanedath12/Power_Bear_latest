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
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;
import com.sangsolutions.powerbear.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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

    ImageView img_minor,img_damaged,img_close,img_forward,img_backward,img_save;
    TextView tv_doc_no,tv_product,tv_code,tv_unit,tv_po_qty;
    EditText et_regular_remarks,et_regular_qty,et_minor_remarks,et_minor_qty,et_damaged_remarks,et_damaged_qty;
    Spinner sp_warehouse;



    int current_position = 0;
    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE};


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
                             listPOProducts.get(j).getProduct(),
                             "",
                             "",
                             helper.GetBarcodeFromIProduct(listPOProducts.get(j).getProduct()),
                             listPOProducts.get(j).getPOQty(),
                             "",
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


public void LoadDataToMainAlert(int pos,List<Warehouse> list){
        if(mainAlertDialog!=null&&mainAlertDialog.isShowing()) {
            if (listMain.get(pos) != null) {
                try {
                    tv_doc_no.setText("Doc No : " + listMain.get(pos).getsPONo());
                    tv_product.setText("Name : " + listMain.get(pos).getName());
                    tv_code.setText("Code : " + listMain.get(pos).getCode());
                    tv_unit.setText("Unit : " + listMain.get(pos).getUnit());
                    tv_po_qty.setText("PO Qty : " + listMain.get(pos).getfPOQty());


                    et_regular_qty.setText(listMain.get(pos).getfQty());
                    et_minor_qty.setText(listMain.get(pos).getfMinorDamageQty());
                    et_damaged_qty.setText(listMain.get(pos).getfDamagedQty());


                    et_regular_remarks.setText(listMain.get(pos).getsRemarks());
                    et_minor_remarks.setText(listMain.get(pos).getsMinorRemarks());
                    et_damaged_remarks.setText(listMain.get(pos).getsDamagedRemarks());


                    if (!listMain.get(pos).getsMinorAttachment().equals("")){
                    Bitmap minorBitmap = BitmapFactory.decodeFile(listMain.get(pos).getsMinorAttachment());
                    if (minorBitmap != null) {
                        img_minor.setImageBitmap(minorBitmap);
                    }
                    }else {
                        img_minor.setImageResource(R.drawable.ic_camera);
                    }

                    if (!listMain.get(pos).getsDamagedAttachment().equals("")) {
                        Bitmap damagedBitmap = BitmapFactory.decodeFile(listMain.get(pos).getsDamagedAttachment());
                        if (damagedBitmap != null) {
                            img_damaged.setImageBitmap(damagedBitmap);
                        }
                    }else {
                        img_damaged.setImageResource(R.drawable.ic_camera);
                    }
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
                GeneralAlert("Do you want to close?","Close!","close");
            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (et_regular_qty.getText().toString().isEmpty()) {
                        et_regular_qty.setError("Enter regular qty!");
                        Toast.makeText(getActivity(), "Entry error!", Toast.LENGTH_SHORT).show();


                    } else if (Integer.parseInt(et_regular_qty.getText().toString().trim()) > Integer.parseInt(listMain.get(pos).getfPOQty())) {
                        et_regular_qty.setError("Quantity can't higher then PO Qty");
                        Toast.makeText(getActivity(), "Entry error!", Toast.LENGTH_SHORT).show();

                    } else {

                        if(!et_minor_qty.getText().toString().trim().isEmpty())
                        if (Integer.parseInt(et_minor_qty.getText().toString().trim()) > Integer.parseInt(listMain.get(pos).getfPOQty())) {
                            et_minor_qty.setError("Quantity can't higher then PO Qty");
                            Toast.makeText(getActivity(), "Entry error!", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if(!et_damaged_qty.getText().toString().trim().isEmpty())
                        if (Integer.parseInt(et_damaged_qty.getText().toString().trim()) > Integer.parseInt(listMain.get(pos).getfPOQty())) {
                            et_damaged_qty.setError("Quantity can't higher then PO Qty");
                            Toast.makeText(getActivity(), "Entry error!", Toast.LENGTH_SHORT).show();
                            return;
                        }

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
                                listMain.get(pos).getUnit(),
                                et_regular_remarks.getText().toString().trim(),
                                et_minor_qty.getText().toString().trim(),
                                et_minor_remarks.getText().toString().trim(),
                                PublicData.image_minor,
                                et_damaged_qty.getText().toString().trim(),
                                et_damaged_remarks.getText().toString().trim(),
                                PublicData.image_damaged
                        ));
                        goodsReceiptBodyAdapter.notifyDataSetChanged();
                        PublicData.clearData();
                        Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                        current_position=0;
                        mainAlertDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
   //////////////////////////////////////////


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
                                current_position = 0;
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
                final PhotoResult photoResult = fotoapparat.takePicture();

                if (type.equals("minor")) {
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            PublicData.image_minor = Tools.savePhoto(requireActivity(), photoResult);
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                            img_minor.setImageBitmap(bitmapPhoto.bitmap);
                            return Unit.INSTANCE;
                        }
                    });

                }else if(type.equals("damaged")){
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            PublicData.image_damaged = Tools.savePhoto(requireActivity(), photoResult);
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                            img_damaged.setImageBitmap(bitmapPhoto.bitmap);
                            return Unit.INSTANCE;
                        }
                    });
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
            warehouse.setText(list.get(position).Name);
            return view;
        }
    }


}
