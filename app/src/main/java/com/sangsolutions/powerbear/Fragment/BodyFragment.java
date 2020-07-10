package com.sangsolutions.powerbear.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;
import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProductAdapter;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProduct;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProductAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.StockCount;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;
import com.sangsolutions.powerbear.Tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BodyFragment extends Fragment {
    private FloatingActionButton fab_controller;
    private LinearLayout barcodeLinear, linear_search;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private SurfaceView surfaceView;
    private EditText et_barcode, et_search_input, qty;
    private DatabaseHelper helper;
    private RelativeLayout rl_showProductInfo;
    private HashMap<String, String> map;
    private TextView product_name, product_code;
    private RecyclerView rv_search, rv_product;
    private AlertDialog dialog;
    private List<SearchProduct> productList;
    private SearchProductAdapter adapter;
    private ListProductAdapter listProductAdapter;
    private List<ListProduct> list;
    private ImageView add_new,save;
    private boolean EditMode = false,EditModeInner = false;
    private String voucherNo = "";
    private int EditPosition = -1;
    private String warehouse_id = "";
    Date c;
private     @SuppressLint("SimpleDateFormat") SimpleDateFormat df;

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }




    @Override
    public void onStop() {
        super.onStop();
        if (cameraSource != null)
            cameraSource.release();

    }



    private void Save(){
      String s_date="",s_remarks="";

            s_date= PublicData.date;

           s_remarks=PublicData.remakes;



        List<ListProduct> list = StockCountSingleton.getInstance().getList();

        if(!s_date.isEmpty()&&!list.isEmpty())
        {
            String str_date,s_voucher_no,str_remarks;
            if(!EditMode){
                str_date = s_date;
                s_voucher_no = helper.GetNewVoucherNo();
                str_remarks = s_remarks;
            }else {
                str_date = s_date;
                s_voucher_no = voucherNo;
                str_remarks = s_remarks;
            }
            StockCount s = new StockCount();
            if(EditMode) {
                helper.DeleteStockCount(voucherNo);
            }
            for(int i = 0 ; i < list.size(); i ++){
                s.setiVoucherNo(s_voucher_no);
                s.setdDate(str_date);
                s.setiWarehouse(warehouse_id);
                s.setiProduct(list.get(i).getiProduct());
                s.setfQty(list.get(i).getQty());
                s.setsUnit(list.get(i).getUnit());
                s.setsRemarks(str_remarks);
                s.setdProcessedDate(df.format(c));
                s.setiStatus("0");

                helper.InsertStockCount(s);


                if(list.size()==i+1){
                    Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                    StockCountSingleton.getInstance().clearList();
                    Objects.requireNonNull(getActivity()).finish();
                }

            }

        }else {
            Toast.makeText(getActivity(), "some data is missing", Toast.LENGTH_SHORT).show();
        }

    }


    private void SaveAlert(){
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save?")
                .setMessage("Do you want't to save?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Save();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    private void setRecyclerView() {
        String Name, Code, Qty;

        Qty = map.get("Qty");

if(!EditModeInner) {
    list.add(new ListProduct(map.get("Name"), map.get("Code"), Qty, map.get("Unit"), map.get("iProduct")));
}else {
    if(EditPosition!= -1)
    list.set(EditPosition,new ListProduct(map.get("Name"), map.get("Code"), Qty, map.get("Unit"), map.get("iProduct")));
    EditModeInner = false;
    EditPosition = -1 ;
}
        rl_showProductInfo.setVisibility(View.GONE);
        qty.setText("");
        et_barcode.setText("");
        map.clear();
        listProductAdapter.notifyDataSetChanged();
        StockCountSingleton.getInstance().setList(list);
    }

    private void setDataForEditing(String voucherNo){
        Cursor cursor = helper.GetHeaderData(voucherNo);

        if(cursor!=null && cursor.moveToFirst()){
            warehouse_id = cursor.getString(cursor.getColumnIndex("iWarehouse"));
        }

    }

    private void SetRecyclerFromDB(String voucherNo) {
        Cursor cursor = helper.GetBodyData(voucherNo);
        String name,qty,code,iProduct,unit;
        if(cursor!=null){
            if(cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    iProduct = cursor.getString(cursor.getColumnIndex("iProduct"));
                    name = helper.GetProductName(iProduct);
                    code = helper.GetProductCode(iProduct);
                    qty = cursor.getString(cursor.getColumnIndex("fQty"));
                    unit = cursor.getString(cursor.getColumnIndex("sUnit"));

                    list.add(new ListProduct(name, code, qty, unit, iProduct));
                    cursor.moveToNext();

                    if (cursor.getCount() == i + 1) {
                        adapter.notifyDataSetChanged();
                        StockCountSingleton.getInstance().setList(list);
                    }
                }
            }else {
                Objects.requireNonNull(getActivity()).finish();
                Log.d("error","Body have no data to load!");
            }
        }
    }

    private void SetDataToEdit(ListProduct product, int pos) {
    et_barcode.setText(helper.GetBarcodeFromIProduct(product.getiProduct()));
    qty.setText(product.getQty());
    EditPosition = pos;
    }
    private void DeleteStockCountItemAlert(final ListProduct product, final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("Delete Product!")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(pos);
                        StockCountSingleton.getInstance().setList(list);
                        listProductAdapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           dialog.dismiss();
            }
        }).create().show();
    }
    private void InitialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(Objects.requireNonNull(getActivity()), barcodeDetector)
                .setRequestedPreviewSize(1080, 1920)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (!barcodeDetector.isOperational()) {
                    Log.d("Detector", "Detector dependencies are not yet available.");
                } else {
                    try {
                        if (cameraSource != null) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //barcodeDetector.release();
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
                            et_barcode.setText(barcode.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });
    }
    private void ProductSearch(String keyword) {
        productList.clear();
        if (dialog.isShowing()) {
            Cursor cursor = helper.SearchProduct(keyword);
            if (cursor != null&&!keyword.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Code = cursor.getString(cursor.getColumnIndex("Code"));
                    String Barcode = cursor.getString(cursor.getColumnIndex("Barcode"));
                    productList.add(new SearchProduct(Name,Code,Barcode));
                    cursor.moveToNext();

                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(adapter);
                    }

                    adapter.setOnClickListener(new SearchProductAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(View view, SearchProduct search_item, int pos) {
                            et_barcode.setText(search_item.getBarcode());
                            dialog.dismiss();
                        }
                    });
                }

            } else {
                productList.clear();
                productList.add(new SearchProduct("No Products available!", "",""));
                rv_search.setAdapter(adapter);

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void GetProductInfoToMap(String barcode) {

        Cursor cursor = helper.GetProductInfo(barcode);

        if (cursor != null) {
            rl_showProductInfo.setVisibility(View.VISIBLE);
            cursor.moveToFirst();
            map.put("Name",cursor.getString(cursor.getColumnIndex("Name")));
            map.put("Code",cursor.getString(cursor.getColumnIndex("Code")));
            map.put("Unit",cursor.getString(cursor.getColumnIndex("Unit")));
            map.put("iProduct",cursor.getString(cursor.getColumnIndex("MasterId")));
            product_name.setText("Name : "+map.get("Name"));
            product_code.setText(map.get("Code"));
        }else {

            rl_showProductInfo.setVisibility(View.GONE);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.body_frgment, container, false);

        fab_controller =view.findViewById(R.id.fab_controller);
        barcodeLinear = view.findViewById(R.id.linear_scan);
        linear_search = view.findViewById(R.id.linear_search);
        et_barcode = view.findViewById(R.id.et_barcode);
        surfaceView = view.findViewById(R.id.surfaceView);
        product_name= view.findViewById(R.id.product_name);
        product_code = view.findViewById(R.id.product_code);
        save = view.findViewById(R.id.save);
        qty = view.findViewById(R.id.qty);
        add_new = view.findViewById(R.id.add_new);
        rv_product = view.findViewById(R.id.rv_product);
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy-MM-dd");
        rv_product.setLayoutManager(new LinearLayoutManager(getActivity()));
        map = new HashMap<>();



        helper = new DatabaseHelper(getActivity());
        rl_showProductInfo = view.findViewById(R.id.details);
        productList = new ArrayList<>();
        adapter = new SearchProductAdapter(getActivity(), productList);
        list = new ArrayList<>();
        listProductAdapter = new ListProductAdapter(getActivity(),list);


        if(getArguments() != null) {
            EditMode = getArguments().getBoolean("EditMode");
            voucherNo = getArguments().getString("voucherNo");
            warehouse_id = getArguments().getString("warehouse");
            if (EditMode) {
                setDataForEditing(voucherNo);
                SetRecyclerFromDB(voucherNo);
            }
        }


        rv_product.setAdapter(listProductAdapter);

        listProductAdapter.setOnClickListener(new ListProductAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view,final ListProduct product,final int pos) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.edit_delete_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                          DeleteStockCountItemAlert(product, pos);
                        } else if (item.getItemId() == R.id.edit) {

                            SetDataToEdit(product,pos);
                            EditModeInner = true;
                        }

                        return true;

                    }
                });
            }
        });


        fab_controller.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Animation move_down_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.move_down);
                Animation move_up_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
                Animation clock_wise_rotate_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.clock_wise_rotate);
                Animation anti_clock_wise_rotate_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anti_clock_wise_rotate);
                if (linear_search.getVisibility() == View.VISIBLE && barcodeLinear.getVisibility() == View.VISIBLE) {
                    linear_search.startAnimation(move_down_anim);
                    barcodeLinear.startAnimation(move_down_anim);
                    linear_search.setVisibility(View.GONE);
                    barcodeLinear.setVisibility(View.GONE);
                    fab_controller.startAnimation(anti_clock_wise_rotate_anim);
                    fab_controller.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_add));
                } else {
                    linear_search.setVisibility(View.VISIBLE);
                    barcodeLinear.setVisibility(View.VISIBLE);
                    linear_search.startAnimation(move_up_anim);
                    barcodeLinear.startAnimation(move_up_anim);
                    fab_controller.startAnimation(clock_wise_rotate_anim);
                    fab_controller.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_close_30dp));
                }
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
                GetProductInfoToMap(s.toString());
            }
        });

        barcodeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (surfaceView.getVisibility() == View.VISIBLE) {
                    if (cameraSource != null) {
                        try {
                            cameraSource.stop();
                        } catch (NullPointerException e) {
                            cameraSource = null;
                        }

                    }
                    surfaceView.setVisibility(View.GONE);
                } else {
                    surfaceView.setVisibility(View.VISIBLE);
                    if (!hasPermissions(getActivity(), PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 100);
                    } else {
                        InitialiseDetectorsAndSources();
                    }
                }
            }
        });

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!map.isEmpty() && !qty.getText().toString().isEmpty()) {
                    map.put("Qty", qty.getText().toString());
                            setRecyclerView();
                }else {
                    if (map.isEmpty()) {
                        Toast.makeText(getActivity(), "No product", Toast.LENGTH_SHORT).show();
                    }
                    if (qty.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter Quantity", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        linear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_layout, null);
                et_search_input = view.findViewById(R.id.search_edit);
                rv_search = view.findViewById(R.id.search_recycler);
                rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                builder.setView(view);
                dialog = builder.create();
                dialog.show();

                if (dialog.isShowing()) {

                    et_search_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            et_search_input.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    Objects.requireNonNull(inputMethodManager).showSoftInput(et_search_input, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                        }
                    });
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
                            ProductSearch(s.toString());
                        }
                    });

                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAlert();
            }
        });

        return view;
    }




}
