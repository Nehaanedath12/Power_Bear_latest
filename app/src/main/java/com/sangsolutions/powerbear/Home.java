package com.sangsolutions.powerbear;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Fragment.SyncFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings("ALL")
public class Home extends AppCompatActivity {
LinearLayout sync_ll, delivery_ll, stock_count_ll, goods_ll, report_ll, grn_without_po_ll;
DatabaseHelper helper;
ImageView img_logout,img_backup;
TextView tv_username,tv_date;
ImageView img_settings;
    SharedPreferences preferences;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onResume() {
        super.onResume();
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        tv_date.setText(df.format(c));
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

    public void BackUpAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Back Up!")
                .setMessage("Do you want to backup the database file?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Home.this, "file copied to :"+helper.copyDatabase(Home.this), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        sync_ll = findViewById(R.id.sync);
        delivery_ll = findViewById(R.id.delivery);
        stock_count_ll = findViewById(R.id.stock_count);
        goods_ll = findViewById(R.id.goods);
        report_ll = findViewById(R.id.report);
        img_logout = findViewById(R.id.logout);
        tv_username = findViewById(R.id.username);
        img_backup = findViewById(R.id.backup);
        tv_date = findViewById(R.id.date);
        grn_without_po_ll = findViewById(R.id.goods_without_po);
        img_settings = findViewById(R.id.settings);
        helper = new DatabaseHelper(this);

        tv_username.setText(helper.GetLoginUser());

        String LoginUserMenu = helper.GetLoginMenuIDs();

        if(!LoginUserMenu.equals("")){
            List<String> listMenu = Arrays.asList(LoginUserMenu.split(","));

        if(!listMenu.contains("1")){
            delivery_ll.setVisibility(View.GONE);
        }
        if(!listMenu.contains("2")){
            stock_count_ll.setVisibility(View.GONE);
        }
        if(!listMenu.contains("3")){
            goods_ll.setVisibility(View.GONE);
        }
        if(!listMenu.contains("4")){
            grn_without_po_ll.setVisibility(View.GONE);
        }
        }else {
            delivery_ll.setVisibility(View.GONE);
            stock_count_ll.setVisibility(View.GONE);
            goods_ll.setVisibility(View.GONE);
            grn_without_po_ll.setVisibility(View.GONE);
        }




    stock_count_ll.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.PENDING_PO_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.PENDING_SO_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.REMARKS_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true")
                    ){
                if(!PublicData.Syncing) {
                    startActivity(new Intent(Home.this, StockCountHistory.class));
                }else {
                    Toast.makeText(Home.this, "Wait for the data finish syncing!", Toast.LENGTH_SHORT).show();
                }
                }
            else {
                Toast.makeText(Home.this, "Sync data before you do anything..", Toast.LENGTH_SHORT).show();
            }
        }
    });

        sync_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Tools.isConnected(Home.this)) {
                    Fragment fragment = new SyncFragment();
                    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                    tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    tx.replace(R.id.fragment, fragment).addToBackStack("sync").commit();
                    //start with SyncGoodsReceipt
                    if(!PublicData.Syncing) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            new ScheduleJob().SyncGoodsReceipt(Home.this);
                        }
                        Toast.makeText(Home.this, "Sync started...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar snackBar = Snackbar .make(v, "You are offline!", Snackbar.LENGTH_SHORT);
                    snackBar.setTextColor(Color.WHITE);
                    snackBar.setBackgroundTint(Color.RED);
                    snackBar.show();
                }




            }
        });

    img_logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(Home.this,view);
            popupMenu.inflate(R.menu.logout_menu);
            popupMenu.setGravity(Gravity.BOTTOM);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.logout){
                    if(helper.DeleteCurrentUser()){

                        startActivity(new Intent(Home.this,MainActivity.class));
                        finish();
                    }
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    });

    goods_ll.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.PENDING_PO_FINISHED, "false").equals("true")
                    && preferences.getString(Commons.PENDING_SO_FINISHED, "false").equals("true")
                    &&  preferences.getString(Commons.REMARKS_FINISHED, "false").equals("true")
                    &&  preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true")
            ){
                if(!PublicData.Syncing) {
                    Intent intent = new Intent(Home.this, GoodsReceiptHistory.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(Home.this, "Wait for the data finish syncing!", Toast.LENGTH_SHORT).show();
                }
                }else {
                Toast.makeText(Home.this, "Sync data before you do anything..", Toast.LENGTH_SHORT).show();
            }
        }
    });

        delivery_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.PENDING_PO_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.PENDING_SO_FINISHED, "false").equals("true")
                        &&  preferences.getString(Commons.REMARKS_FINISHED, "false").equals("true")
                        &&  preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true")
                ){
                    if(!PublicData.Syncing) {
                        Intent intent = new Intent(Home.this, DeliveryNoteHistory.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(Home.this, "Wait for the data finish syncing!", Toast.LENGTH_SHORT).show();
                    }
            }else {
                Toast.makeText(Home.this, "Sync data before you do anything..", Toast.LENGTH_SHORT).show();
            }
            }
        });

        report_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        startActivity(new Intent(Home.this,ReportMain.class));
            }
        });

        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,SetIPActivity.class));
            }
        });

        grn_without_po_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.PENDING_PO_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.PENDING_SO_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.REMARKS_FINISHED, "false").equals("true")
                        && preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true")
                ){
                    if(!PublicData.Syncing) {
                        Intent intent = new Intent(Home.this, GoodsReceiptWithoutPOHistory.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(Home.this, "Wait for the data finish syncing!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Home.this, "Sync data before you do anything..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    img_backup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!hasPermissions(Home.this, PERMISSIONS)){
                ActivityCompat.requestPermissions(Home.this, PERMISSIONS, 100);
            }else {
                BackUpAlert();
            }

        }
    });

    }


}
