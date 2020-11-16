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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.Adapter.MinorDamagedPhotoAdapter.MinorDamagedPhotoAdapter;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksType;
import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksTypeAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
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

public class GoodsWithoutPOBodyFragment extends Fragment {

private FloatingActionButton add_fab, fab_delete, fab_close_all;
private RecyclerView rv_products;

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

    ImageView img_minor,img_damaged,img_close,img_forward,img_backward,img_save;

    boolean EditMode = false;
    String DocNo = "";

    LinearLayout minor_damaged_linear,damaged_linear;

    Animation move_down_anim, move_up_anim;

    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    boolean selection_active = false,selection_active_main = false;

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
                                 //current_position = 0;
                                //listDamagedImage.clear();
                               //listMinorImage.clear();
                            }
                        }else if(type.equals("delete_item")){
                           //DeleteItem(pos);
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

        AlertDialog.Builder builder= new AlertDialog.Builder(requireActivity() ,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(view);
        builder.setCancelable(false);
        mainAlertDialog = builder.create();
        mainAlertDialog.show();

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralAlert("Do you want to close?","Close!","close",0);
            }
        });
    }

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
        listMain = new ArrayList<>();
        goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(), listMain);


        Bundle bundle = getArguments();

        if(bundle!=null){
            EditMode = bundle.getBoolean("EditMode");
            DocNo = bundle.getString("DocNo");

            if(EditMode){
                GoodsBodyMainAlert(listMain,-1);
            }
        }

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsBodyMainAlert(listMain,-1);
            }
        });

        return view;
    }

}
