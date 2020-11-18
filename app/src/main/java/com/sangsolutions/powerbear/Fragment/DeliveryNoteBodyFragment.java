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
import com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody;
import com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBodyAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProduct;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProductAdapter;
import com.sangsolutions.powerbear.Adapter.MinorDamagedPhotoAdapter.MinorDamagedPhotoAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteBodySingleton;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteSOSingleton;
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

public class DeliveryNoteBodyFragment extends Fragment {
    private FloatingActionButton add_fab, fab_delete, fab_close_all;
    private RecyclerView rv_products;
    // to load main recyclerView
    private DeliveryNoteBodyAdapter deliveryNoteBodyAdapter;
    private List<DeliveryNoteBody> listMain;

    //To load Alert With product recyclerView
    private GoodsPOProductAdapter deliverySOProductAdapter;
    private  List<GoodsPOProduct> listSOProducts;
    boolean productSelection = false;

    //photo minor
    private List<String> listImage;
    private MinorDamagedPhotoAdapter PhotoAdapter;


    private DatabaseHelper helper;
    private AlertDialog mainAlertDialog;
    boolean selection_active = false,selection_active_main = false;

    private AlertDialog CameraAlertDialog;

    ImageView img_close,img_forward,img_backward,img_save,img_photo;
    private TextView tv_doc_no,tv_product,tv_code,tv_unit, tv_so_qty;
    private EditText et_regular_remarks,et_regular_qty;
    private Spinner sp_warehouse;
    RecyclerView rv_photo;
    boolean EditMode = false;
    String DocNo = "";

    int current_position = 0;

    Animation move_down_anim, move_up_anim;

    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};


    // methods for selecting product from DocNo
    public void closeProductSelection(){
        deliverySOProductAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleProductSelection(int position) {
        deliverySOProductAdapter.toggleSelection(position);
        int count = deliverySOProductAdapter.getSelectedItemCount();
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
        deliveryNoteBodyAdapter.clearSelections();
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
        deliveryNoteBodyAdapter.toggleSelection(position);
        int count = deliveryNoteBodyAdapter.getSelectedItemCount();
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
                .setMessage("Do you want to delete " + deliveryNoteBodyAdapter.getSelectedItemCount() + " items?")
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
        List<Integer> listSelectedItem = deliveryNoteBodyAdapter.getSelectedItems();

        for (int i = listSelectedItem.size() - 1; i >= 0; i--) {
            for (int j = listMain.size() - 1; j >= 0; j--) {
                if (listSelectedItem.get(i) == j)
                    listMain.remove(j);
            }
            if (i + 1 == listSelectedItem.size()) {
                deliveryNoteBodyAdapter.notifyDataSetChanged();
                DeliveryNoteBodySingleton.getInstance().setList(listMain);
                closeMainSelection();
            }
        }

    }


    //Image DeleteAlert
    public void ImageDeleteAlert(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete Image!")
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            try {
                                listImage.remove(pos);
                                PhotoAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
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
        if(deliverySOProductAdapter.getSelectedItemCount()>0){
            List<Integer> listSelected = deliverySOProductAdapter.getSelectedItems();
            for(int i=0;i<listSelected.size();i++){
                for (int j = 0; j< listSOProducts.size(); j++){


                    if(listSelected.get(i)==j){
                        listMain.add(new DeliveryNoteBody(
                                listSOProducts.get(j).getDocNo(),
                                listSOProducts.get(j).getPOQty(),
                                listSOProducts.get(j).getName(),
                                listSOProducts.get(j).getCode(),
                                helper.GetPendingSOTempQty(listSOProducts.get(j).getDocNo(),listSOProducts.get(j).getProduct()),
                                listSOProducts.get(j).getProduct(),
                                "",
                                "",
                                "",
                                "",
                                "",
                                listSOProducts.get(j).getUnit()
                        ));
                    }
                }
                if(i+1==listSelected.size()){
                    deliveryNoteBodyAdapter.notifyDataSetChanged();
                    DeliveryNoteBodySingleton.getInstance().setList(listMain);
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
    public void SOProductSelectAlert(){
        List<String> list = DeliveryNoteSOSingleton.getInstance().getList();
        if(list!=null&&list.size()>0) {
            List<String> ListSONos = new ArrayList<>(list);
            try {
                Cursor cursor = helper.GetDeliverySOProdcut(ListSONos);

                if(cursor !=null && cursor.moveToFirst()){
                    listSOProducts.clear();
                    for(int i = 0; i<cursor.getCount();i++){
                        if(listMain.size()>0) {
                            for (int j = 0; j < listMain.size(); j++) {
                                if (listMain.get(j).getsSONo().equals(cursor.getString(cursor.getColumnIndex("DocNo")))
                                        && listMain.get(j).getiProduct().equals( cursor.getString(cursor.getColumnIndex("Product")))
                                        && listMain.get(j).getfSOQty().equals(cursor.getString(cursor.getColumnIndex("Qty")))
                                ) {
                                    break;
                                }
                                if (listMain.size() == j + 1) {
                                    if(!cursor.getString(cursor.getColumnIndex("Qty")).equals(cursor.getString(cursor.getColumnIndex("TempQty"))))
                                        listSOProducts.add(new GoodsPOProduct(
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
                                listSOProducts.add(new GoodsPOProduct(
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
                            deliverySOProductAdapter.notifyDataSetChanged();
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
        FloatingActionButton fab_select = view.findViewById(R.id.select);
        RecyclerView rv_product_select = view.findViewById(R.id.rv_product_selection);
        ImageView ic_close = view.findViewById(R.id.goodsReceipt);

        rv_product_select.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_product_select.setAdapter(deliverySOProductAdapter);
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
                if(deliverySOProductAdapter.getSelectedItemCount()<=0){
                    Toast.makeText(getActivity(), "Select at least one product to add!", Toast.LENGTH_SHORT).show();
                }else {
                    setMainRecyclerView();
                    alertDialog.dismiss();
                }
            }
        });

        deliverySOProductAdapter.setOnClickListener(new GoodsPOProductAdapter.OnClickListener() {
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
        fab_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listSOProducts.size()>0)
                    if(productSelection){
                        deliverySOProductAdapter.DeselectAll();
                        productSelection =false;
                    }else {
                        deliverySOProductAdapter.SelectAll();
                        productSelection = true;
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
                    tv_doc_no.setText("Doc No : " + listMain.get(pos).getsSONo());
                    tv_product.setText("Name : " + listMain.get(pos).getsName());
                    tv_code.setText("Code : " + listMain.get(pos).getsCode());
                    tv_unit.setText("Unit : " + listMain.get(pos).getUnit());
                    if(listMain.get(pos).getfTempQty().equals("0")) {
                        tv_so_qty.setText("Qty : " + listMain.get(pos).getfSOQty());
                    }else {
                        try {
                        int qty=0;
                        if(EditMode) {
                          int sum = Integer.parseInt(listMain.get(pos).getfSOQty()) - Integer.parseInt(listMain.get(pos).getfTempQty().equals("") ? "0" : listMain.get(pos).getfTempQty());

                            if(sum==0){
                                qty  =   Integer.parseInt(listMain.get(pos).getfSOQty()) - Integer.parseInt(listMain.get(pos).getfQty().equals("")?"0":listMain.get(pos).getfQty());

                                if(qty==0){
                                    qty = Integer.parseInt(listMain.get(pos).getfQty().equals("")?"0":listMain.get(pos).getfQty());
                                }
                            }else {
                                qty  =   Integer.parseInt(listMain.get(pos).getfTempQty()) + sum;
                            }
                        }else {
                            qty = Integer.parseInt(listMain.get(pos).getfSOQty()) - Integer.parseInt(listMain.get(pos).getfTempQty().equals("") ? "0" : listMain.get(pos).getfTempQty());
                        }
                        tv_so_qty.setText("Qty : " +qty);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    }

                    et_regular_qty.setText(listMain.get(pos).getfQty());


                    et_regular_remarks.setText(listMain.get(pos).getsRemarks());


                    listImage.clear();
                    if (!listMain.get(pos).getsAttachment().equals("")) {
                        listImage.addAll(Arrays.asList(listMain.get(pos).getsAttachment().split(",")));
                    }else {
                        listImage.clear();
                    }
                    PhotoAdapter.notifyDataSetChanged();

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
    public void DeliveryBodyMainAlert(final List<DeliveryNoteBody> listMain, final int pos){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delivery_body_main_alert,null,false);

        img_close = view.findViewById(R.id.close_alert);
        img_forward = view.findViewById(R.id.forward);
        img_backward = view.findViewById(R.id.backward);
        img_save = view.findViewById(R.id.add_save);
        img_photo = view.findViewById(R.id.photo);

        tv_doc_no = view.findViewById(R.id.doc_no);
        tv_product = view.findViewById(R.id.product);
        tv_code = view.findViewById(R.id.code);
        tv_unit = view.findViewById(R.id.unit);
        tv_so_qty = view.findViewById(R.id.po_qty);

        et_regular_remarks = view.findViewById(R.id.regular_remarks);
        et_regular_qty = view.findViewById(R.id.regular_qty);

        sp_warehouse = view.findViewById(R.id.warehouse);

        rv_photo = view.findViewById(R.id.rv_minor);

        rv_photo.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.HORIZONTAL,false));
        rv_photo.setAdapter(PhotoAdapter);




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
                    if (current_position+1 < listMain.size()) {
                        current_position++;
                        LoadDataToMainAlert(current_position,list);
                    }
                }
            }
        });

        img_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listMain.size() > 1) {
                    if (current_position > 0 &&current_position<listMain.size()) {
                        current_position--;
                        LoadDataToMainAlert(current_position,list);
                    }
                }
            }
        });

        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraAlert();
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




                    boolean condition;
                    if(!EditMode){
                        condition = regular > Integer.parseInt(listMain.get(pos).getfSOQty())-Integer.parseInt(listMain.get(pos).getfTempQty().equals("")?"0":listMain.get(pos).getfTempQty());
                        Log.d("data",""+(listMain.get(pos).getfTempQty()));
                    }else {
                        condition = Integer.parseInt(listMain.get(pos).getfSOQty()) <  regular;
                    }
                    if((regular)!=0) {
                        if (condition) {
                            if (!et_regular_qty.getText().toString().isEmpty()) {
                                et_regular_qty.setError("Error in entered Quantity");
                            }

                            Toast.makeText(getActivity(), "Quantity can't higher then SO Qty", Toast.LENGTH_SHORT).show();

                        } else {
                            String Image;

                            Image = TextUtils.join(",", listImage);


                            /*listMain.set(pos, new GoodsReceiptBody(
                                    listMain.get(pos).getsSONo(),
                                    listMain.get(pos).getsName(),
                                    listMain.get(pos).getsCode(),
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
                            ));*/

                            listMain.set(pos,new DeliveryNoteBody(
                                    listMain.get(pos).getsSONo(),
                                    listMain.get(pos).getfSOQty(),
                                    listMain.get(pos).getsName(),
                                    listMain.get(pos).getsCode(),
                                    listMain.get(pos).getfTempQty(),
                                    listMain.get(pos).getiProduct(),
                                    helper.GetWarehouse(list.get(sp_warehouse.getSelectedItemPosition()).getMasterId()),
                                    list.get(sp_warehouse.getSelectedItemPosition()).getMasterId(),
                                    Image,
                                    et_regular_remarks.getText().toString().trim(),
                                    et_regular_qty.getText().toString().trim(),
                                    listMain.get(pos).getUnit()));

                            DeliveryNoteBodySingleton.getInstance().setList(listMain);
                            deliveryNoteBodyAdapter.notifyDataSetChanged();
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

        PhotoAdapter.setOnClickListener(new MinorDamagedPhotoAdapter.OnClickListener() {
            @Override
            public void OnDeleteListener(String photo, int position) {
                ImageDeleteAlert(position);
            }

            @Override
            public void OnImageClickListener(ImageView view, List<String> photo, int potions) {
            }

        });

    }
    //////////////////////////////////////////

    public void DeleteItem(int pos) {
        try {
            if (listMain != null) {
                listMain.remove(pos);
            }
            DeliveryNoteBodySingleton.getInstance().setList(listMain);
            deliveryNoteBodyAdapter.notifyDataSetChanged();
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
                                listImage.clear();
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
    public void CameraAlert(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.camera_layout,null,false);
        CameraView cameraView = view.findViewById(R.id.camera);
        ImageView close = view.findViewById(R.id.goodsReceipt);
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
                Click.setClickable(false);
                final PhotoResult photoResult = fotoapparat.takePicture();

                    try {
                        photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                            @Override
                            public Unit invoke(BitmapPhoto bitmapPhoto) {
                                Click.setClickable(true);
                                listImage.add(Tools.savePhoto(requireActivity(), photoResult));
                                CameraAlertDialog.dismiss();
                                Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                                PhotoAdapter.notifyDataSetChanged();
                                return Unit.INSTANCE;
                            }
                        });
                    }catch (Exception e){
                        Click.setClickable(true);
                        e.printStackTrace();
                    }

                }

        });

    }

    ////////////////////////////////////////



    //Load values for editing operation


    private void LoadBodyValues() {
        try {
            Cursor cursor =  helper.GetDeliveryBodyData(DocNo);

            if(cursor!=null&&cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    listMain.add(new DeliveryNoteBody(
                            cursor.getString(cursor.getColumnIndex("sSONo")),
                            cursor.getString(cursor.getColumnIndex("sSOQty")),
                            helper.GetProductName(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            helper.GetProductCode(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            helper.GetPendingSOTempQty(cursor.getString(cursor.getColumnIndex("sSONo")),cursor.getString(cursor.getColumnIndex("iProduct"))),
                            cursor.getString(cursor.getColumnIndex("iProduct")),
                            helper.GetWarehouse(cursor.getString(cursor.getColumnIndex("iWarehouse"))),
                            cursor.getString(cursor.getColumnIndex("iWarehouse")),
                            cursor.getString(cursor.getColumnIndex("sAttachment")),
                            cursor.getString(cursor.getColumnIndex("sRemarks")),
                            cursor.getString(cursor.getColumnIndex("fQty")),
                            cursor.getString(cursor.getColumnIndex("Unit"))));

                    cursor.moveToNext();
                    if(i+1==cursor.getCount()){
                        deliveryNoteBodyAdapter.notifyDataSetChanged();
                        DeliveryNoteBodySingleton.getInstance().setList(listMain);
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
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.delivery_note_body_fragment,container,false);
        add_fab = view.findViewById(R.id.fab_controller);
        rv_products = view.findViewById(R.id.rv_product);

        initFab(view);

        listMain = new ArrayList<>();
        listSOProducts = new ArrayList<>();

        deliveryNoteBodyAdapter = new DeliveryNoteBodyAdapter(requireActivity(), listMain);
        deliverySOProductAdapter = new GoodsPOProductAdapter(listSOProducts,requireActivity());
        listImage = new ArrayList<>();


        PhotoAdapter = new MinorDamagedPhotoAdapter(getActivity(), listImage);

        helper = new DatabaseHelper(requireActivity());

        rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv_products.setAdapter(deliveryNoteBodyAdapter);



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
                if(DeliveryNoteSOSingleton.getInstance().getList()!=null&& DeliveryNoteSOSingleton.getInstance().getList().size()>0){
                    SOProductSelectAlert();
                }else {
                    Toast.makeText(getActivity(), "Select SONo's", Toast.LENGTH_SHORT).show();
                }

            }
        });


        deliveryNoteBodyAdapter.setOnClickListener(new DeliveryNoteBodyAdapter.OnClickListener() {
            @Override
            public void onItemClick(DeliveryNoteBody Body, int pos) {
                if (!selection_active_main) {
                    current_position = pos;
                    DeliveryBodyMainAlert(listMain,pos);
                } else {
                    enableMainActionMode(pos);
                }
            }

            @Override
            public void ItemDeleteClick(DeliveryNoteBody body, int pos) {
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
        final List<DeliveryNoteBodyFragment.Warehouse> list;

        public WarehouseAdapter(List<DeliveryNoteBodyFragment.Warehouse> list) {
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
