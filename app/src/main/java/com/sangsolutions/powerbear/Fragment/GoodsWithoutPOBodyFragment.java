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
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.DamagedPhotoAdapter.DamagedPhotoAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.Adapter.MinorDamagedPhotoAdapter.MinorDamagedPhotoAdapter;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksType;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksTypeAdapter;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProduct;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProductAdapter;
import com.sangsolutions.powerbear.Adapter.UnitAdapter.UnitAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.ScanDrawable;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
import com.sangsolutions.powerbear.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class GoodsWithoutPOBodyFragment extends Fragment {

private FloatingActionButton add_fab, fab_delete, fab_close_all;
private RecyclerView rv_products,rv_product_search;

    // to load main recyclerView
    private  GoodsReceiptBodyAdapter goodsReceiptBodyAdapter;
    private List<GoodsReceiptBody> listMain;

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

    private AlertDialog CameraAlertDialog;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;

    //product search
    private RecyclerView rv_search;
    private AlertDialog dialog;
    private List<SearchProduct> SearchProductList;
    private SearchProductAdapter adapter;


    ImageView img_minor,img_damaged,img_barcode,img_close,img_forward,img_backward,img_save;
    EditText et_barcode;
    Spinner sp_unit,sp_warehouse;
    private TextView product_code;
    private EditText product_name;
    private EditText et_search_input;
    private EditText et_regular_remarks,et_regular_qty,et_minor_remarks,et_minor_qty,et_damaged_remarks,et_damaged_qty;
    private Spinner sp_minor_type,sp_damage_type;
    private FrameLayout frame_scan;
    RecyclerView rv_minor,rv_damaged;

    int current_position = 0;

    boolean EditMode = false,InnerEditMode=false;
    String DocNo = "";

    AlertDialog barcodeAlertDialog;

    LinearLayout minor_damaged_linear,damaged_linear;

    Animation move_down_anim, move_up_anim;

    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    boolean selection_active = false,selection_active_main = false;

    HashMap<String,String> map;

    // methods for selecting main list item
    public void closeMainSelection(){
        goodsReceiptBodyAdapter.clearSelections();
        add_fab.setVisibility(View.VISIBLE);
        add_fab.startAnimation(move_up_anim);


        fab_delete.startAnimation(move_down_anim);
        fab_close_all.startAnimation(move_down_anim);


        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            fab_delete.setVisibility(View.GONE);
            fab_close_all.setVisibility(View.GONE);
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
            handler.postDelayed(() -> {
                add_fab.startAnimation(move_down_anim);
                add_fab.setVisibility(View.GONE);

                fab_delete.startAnimation(move_up_anim);
                fab_close_all.startAnimation(move_up_anim);
                fab_delete.setVisibility(View.VISIBLE);
                fab_close_all.setVisibility(View.VISIBLE);
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

    public void DeleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete " + goodsReceiptBodyAdapter.getSelectedItemCount() + " items?")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    DeleteItems();
                })
                .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).create()
                .show();
    }

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
                .setPositiveButton("Yes", (dialog, which) -> {
                    if(type.equals("close")){
                        if (mainAlertDialog.isShowing()){
                            mainAlertDialog.dismiss();
                            current_position = 0;
                            InnerEditMode = false;
                            listDamagedImage.clear();
                            listMinorImage.clear();
                            SearchProductList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }else if(type.equals("delete_item")){
                       DeleteItem(pos);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
    //////////////////////////////////////////


    //Load values for editing operation
    private void LoadBodyValues() {
        try {
            Cursor cursor =  helper.GetGoodsWithoutPOBodyData(DocNo);

            if(cursor!=null&&cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    listMain.add(new GoodsReceiptBody(
                           "",
                            helper.GetProductName(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            helper.GetProductCode(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            cursor.getString(cursor.getColumnIndex("iProduct")),
                            helper.GetWarehouse(cursor.getString(cursor.getColumnIndex("iWarehouse"))),
                            cursor.getString(cursor.getColumnIndex("iWarehouse")),
                            cursor.getString(cursor.getColumnIndex("Barcode")),
                            "",
                            cursor.getString(cursor.getColumnIndex("fQty")),
                            "",
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


    @Override
    public void onPause() {
        super.onPause();
        if(CameraAlertDialog!=null&&CameraAlertDialog.isShowing()){
            CameraAlertDialog.dismiss();
        }
        try {
            if (barcodeAlertDialog!=null&&barcodeAlertDialog.isShowing()) {
                if (cameraSource != null) {
                    try {
                        cameraSource.stop();
                    } catch (NullPointerException e) {
                        cameraSource = null;
                    }
                }
                barcodeAlertDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SetUnit(String units, int position) {
        List<String> list;
        list = Arrays.asList(units.split("\\s*,\\s*"));
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        sp_unit.setAdapter(unitAdapter);

       if (listMain.size() > 0) {
            if (position != -1)
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(listMain.get(position).getUnit())) {
                        sp_unit.setSelection(i);
                    }
                }
        }
    }

    private void InitialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(requireActivity())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(requireActivity(), barcodeDetector)
                .setRequestedPreviewSize(1080, 1920)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if (!barcodeDetector.isOperational()) {
                    Log.d("Detector", "Detector dependencies are not yet available.");
                } else {
                    try {
                        if (cameraSource != null) {
                            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            cameraSource.start(surfaceView.getHolder());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                //  barcodeDetector.release();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d("msg", "Barcode scanning stopped");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() > 0) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(barcodeAlertDialog!=null&&barcodeAlertDialog.isShowing()){
                                barcodeAlertDialog.dismiss();
                            }
                            et_barcode.setText(barcode.valueAt(0).displayValue);
                            SetUnit(helper.GetProductUnit(barcode.valueAt(0).displayValue),-1);
                        }
                    });
                }
            }
        });
    }


    public void BarcodeAlert(){
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.barcode_layout,null,false);
        ImageView img_close = view.findViewById(R.id.goodsReceipt);
        surfaceView = view.findViewById(R.id.surfaceView);
        frame_scan = view.findViewById(R.id.frame_scan);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        barcodeAlertDialog = builder.create();
        Objects.requireNonNull(barcodeAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        barcodeAlertDialog.show();

        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 100);
        } else {
            frame_scan.setVisibility(View.VISIBLE);
            frame_scan.setForeground(new ScanDrawable(requireActivity(), 15));
            InitialiseDetectorsAndSources();
        }

        img_close.setOnClickListener(v -> {
            if(barcodeAlertDialog.isShowing()){
                barcodeAlertDialog.dismiss();
            }
        });

    }


    @SuppressLint("SetTextI18n")
    private void GetProductInfoToMap(String barcode,int position) {

        Cursor cursor = helper.GetProductInfo(barcode);

        if (cursor != null) {
            cursor.moveToFirst();
            map.put("Name",cursor.getString(cursor.getColumnIndex("Name")));
            map.put("Code",cursor.getString(cursor.getColumnIndex("Code")));
            map.put("Unit",cursor.getString(cursor.getColumnIndex("Unit")));

            map.put("iProduct",cursor.getString(cursor.getColumnIndex("MasterId")));
            product_name.setText(map.get("Name"));
            product_code.setText("Code : "+map.get("Code"));
            SetUnit(helper.GetProductUnit(barcode),position);

            SearchProductList.clear();
            adapter.notifyDataSetChanged();

            et_regular_qty.requestFocus();
            new Handler().postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_regular_qty,InputMethodManager.SHOW_FORCED);
            },100);

        }else {
            map.put("Name","");
            map.put("Code","");
            map.put("Unit","");
            map.put("iProduct","");
            product_name.setText("");
            SearchProductList.clear();
            adapter.notifyDataSetChanged();
            product_code.setText("Code : ");

        }
    }

    //change image orientation
    private void rotation_image(Bitmap bm, ImageView image_clicked) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, 500, 500, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
        image_clicked.setImageBitmap(rotatedBitmap);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(image_clicked);
        photoViewAttacher.setZoomable(true);
    }
   ////////////////////////////

    //Show image
    private void imageView(List<String> photo,final int position) {
        View view1=LayoutInflater.from(requireActivity()).inflate(R.layout.imageview_goods_receipt,null,false);
        ImageView image_clicked=view1.findViewById(R.id.image_view_clicked);
        ImageView backward=view1.findViewById(R.id.backward);
        ImageView forward=view1.findViewById(R.id.forward);
        final int[] image_current_position = {position};
        AlertDialog.Builder builder1=new AlertDialog.Builder(requireActivity(),android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                .setView(view1);
        AlertDialog dialog=builder1.create();
        dialog.show();
        Bitmap bm = BitmapFactory.decodeFile(photo.get(position));

        rotation_image(bm,image_clicked);

        forward.setOnClickListener(v -> {
            if(photo.size()>0) {
                if (image_current_position[0] < photo.size()) {
                    Bitmap bm1 = BitmapFactory.decodeFile(photo.get(image_current_position[0]));
                    rotation_image(bm1, image_clicked);
                    image_current_position[0]++;

                }
            }
        });
        backward.setOnClickListener(v -> {
            if(photo.size()>0){
                if(image_current_position[0]>0){
                    image_current_position[0]--;
                    Bitmap bm12 = BitmapFactory.decodeFile(photo.get(image_current_position[0]));
                    rotation_image(bm12,image_clicked);
                }
            }
        });


    }
    ////////////////////////////////////////////
    //Image DeleteAlert
    public void ImageDeleteAlert(final String type, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete Image!")
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes", (dialog, which) -> {
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
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show();

    }
    ///////////////////////////////////////////////////////////

    //Load item Values for editing
    @SuppressLint("SetTextI18n")
    public void LoadDataToMainAlert(int pos, List<Warehouse> listWarehouse){
        if(mainAlertDialog!=null&&mainAlertDialog.isShowing()){
            try {
                if(listMain.get(pos)!=null){

                    et_barcode.setText(listMain.get(pos).getBarcode());

                    et_regular_qty.setText(listMain.get(pos).getfQty());
                    et_minor_qty.setText(listMain.get(pos).getfMinorDamageQty());
                    et_damaged_qty.setText(listMain.get(pos).getfDamagedQty());
                    if(listRemarks.get(pos).getiId().equals(listMain.get(pos).getRemarksMinorType())){
                        sp_minor_type.setSelection(pos);
                    }
                    if(listRemarks.get(pos).getiId().equals(listMain.get(pos).getRemarksDamagedType())){
                        sp_damage_type.setSelection(pos);
                    }


                    if (!listMain.get(pos).getfQty().isEmpty()) {
                        et_regular_remarks.setVisibility(View.VISIBLE);
                    } else {
                        et_regular_remarks.setVisibility(View.GONE);
                    }

                    if (!listMain.get(pos).getfMinorDamageQty().isEmpty()) {
                        et_minor_remarks.setVisibility(View.VISIBLE);
                        minor_damaged_linear.setVisibility(View.VISIBLE);
                        sp_minor_type.setEnabled(true);
                    } else {
                        et_minor_remarks.setVisibility(View.GONE);
                        minor_damaged_linear.setVisibility(View.GONE);
                        sp_minor_type.setEnabled(false);
                    }
                    if (!listMain.get(pos).getfDamagedQty().isEmpty()) {
                        et_damaged_remarks.setVisibility(View.VISIBLE);
                        damaged_linear.setVisibility(View.VISIBLE);
                        sp_damage_type.setEnabled(true);
                    } else {
                        et_damaged_remarks.setVisibility(View.GONE);
                        damaged_linear.setVisibility(View.GONE);
                        sp_damage_type.setEnabled(false);
                    }

                    et_regular_remarks.setText(listMain.get(pos).getsRemarks());
                    et_minor_remarks.setText(listMain.get(pos).getsMinorRemarks());
                    et_damaged_remarks.setText(listMain.get(pos).getsDamagedRemarks());

                    listMinorImage.clear();
                    if (!listMain.get(pos).getsMinorAttachment().equals("")) {
                        listMinorImage.addAll(Arrays.asList(listMain.get(pos).getsMinorAttachment().split(",")));
                    } else {
                        listMinorImage.clear();
                    }
                    minorDamagedPhotoAdapter.notifyDataSetChanged();

                    listDamagedImage.clear();
                    if (!listMain.get(pos).getsDamagedAttachment().equals("")) {
                        listDamagedImage.addAll(Arrays.asList(listMain.get(pos).getsDamagedAttachment().split(",")));
                    } else {
                        listDamagedImage.clear();
                    }
                    damagedPhotoAdapter.notifyDataSetChanged();

                    if (listWarehouse != null)
                        if (!listMain.get(pos).getiWarehouse().isEmpty()) {
                            for (int i = 0; i < listWarehouse.size(); i++) {
                                if (listWarehouse.get(i).getMasterId().equals(listMain.get(pos).getiWarehouse())) {
                                    sp_warehouse.setSelection(i);
                                }
                            }
                        } else {
                            sp_warehouse.setSelection(0);
                        }

                    if (listRemarks != null)
                        if (!listMain.get(pos).getRemarksMinorType().isEmpty()) {
                            for (int i = 0; i < listRemarks.size(); i++) {
                                if (!listMain.get(pos).getRemarksMinorType().equals("0"))
                                    if (listRemarks.get(i).getiId().equals(listMain.get(pos).getRemarksMinorType())) {
                                        sp_minor_type.setSelection(i);
                                    }
                            }
                        } else {
                            sp_minor_type.setSelection(0);
                        }

                    if (listRemarks != null)
                        if (!listMain.get(pos).getRemarksDamagedType().isEmpty()) {
                            for (int i = 0; i < listRemarks.size(); i++) {
                                if (!listMain.get(pos).getRemarksDamagedType().equals("0"))
                                    if (listRemarks.get(i).getiId().equals(listMain.get(pos).getRemarksDamagedType())) {
                                        sp_damage_type.setSelection(i);
                                    }
                            }
                        } else {
                            sp_damage_type.setSelection(0);
                        }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    //Alert for main
    @SuppressLint("SetTextI18n")
    public void GoodsBodyMainAlert(final List<GoodsReceiptBody> listMain, final int pos){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_without_po_main_alert,null,false);
        img_close = view.findViewById(R.id.close_alert);
        img_forward = view.findViewById(R.id.forward);
        img_backward = view.findViewById(R.id.backward);
        img_minor = view.findViewById(R.id.minor_img);
        img_damaged = view.findViewById(R.id.damaged_img);
        img_save = view.findViewById(R.id.add_save);
        img_barcode = view.findViewById(R.id.ic_barcode);

        et_barcode = view.findViewById(R.id.barcode);
        sp_unit = view.findViewById(R.id.unit);
        sp_warehouse = view.findViewById(R.id.warehouse);
        product_name = view.findViewById(R.id.et_product);
        product_code = view.findViewById(R.id.code);

        et_regular_remarks = view.findViewById(R.id.regular_remarks);
        et_regular_qty = view.findViewById(R.id.regular_qty);

        et_minor_remarks = view.findViewById(R.id.minor_remarks);
        et_minor_qty = view.findViewById(R.id.minor_qty);

        et_damaged_remarks = view.findViewById(R.id.damaged_remarks);
        et_damaged_qty = view.findViewById(R.id.damaged_qty);

        sp_minor_type = view.findViewById(R.id.minor_type);
        sp_damage_type = view.findViewById(R.id.damaged_type);

        rv_minor = view.findViewById(R.id.rv_minor);
        rv_damaged = view.findViewById(R.id.rv_damaged);

        damaged_linear=view.findViewById(R.id.damaged_linear);
        minor_damaged_linear=view.findViewById(R.id.minor_damage_linear);

        rv_product_search = view.findViewById(R.id.rv_product_edit);
        rv_product_search.setLayoutManager(new LinearLayoutManager(requireContext()));

        rv_minor.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.HORIZONTAL,false));
        rv_minor.setAdapter(minorDamagedPhotoAdapter);

        rv_damaged.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.HORIZONTAL,false));
        rv_damaged.setAdapter(damagedPhotoAdapter);

        listRemarks = new ArrayList<>();
        remarksTypeAdapter = new RemarksTypeAdapter(listRemarks,requireActivity());
        sp_minor_type.setAdapter(remarksTypeAdapter);
        sp_damage_type.setAdapter(remarksTypeAdapter);

        ImageView img_search = view.findViewById(R.id.search);

        sp_minor_type.setEnabled(false);
        sp_damage_type.setEnabled(false);
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

        img_close.setOnClickListener(v -> GeneralAlert("Do you want to close?","Close!","close",0));

        img_barcode.setOnClickListener(v -> {
            if (!hasPermissions(getActivity(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 100);
            } else {
            BarcodeAlert();
            }
        });

        img_save.setOnClickListener(v -> {
            if(et_regular_qty.getText().toString().equals("")){
                et_regular_remarks.setText("");

            }
            if(et_damaged_qty.getText().toString().equals("")){
                et_damaged_remarks.setText("");
                if(listDamagedImage.size()>0) {
                    listDamagedImage.clear();
                }
            }
            if(et_minor_qty.getText().toString().equals("")){
                et_minor_remarks.setText("");
                if(listMinorImage.size()>0) {
                    listMinorImage.clear();
                }
            }

            try {
                int regular = et_regular_qty.getText().toString().isEmpty() ? 0 : Integer.parseInt(et_regular_qty.getText().toString());
                int minor = et_minor_qty.getText().toString().isEmpty() ? 0 : Integer.parseInt(et_minor_qty.getText().toString());
                int damaged = et_damaged_qty.getText().toString().isEmpty() ? 0 : Integer.parseInt(et_damaged_qty.getText().toString());

                if((regular+minor+damaged)<=0){
                    Toast.makeText(getActivity(), "Enter Quantity to save!", Toast.LENGTH_SHORT).show();
                }else if(et_barcode.getText().toString().equals("")||helper.GetNameFromBarcode(et_barcode.getText().toString()).equals("")){
                    et_barcode.setError("Enter valid barcode!");
                }else {
                    String minorImage, damagedImage,barcode;

                    minorImage = TextUtils.join(",", listMinorImage);

                    damagedImage = TextUtils.join(",", listDamagedImage);

                    barcode = et_barcode.getText().toString();

                    if(!InnerEditMode){
                        listMain.add(new GoodsReceiptBody(
                                "",
                                helper.GetNameFromBarcode(barcode),
                                helper.GetCodeFromBarcode(barcode),
                                helper.GetMasterIdFromBarcode(barcode),
                                list.get(sp_warehouse.getSelectedItemPosition()==-1?0:sp_warehouse.getSelectedItemPosition()).getName(),
                                list.get(sp_warehouse.getSelectedItemPosition()==-1?0:sp_warehouse.getSelectedItemPosition()).getMasterId(),
                                barcode,
                                "",
                                et_regular_qty.getText().toString().trim(),
                                "",
                                sp_unit.getSelectedItem().toString(),
                                et_regular_remarks.getText().toString().trim(),
                                et_minor_qty.getText().toString().trim(),
                                et_minor_remarks.getText().toString().trim(),
                                minorImage,
                                et_damaged_qty.getText().toString().trim(),
                                et_damaged_remarks.getText().toString().trim(),
                                damagedImage,
                                listRemarks.get(sp_minor_type.getSelectedItemPosition()==-1?0:sp_minor_type.getSelectedItemPosition()).getiId(),
                                listRemarks.get(sp_damage_type.getSelectedItemPosition()==-1?0:sp_damage_type.getSelectedItemPosition()).getiId()
                        ));
                    }else {
                       listMain.set(current_position,new GoodsReceiptBody(
                               "",
                               helper.GetNameFromBarcode(barcode),
                               helper.GetCodeFromBarcode(barcode),
                               helper.GetMasterIdFromBarcode(barcode),
                               list.get(sp_warehouse.getSelectedItemPosition()==-1?0:sp_warehouse.getSelectedItemPosition()).getName(),
                               list.get(sp_warehouse.getSelectedItemPosition()==-1?0:sp_warehouse.getSelectedItemPosition()).getMasterId(),
                               barcode,
                               "",
                               et_regular_qty.getText().toString().trim(),
                               "",
                               sp_unit.getSelectedItem().toString(),
                               et_regular_remarks.getText().toString().trim(),
                               et_minor_qty.getText().toString().trim(),
                               et_minor_remarks.getText().toString().trim(),
                               minorImage,
                               et_damaged_qty.getText().toString().trim(),
                               et_damaged_remarks.getText().toString().trim(),
                               damagedImage,
                               listRemarks.get(sp_minor_type.getSelectedItemPosition()==-1?0:sp_minor_type.getSelectedItemPosition()).getiId(),
                               listRemarks.get(sp_damage_type.getSelectedItemPosition()==-1?0:sp_damage_type.getSelectedItemPosition()).getiId()
                       ));
                    }

                    GoodsReceiptBodySingleton.getInstance().setList(listMain);
                    goodsReceiptBodyAdapter.notifyDataSetChanged();
                    PublicData.clearDataIgnoreHeader();
                    Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                    current_position = 0;
                    InnerEditMode = false;
                    listDamagedImage.clear();
                    listMinorImage.clear();
                    mainAlertDialog.dismiss();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            });

        et_barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetProductInfoToMap(s.toString(),pos);
            }
        });

        img_search.setOnClickListener(v -> searchAlert());

        minorDamagedPhotoAdapter.setOnClickListener(new MinorDamagedPhotoAdapter.OnClickListener() {
            @Override
            public void OnDeleteListener(String photo, int position) {
                ImageDeleteAlert("minor", position);
            }


            @Override
            public void OnImageClickListener(ImageView view, List<String> photo, int position) {
                imageView(photo,position);
            }

        });


        img_minor.setOnClickListener(v -> CameraAlert("minor"));

        img_damaged.setOnClickListener(v -> CameraAlert("damaged"));

        img_forward.setOnClickListener(v -> {
            if (listMain.size() > 1) {
                if (current_position+1 < listMain.size()) {
                    current_position++;
                    LoadDataToMainAlert(current_position,list);
                }
            }
        });

        img_backward.setOnClickListener(v -> {
            if (listMain.size() > 1) {
                if (current_position > 0 &&current_position<listMain.size()) {
                    current_position--;
                    LoadDataToMainAlert(current_position,list);
                }
            }
        });

        damagedPhotoAdapter.setOnClickListener(new DamagedPhotoAdapter.OnClickListener() {
            @Override
            public void OnDeleteListener(String photo, int position) {
                ImageDeleteAlert("damaged", position);
            }

            @Override
            public void onImageClickListener(List<String> photo, int position) {
                imageView(photo,position);
            }
        });

        if(!et_regular_qty.getText().toString().equals("")){
            et_regular_remarks.setVisibility(View.VISIBLE);
        }
        if(!et_minor_qty.getText().toString().equals("")){
            et_minor_remarks.setVisibility(View.VISIBLE);
            minor_damaged_linear.setVisibility(View.VISIBLE);
            sp_minor_type.setEnabled(true);
        }
        if(!et_damaged_qty.getText().toString().equals("")){
            et_damaged_remarks.setVisibility(View.VISIBLE);
            damaged_linear.setVisibility(View.VISIBLE);
            sp_damage_type.setEnabled(true);
        }

        et_regular_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null){
                    et_regular_remarks.setVisibility(View.VISIBLE);
                }
            }
        });
        et_minor_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    et_minor_remarks.setVisibility(View.VISIBLE);
                    minor_damaged_linear.setVisibility(View.VISIBLE);
                    sp_minor_type.setEnabled(true);
                    if(s.toString().equals("")){
                        sp_minor_type.setSelection(0);
                        sp_minor_type.setEnabled(false);
                    }

                }
            }
        });
        et_damaged_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null){
                    et_damaged_remarks.setVisibility(View.VISIBLE);
                    damaged_linear.setVisibility(View.VISIBLE);
                    sp_damage_type.setEnabled(true);
                    if(s.toString().equals("")){
                        sp_damage_type.setSelection(0);
                        sp_damage_type.setEnabled(false);
                    }
                }
            }
        });

        if(InnerEditMode){
            LoadDataToMainAlert(pos,list);
        }

        product_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            ProductSearch(s.toString(),"autoComplete");
            }
        });
    }

    private void ProductSearch(String keyword,String type) {
        SearchProductList.clear();
        Cursor cursor = helper.SearchProduct(keyword);
        if(type.equals("autoComplete")){
            if (cursor != null && !keyword.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Code = cursor.getString(cursor.getColumnIndex("Code"));
                    String Barcode = cursor.getString(cursor.getColumnIndex("Barcode"));
                    SearchProductList.add(new SearchProduct(Name, Code, Barcode));
                    cursor.moveToNext();

                    if (i + 1 == cursor.getCount()) {
                        rv_product_search.setAdapter(adapter);
                    }

                    adapter.setOnClickListener(search_item -> {
                        et_barcode.setText(search_item.getBarcode());
                        product_name.setText(search_item.getName());
                        SetUnit(helper.GetProductUnit(search_item.getBarcode()), -1);
                        SearchProductList.clear();
                        adapter.notifyDataSetChanged();
                    });
                }

            } else {
                SearchProductList.clear();
                SearchProductList.add(new SearchProduct("No Products available!", "", ""));
                rv_product_search.setAdapter(adapter);

            }
        }else {
        if (dialog.isShowing()) {

            if (cursor != null && !keyword.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Code = cursor.getString(cursor.getColumnIndex("Code"));
                    String Barcode = cursor.getString(cursor.getColumnIndex("Barcode"));
                    SearchProductList.add(new SearchProduct(Name, Code, Barcode));
                    cursor.moveToNext();

                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(adapter);
                    }

                    adapter.setOnClickListener(search_item -> {
                        et_barcode.setText(search_item.getBarcode());
                        SetUnit(helper.GetProductUnit(search_item.getBarcode()), -1);
                        dialog.dismiss();
                    });
                }

            } else {
                SearchProductList.clear();
                SearchProductList.add(new SearchProduct("No Products available!", "", ""));
                rv_search.setAdapter(adapter);

            }
        }
        }
    }

    //product search
    public void searchAlert(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_layout, null);
        et_search_input = view.findViewById(R.id.search_edit);
        rv_search = view.findViewById(R.id.search_recycler);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        if (dialog.isShowing()) {

            et_search_input.setOnFocusChangeListener((v, hasFocus) -> et_search_input.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(inputMethodManager).showSoftInput(et_search_input, InputMethodManager.SHOW_IMPLICIT);
                }
            }));
            et_search_input.requestFocus();
            et_search_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ProductSearch(s.toString(),"type");
                }
            });

        }
    }

    ////////////////////////////////////////

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


    //Camera Alert
    public void CameraAlert(final String type){

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
        close.setOnClickListener(view1 -> {
            fotoapparat.stop();
            CameraAlertDialog.dismiss();
        });
        Click.setOnClickListener(view12 -> {
            Click.setClickable(false);
            final PhotoResult photoResult = fotoapparat.takePicture();

            if (type.equals("minor")) {
                try {
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            Click.setClickable(true);
                            listMinorImage.add(Tools.savePhoto(requireActivity(), photoResult));
                            CameraAlertDialog.dismiss();
                            Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                            minorDamagedPhotoAdapter.notifyDataSetChanged();
                            return Unit.INSTANCE;
                        }
                    });
                }catch (Exception e){
                    Click.setClickable(true);
                    e.printStackTrace();
                }
            }else if(type.equals("damaged")){
                try {
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            Click.setClickable(true);
                            listDamagedImage.add(Tools.savePhoto(requireActivity(), photoResult));
                            CameraAlertDialog.dismiss();
                            Toast.makeText(getActivity(), "picture taken!", Toast.LENGTH_SHORT).show();
                            damagedPhotoAdapter.notifyDataSetChanged();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View view = LayoutInflater.from(requireActivity()).inflate(R.layout.goods_without_po_body_fragment,container,false);
        helper = new DatabaseHelper(requireActivity());
        add_fab = view.findViewById(R.id.fab_controller);
        rv_products = view.findViewById(R.id.rv_product);
        initFab(view);
        map = new HashMap<>();
        listMain = new ArrayList<>();
        goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(), listMain);
        rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv_products.setAdapter(goodsReceiptBodyAdapter);

        SearchProductList = new ArrayList<>();
        adapter = new SearchProductAdapter(getActivity(), SearchProductList);

        listMinorImage = new ArrayList<>();
        listDamagedImage = new ArrayList<>();

        minorDamagedPhotoAdapter = new MinorDamagedPhotoAdapter(getActivity(),listMinorImage);
        damagedPhotoAdapter = new DamagedPhotoAdapter(getActivity(),listDamagedImage);

        Bundle bundle = getArguments();

        if(bundle!=null){
            EditMode = bundle.getBoolean("EditMode");
            DocNo = bundle.getString("DocNo");

            if(EditMode){
                //TODO load main list with data
                LoadBodyValues();
            }
        }

        add_fab.setOnClickListener(v -> {
            InnerEditMode = false;
            GoodsBodyMainAlert(listMain,-1);
        });

        goodsReceiptBodyAdapter.setOnClickListener(new GoodsReceiptBodyAdapter.OnClickListener() {
            @Override
            public void onItemClick(GoodsReceiptBody goodsReceiptBody, int pos) {
                if (!selection_active_main) {
                    current_position=pos;
                    InnerEditMode = true;
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

        fab_close_all.setOnClickListener(view1 -> closeMainSelection());

        fab_delete.setOnClickListener(view12 -> DeleteAlert());

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
