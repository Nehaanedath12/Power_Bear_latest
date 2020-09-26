package com.sangsolutions.powerbear.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.sangsolutions.powerbear.Adapter.UnitAdapter.UnitAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.ScanDrawable;
import com.sangsolutions.powerbear.Singleton.StockCountProductSingleton;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BodyFragment extends Fragment {
    private FloatingActionButton fab_controller, fab_delete, fab_close_all;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private SurfaceView surfaceView;
    private EditText et_search_input, qty;
    private DatabaseHelper helper;
    private RelativeLayout rl_showProductInfo;
    private HashMap<String, String> map;
    private TextView product_name, product_code;
    private RecyclerView rv_search, rv_product;
    private AlertDialog dialog;
    private List<SearchProduct> SearchproductList;
    private SearchProductAdapter adapter;
    private ListProductAdapter listProductAdapter;
    private List<ListProduct> listProduct;
    private ImageView add_new, save;
    private String EditMode = "";
    private boolean EditModeInner = false;
    private String voucherNo = "";
    private int EditPosition = -1;
    private String warehouse_id = "";
    private EditText et_barcode;
    private EditText et_remarks, et_qty;
    private AlertDialog alertDialog;
    private ImageView search;
    private Date c;
    private FrameLayout frame_scan;
    int current_position = 0;
    private Spinner sp_unit;
    Animation move_down_anim, move_up_anim;//clock_wise_rotate_anim,//anti_clock_wise_rotate_anim;
    private boolean selection_active = false;
    private @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df;

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


    public void SetUnit(String units,int position) {
        List<String> list;
        list = Arrays.asList(units.split("\\s*,\\s*"));
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        sp_unit.setAdapter(unitAdapter);

        if(listProduct.size()>0){
            if(position != -1)
            for(int i = 0 ;i< list.size();i++){
                if(list.get(i).equals(listProduct.get(position).getUnit())){
                    sp_unit.setSelection(i);
                }
            }
        }
    }

    public void deleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete!")
                .setMessage("Do you want to delete " + listProductAdapter.getSelectedItemCount() + " items?")
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
        List<Integer> listSelectedItem = listProductAdapter.getSelectedItems();

        for (int i = listSelectedItem.size() - 1; i >= 0; i--) {
            for (int j = listProduct.size() - 1; j >= 0; j--) {
                if (listSelectedItem.get(i) == j)
                    listProduct.remove(j);
            }
            if (i + 1 == listSelectedItem.size()) {
                listProductAdapter.notifyDataSetChanged();
                StockCountProductSingleton.getInstance().setList(listProduct);
                closeSelection();
            }
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (cameraSource != null)
            cameraSource.release();

    }

    private void initFab(View view) {
        move_down_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_down);
        move_up_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_up);
        fab_delete = view.findViewById(R.id.fab_delete);
        fab_close_all = view.findViewById(R.id.fab_close_all);
        fab_delete.setVisibility(View.GONE);
        fab_close_all.setVisibility(View.GONE);
        fab_controller.setVisibility(View.VISIBLE);
    }

    public void closeSelection() {
        listProductAdapter.clearSelections();


        fab_controller.setVisibility(View.VISIBLE);
        fab_controller.startAnimation(move_up_anim);


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


        selection_active = false;
    }

    private void toggleSelection(int position) {
        listProductAdapter.toggleSelection(position);
        int count = listProductAdapter.getSelectedItemCount();
        if (count == 0) {
            closeSelection();
        }

        if (count == 1 && fab_delete.getVisibility() != View.VISIBLE) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab_controller.startAnimation(move_down_anim);
                    fab_controller.setVisibility(View.GONE);

                    fab_delete.startAnimation(move_up_anim);
                    fab_close_all.startAnimation(move_up_anim);
                    fab_delete.setVisibility(View.VISIBLE);
                    fab_close_all.setVisibility(View.VISIBLE);
                }
            }, 300);
        }

    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }


    private void AddNewAlert(final int position) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.add_stock_count_product_alert, null, false);
        final ImageView close, add, barcode;
        ImageView btn_forward, btn_backward;


        product_name = view.findViewById(R.id.product_name);
        product_code = view.findViewById(R.id.product_code);
        rl_showProductInfo = view.findViewById(R.id.details);
        surfaceView = view.findViewById(R.id.surfaceView);
        frame_scan = view.findViewById(R.id.frame_scan);
        btn_forward = view.findViewById(R.id.forward);
        btn_backward =view.findViewById(R.id.backward);
        et_remarks = view.findViewById(R.id.narration);
        et_qty = view.findViewById(R.id.qty);
        et_barcode = view.findViewById(R.id.barcode);
        add = view.findViewById(R.id.new_item);
        search = view.findViewById(R.id.search);
        sp_unit = view.findViewById(R.id.unit);
        barcode = view.findViewById(R.id.img_barcode);
        close = view.findViewById(R.id.close_alert);

        AlertDialog.Builder builder= new AlertDialog.Builder(requireActivity(),android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                current_position = -1;
                EditModeInner = false;
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
                GetProductInfoToMap(s.toString(),position);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAlert();
            }
        });


        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (frame_scan.getVisibility() == View.VISIBLE) {
                    if (cameraSource != null) {
                        try {
                            cameraSource.stop();
                        } catch (NullPointerException e) {
                            cameraSource = null;
                        }

                    }
                    frame_scan.setVisibility(View.GONE);
                } else {
                    frame_scan.setVisibility(View.VISIBLE);
                    frame_scan.setForeground(new ScanDrawable(requireActivity(),15));
                    if (!hasPermissions(getActivity(), PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 100);
                    } else {
                        InitialiseDetectorsAndSources();
                    }
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!map.isEmpty() && !et_qty.getText().toString().isEmpty()) {
                    map.put("Qty", et_qty.getText().toString());
                    map.put("sRemarks",et_remarks.getText().toString());
                            setRecyclerView();
                }else {
                    if (map.isEmpty()) {
                        Toast.makeText(getActivity(), "No product", Toast.LENGTH_SHORT).show();
                    }
                    if (et_qty.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter Quantity", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listProduct.size() > 1) {
                    if (current_position < listProduct.size()) {
                        EditModeInner = true;
                        EditPosition = current_position;
                        et_barcode.setText(helper.GetBarcodeFromIProduct(listProduct.get(current_position).getiProduct()));
                        et_qty.setText(listProduct.get(current_position).getQty());
                        et_remarks.setText(listProduct.get(current_position).getsRemarks());
                        SetUnit(helper.GetProductUnit(helper.GetBarcodeFromIProduct(listProduct.get(current_position).getiProduct())),current_position);
                        current_position++;
                    }
                }
            }
        });
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listProduct.size() > 1) {
                    if (current_position > 0) {
                        current_position--;
                        EditModeInner = true;
                        EditPosition = current_position;
                        et_barcode.setText(helper.GetBarcodeFromIProduct(listProduct.get(current_position).getiProduct()));
                        et_qty.setText(listProduct.get(current_position).getQty());
                        SetUnit(helper.GetProductUnit(helper.GetBarcodeFromIProduct(listProduct.get(current_position).getiProduct())),current_position);
                        et_remarks.setText(listProduct.get(current_position).getsRemarks());

                    }

                }
            }
        });
    }

    private void setRecyclerView() {
        String  Qty;

        Qty = map.get("Qty");

if(!EditModeInner) {
    listProduct.add(new ListProduct(map.get("Name"), map.get("Code"), Qty,sp_unit.getSelectedItem().toString(), map.get("iProduct"), map.get("sRemarks")));
}else {
    if(EditPosition!= -1)
        listProduct.set(EditPosition, new ListProduct(map.get("Name"), map.get("Code"), Qty, sp_unit.getSelectedItem().toString(), map.get("iProduct"), map.get("sRemarks")));
}
        EditModeInner = false;
        EditPosition = -1 ;
        rl_showProductInfo.setVisibility(View.GONE);
        et_qty.setText("");
        et_barcode.setText("");
        et_remarks.setText("");
        SetUnit("",-1);
        map.clear();
        listProductAdapter.notifyDataSetChanged();
        StockCountProductSingleton.getInstance().setList(listProduct);
    }

    private void setDataForEditing(String voucherNo){
        Cursor cursor = helper.GetHeaderData(voucherNo);

        if(cursor!=null && cursor.moveToFirst()){
            warehouse_id = cursor.getString(cursor.getColumnIndex("iWarehouse"));
        }

    }

    private void SetRecyclerFromDB(String voucherNo) {
        Cursor cursor = helper.GetBodyData(voucherNo);
        String name,qty,code,iProduct,unit,sRemarks;
        if(cursor!=null){
            if(cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    iProduct = cursor.getString(cursor.getColumnIndex("iProduct"));
                    name = helper.GetProductName(iProduct);
                    code = helper.GetProductCode(iProduct);
                    qty = cursor.getString(cursor.getColumnIndex("fQty"));
                    unit = cursor.getString(cursor.getColumnIndex("sUnit"));
                    sRemarks = cursor.getString(cursor.getColumnIndex("sRemarks"));
                    listProduct.add(new ListProduct(name, code, qty, unit, iProduct, sRemarks));
                    cursor.moveToNext();

                    if (cursor.getCount() == i + 1) {
                        adapter.notifyDataSetChanged();
                        StockCountProductSingleton.getInstance().setList(listProduct);
                    }
                }
            }else {
                Objects.requireNonNull(getActivity()).finish();
                Log.d("error","Body have no data to load!");
            }
        }
    }

    private void SetDataToEdit(ListProduct product, int pos) {
        AddNewAlert(pos);
        if(alertDialog.isShowing()) {
            et_barcode.setText(helper.GetBarcodeFromIProduct(product.getiProduct()));
            et_qty.setText(product.getQty());
            et_remarks.setText(product.getsRemarks());
            EditPosition = pos;
        }
    }

    private void DeleteStockCountItemAlert(final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("Delete Product!")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listProduct.remove(pos);
                        StockCountProductSingleton.getInstance().setList(listProduct);
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
                            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                            SetUnit(helper.GetProductUnit(barcode.valueAt(0).displayValue),-1);

                        }
                    });
                }
            }
        });
    }

    private void ProductSearch(String keyword) {
        SearchproductList.clear();
        if (dialog.isShowing()) {
            Cursor cursor = helper.SearchProduct(keyword);
            if (cursor != null&&!keyword.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Code = cursor.getString(cursor.getColumnIndex("Code"));
                    String Barcode = cursor.getString(cursor.getColumnIndex("Barcode"));
                    SearchproductList.add(new SearchProduct(Name, Code, Barcode));
                    cursor.moveToNext();

                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(adapter);
                    }

                    adapter.setOnClickListener(new SearchProductAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(View view, SearchProduct search_item, int pos) {
                            et_barcode.setText(search_item.getBarcode());
                            SetUnit(helper.GetProductUnit(search_item.getBarcode()),-1);
                            dialog.dismiss();
                        }
                    });
                }

            } else {
                SearchproductList.clear();
                SearchproductList.add(new SearchProduct("No Products available!", "", ""));
                rv_search.setAdapter(adapter);

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void GetProductInfoToMap(String barcode,int position) {

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
            SetUnit(helper.GetProductUnit(barcode),position);
        }else {

            rl_showProductInfo.setVisibility(View.GONE);

        }
    }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.body_frgment, container, false);

        fab_controller = view.findViewById(R.id.fab_controller);
        et_barcode = view.findViewById(R.id.et_barcode);

        product_name = view.findViewById(R.id.product_name);
        product_code = view.findViewById(R.id.product_code);
        save = view.findViewById(R.id.save);
        qty = view.findViewById(R.id.qty);
        add_new = view.findViewById(R.id.add_new);
        rv_product = view.findViewById(R.id.rv_product);
        initFab(view);

        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy");
        rv_product.setLayoutManager(new LinearLayoutManager(getActivity()));
        map = new HashMap<>();


        listProduct = new ArrayList<>();
        helper = new DatabaseHelper(getActivity());

        SearchproductList = new ArrayList<>();
        adapter = new SearchProductAdapter(getActivity(), SearchproductList);

        if (getArguments() != null) {
            EditMode = getArguments().getString("EditMode");

            assert EditMode != null;
            if (EditMode.equals("edit")) {
                voucherNo = getArguments().getString("voucherNo");
                warehouse_id = getArguments().getString("warehouse");
                setDataForEditing(voucherNo);
                SetRecyclerFromDB(voucherNo);

            } else {
                warehouse_id = PublicData.warehouse;
            }
        }

        listProductAdapter = new ListProductAdapter(getActivity(), listProduct, EditMode);

        rv_product.setAdapter(listProductAdapter);

        listProductAdapter.setOnClickListener(new ListProductAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, final ListProduct product, final int pos) {
                if (!selection_active) {
                    current_position = pos;
                    EditModeInner = true;
                    SetDataToEdit(product, current_position);


                } else {
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemDeleteClickListener(View view, ListProduct product, int pos) {
                DeleteStockCountItemAlert(pos);
            }

            @Override
            public void onItemLongClick(View view, ListProduct product, int pos) {
                enableActionMode(pos);
                selection_active = true;
            }
        });


        fab_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewAlert(-1);
            }
        });


        fab_close_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
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

}
