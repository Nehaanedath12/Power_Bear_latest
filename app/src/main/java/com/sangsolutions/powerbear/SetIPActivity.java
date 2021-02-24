package com.sangsolutions.powerbear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetIPActivity extends AppCompatActivity {
EditText et_ip_address;
Button btn_save;
Tools tools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_i_p);
        et_ip_address = findViewById(R.id.ip_address);
        btn_save = findViewById(R.id.save);
        tools = new Tools();
        if(!new Tools().getIP(this).isEmpty()){
            et_ip_address.setText(tools.getIP(this));
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tools.setIP(SetIPActivity.this,et_ip_address.getText().toString())){
                    Toast.makeText(SetIPActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                     startActivity(new Intent(SetIPActivity.this,MainActivity.class));
                }
                }

        });

    }
}