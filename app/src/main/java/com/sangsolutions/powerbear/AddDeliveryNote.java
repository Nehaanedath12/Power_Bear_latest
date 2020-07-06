package com.sangsolutions.powerbear;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.ListProduct.ListProduct;
import com.sangsolutions.powerbear.Adapter.ListProduct.ListProductAdapter;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProduct;
import com.sangsolutions.powerbear.Adapter.SearchProduct.SearchProductAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.DeliveryNote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddDeliveryNote extends AppCompatActivity {
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
    private ImageView add_new, save;
    private String DocNo = "";
    private boolean EditMode = false;
    private String iVoucherNo;
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

    private void setRecyclerView(int position) {
        String Name, Code, Qty;

        Qty = map.get("Qty");

        if (Integer.parseInt(list.get(position).getQty()) >= Integer.parseInt(Qty)) {
            list.set(position, new ListProduct(
                    list.get(position).getiVoucherNo(),
                    list.get(position).getName(),
                    list.get(position).getCode(),
                    list.get(position).getQty(),
                    Qty,
                    list.get(position).getHeaderId(),
                    list.get(position).getProduct(),
                    list.get(position).getSiNo(),
                    list.get(position).getUnit()
            ));
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Picked Qty should not exceed Qty ", Toast.LENGTH_SHORT).show();
        }


        rl_showProductInfo.setVisibility(View.GONE);
        qty.setText("");
        et_barcode.setText("");
        map.clear();
        listProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cameraSource != null)
            cameraSource.release();

    }


    public void SaveAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save ?")
                .setMessage("Do you want to save the current data ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDataToDB();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create()
                .show();

    }


    public void SaveDataToDB() {
        DeliveryNote d = new DeliveryNote();
        if(helper.DeleteDeliveryNote(DocNo)) {
            for (int i = 0; i < list.size(); i++) {
                d.setiVoucherNo(iVoucherNo);
                d.setSiNo(list.get(i).getSiNo());
                d.setHeaderId(list.get(i).getHeaderId());
                d.setProduct(list.get(i).getProduct());
                d.setQty(list.get(i).getPickedQty());
                d.setiStatus("0");
                helper.InsertDelivery(d);


                if (list.size() == i + 1) {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }

    }


    public void setRecyclerViewFromDB(String DocNo,boolean EditMode) {
        String Name, Code, Qty, PickedQty, HeaderId, Product, SiNo ,Unit;
        Log.d("docno", DocNo);
        Cursor cursor;
        if(!EditMode) {
             cursor = helper.GetAllPendingDN(DocNo);
        }else {
             cursor = helper.GetAllDeliveryNote(DocNo);
        }
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Qty = cursor.getString(cursor.getColumnIndex("Qty"));
                if(EditMode) {
                    PickedQty = cursor.getString(cursor.getColumnIndex("PickedQty"));
                    iVoucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                }
                else
                PickedQty = "0";
                Name = cursor.getString(cursor.getColumnIndex("Name"));
                Code = cursor.getString(cursor.getColumnIndex("Code"));
                Unit = cursor.getString(cursor.getColumnIndex("Unit"));
                HeaderId = cursor.getString(cursor.getColumnIndex("HeaderId"));
                Product = cursor.getString(cursor.getColumnIndex("Product"));
                SiNo = cursor.getString(cursor.getColumnIndex("SiNo"));

                list.add(new ListProduct(iVoucherNo,Name, Code, Qty, PickedQty, HeaderId, Product, SiNo,Unit));

                listProductAdapter.notifyDataSetChanged();
                cursor.moveToNext();

            }
        }


    }

    private void InitialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(Objects.requireNonNull(this), barcodeDetector)
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
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    AddDeliveryNote.this.runOnUiThread(new Runnable() {
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
            Cursor cursor ;
            if(!EditMode){
                cursor =  helper.SearchProductPendingSO(keyword,DocNo);;
            }else {
                cursor =  helper.SearchProductDeliveryNote(keyword,DocNo);
            }

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
            product_name.setText("Name : "+map.get("Name"));
            product_code.setText(map.get("Code"));
        }else {

                rl_showProductInfo.setVisibility(View.GONE);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_note);
        fab_controller = findViewById(R.id.fab_controller);
        barcodeLinear = findViewById(R.id.linear_scan);
        linear_search = findViewById(R.id.linear_search);
        et_barcode = findViewById(R.id.et_barcode);
        surfaceView = findViewById(R.id.surfaceView);
        product_name= findViewById(R.id.product_name);
        product_code = findViewById(R.id.product_code);
        qty = findViewById(R.id.qty);
        add_new = findViewById(R.id.add_new);
        save = findViewById(R.id.save);

        helper = new DatabaseHelper(this);

        iVoucherNo = Objects.requireNonNull(helper).GetDeliveryNoteVoucherNo();

        rv_product = findViewById(R.id.rv_product);
        rv_product.setLayoutManager(new LinearLayoutManager(this));
        map = new HashMap<>();

        helper = new DatabaseHelper(this);
        rl_showProductInfo = findViewById(R.id.details);
        productList = new ArrayList<>();
        adapter = new SearchProductAdapter(AddDeliveryNote.this, productList);
        list = new ArrayList<>();
        listProductAdapter = new ListProductAdapter(this,list);
        rv_product.setAdapter(listProductAdapter);

        Intent intent = getIntent();
        if(intent!=null) {
            DocNo = intent.getStringExtra("DocNo");
            EditMode = intent.getBooleanExtra("EditMode", false);
        }
        if (DocNo != null && !DocNo.equals("")) {
            setRecyclerViewFromDB(DocNo,EditMode);
        }



        listProductAdapter.setOnClickListener(new ListProductAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, final ListProduct listProduct, int pos) {
                PopupMenu popupMenu = new PopupMenu(AddDeliveryNote.this, view);
                popupMenu.inflate(R.menu.edit_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                      if (item.getItemId() == R.id.edit) {
                          qty.setText(listProduct.getPickedQty());
                          et_barcode.setText(helper.GetProductBarcode(listProduct.getCode()));
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
                Animation move_down_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_down);
                Animation move_up_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up);
                Animation clock_wise_rotate_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clock_wise_rotate);
                Animation anti_clock_wise_rotate_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anti_clock_wise_rotate);
                if (linear_search.getVisibility() == View.VISIBLE && barcodeLinear.getVisibility() == View.VISIBLE) {
                    linear_search.startAnimation(move_down_anim);
                    barcodeLinear.startAnimation(move_down_anim);
                    linear_search.setVisibility(View.GONE);
                    barcodeLinear.setVisibility(View.GONE);
                    fab_controller.startAnimation(anti_clock_wise_rotate_anim);
                    fab_controller.setImageDrawable(Objects.requireNonNull(getApplicationContext()).getDrawable(R.drawable.ic_add));
                } else {
                    linear_search.setVisibility(View.VISIBLE);
                    barcodeLinear.setVisibility(View.VISIBLE);
                    linear_search.startAnimation(move_up_anim);
                    barcodeLinear.startAnimation(move_up_anim);
                    fab_controller.startAnimation(clock_wise_rotate_anim);
                    fab_controller.setImageDrawable(Objects.requireNonNull(getApplicationContext()).getDrawable(R.drawable.ic_close_30dp));
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
                    if (!hasPermissions(AddDeliveryNote.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(AddDeliveryNote.this, PERMISSIONS, 100);
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
                    for (int i= 0 ;i<list.size();i++) {
                        if (list.get(i).getCode().equals(map.get("Code"))) {
                            setRecyclerView(i);
                        }
                    }
                }else {
                    if (map.isEmpty()) {
                        Toast.makeText(AddDeliveryNote.this, "No product", Toast.LENGTH_SHORT).show();
                    }
                    if (qty.getText().toString().isEmpty()) {
                        Toast.makeText(AddDeliveryNote.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()>0) {
                   SaveAlert();
                }
                }
        });

        linear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(AddDeliveryNote.this).inflate(R.layout.search_layout, null);
                et_search_input = view.findViewById(R.id.search_edit);
                rv_search = view.findViewById(R.id.search_recycler);
                rv_search.setLayoutManager(new LinearLayoutManager(AddDeliveryNote.this));
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDeliveryNote.this);
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
                                    InputMethodManager inputMethodManager = (InputMethodManager) AddDeliveryNote.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
    }
}
