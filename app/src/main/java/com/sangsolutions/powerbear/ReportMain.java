package com.sangsolutions.powerbear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ReportMain extends AppCompatActivity implements View.OnClickListener {
CardView card_pendingPO,card_pendingSO,card_deliveryNote,card_goodsReceipt,card_stockCount;
    ImageView img_home;
    TextView title;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main);

    title = findViewById(R.id.title);
    img_home = findViewById(R.id.home);

    title.setText("Reports");

        card_pendingPO = findViewById(R.id.pending_po);
        card_pendingSO = findViewById(R.id.pending_so);
        card_deliveryNote = findViewById(R.id.delivery_note);
        card_goodsReceipt = findViewById(R.id.close);
        card_stockCount = findViewById(R.id.stock_count);

        card_pendingPO.setOnClickListener(this);
        card_pendingSO.setOnClickListener(this);
        card_deliveryNote.setOnClickListener(this);
        card_goodsReceipt.setOnClickListener(this);
        card_stockCount.setOnClickListener(this);
        img_home.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pending_po:
                Intent intent = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent.putExtra("report_type","pending_po");
                startActivity(intent);
                break;
            case R.id.pending_so:
                Intent intent2 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent2.putExtra("report_type","pending_so");
                startActivity(intent2);
                break;
            case R.id.delivery_note:
                Intent intent3 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent3.putExtra("report_type","delivery_note");
                startActivity(intent3);
                break;
            case R.id.close:
                Intent intent4 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent4.putExtra("report_type","goods_receipt");
                startActivity(intent4);
                break;
            case R.id.stock_count:
                Intent intent5 = new Intent(ReportMain.this, SelectCustomerOrProduct.class);
                intent5.putExtra("report_type","stock_count");
                startActivity(intent5);
                break;

            case R.id.home:
                startActivity(new Intent(ReportMain.this,Home.class));
                finishAffinity();
                break;
        }
    }
}