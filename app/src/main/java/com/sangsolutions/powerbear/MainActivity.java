package com.sangsolutions.powerbear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.User;

import java.util.Objects;

@SuppressWarnings("StatementWithEmptyBody")
public class MainActivity extends AppCompatActivity {
Button login_btn;
EditText login_name,password;
DatabaseHelper helper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
ImageView settings;

/*    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }*/

@SuppressWarnings("StatementWithEmptyBody")
public void syncData(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        ScheduleJob scheduleJob = new ScheduleJob();
        if(!Objects.equals(preferences.getString("syncDate", ""),String.valueOf(DateFormat.format("yyyy-MM-dd", new java.util.Date())))) {
            if (Objects.equals(preferences.getString(Commons.WAREHOUSE_FINISHED, "false"), "true")
                    && Objects.equals(preferences.getString(Commons.PENDING_PO_FINISHED, "false"), "true")
                    && Objects.equals(preferences.getString(Commons.PENDING_SO_FINISHED, "false"), "true")
                    && Objects.equals(preferences.getString(Commons.REMARKS_FINISHED, "false"), "true")) {

            } else {

                scheduleJob.SyncUserData(this);

                editor.putString(Commons.WAREHOUSE_FINISHED, "false").apply();
                editor.putString(Commons.PENDING_PO_FINISHED, "false").apply();
                editor.putString(Commons.PENDING_SO_FINISHED, "false").apply();
                editor.putString(Commons.PRODUCT_FINISHED, "false").apply();
                editor.putString(Commons.REMARKS_FINISHED,"false").apply();
                editor.putString(Commons.SYNC_DATE, "").apply();
            }
        }else {
            scheduleJob.SyncUserData(this);
            editor.putString(Commons.WAREHOUSE_FINISHED, "false").apply();
            editor.putString(Commons.PENDING_PO_FINISHED, "false").apply();
            editor.putString(Commons.PENDING_SO_FINISHED, "false").apply();
            editor.putString(Commons.PRODUCT_FINISHED, "false").apply();
            editor.putString(Commons.REMARKS_FINISHED,"false").apply();
            editor.putString(Commons.SYNC_DATE, "").apply();
        }

    }else {
        Toast.makeText(this, "Cannot be synced do to lower Api level", Toast.LENGTH_SHORT).show();
    }
}

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new ScheduleJob().SyncUserData(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_btn = findViewById(R.id.login);
        login_name = findViewById(R.id.username);
        password = findViewById(R.id.password);
        settings = findViewById(R.id.settings);
    AndroidNetworking.initialize(this);
    helper = new DatabaseHelper(this);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("syncDate", "").apply();

        if (helper.GetLoginStatus()) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
            syncData();
        }



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SetIPActivity.class));

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            if (login_name.getText().toString().trim().equals("")) {
                login_name.setError("Enter Username");
            } else if (password.getText().toString().trim().equals("")) {
                password.setError("Enter Password");

            } else {
                User u = new User();
                u.setsLoginName(login_name.getText().toString().trim());
                u.setsPassword(password.getText().toString().trim());
                if(helper.GetUser()) {
                    boolean success = helper.LoginUser(u);
                    if (success) {

                        boolean status = helper.InsertCurrentLoginUser(u);
                        if (status) {
                            syncData();
                            startActivity(new Intent(MainActivity.this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login information don't mach the recoded ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Check the IP and try again!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    });
    }
}
