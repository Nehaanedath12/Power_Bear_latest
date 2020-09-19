package com.sangsolutions.powerbear.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;
import com.sangsolutions.powerbear.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class HeaderFragment extends Fragment {
EditText date,remarks;
TextView warehouse,VoucherNo;
DatabaseHelper helper;
String warehouse_id = "",voucherNo="",Date ="" ,Remarks="";
Button close;
Date c;

boolean EditMode = false;

    @SuppressLint("SimpleDateFormat") SimpleDateFormat df;


private void CloseAlert() {
    if (EditMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit?")
                .setMessage("Do you want't to exit without saving?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StockCountSingleton.getInstance().clearList();
                        Objects.requireNonNull(getActivity()).finish();
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
    else {
        StockCountSingleton.getInstance().clearList();
        Objects.requireNonNull(getActivity()).finish();
    }
}


private void setDataForEditing(String voucherNo){
    Cursor cursor = helper.GetHeaderData(voucherNo);

    if(cursor!=null && cursor.moveToFirst()){
                warehouse_id = cursor.getString(cursor.getColumnIndex("iWarehouse"));
                voucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                Date  = Tools.dateFormat2(cursor.getString(cursor.getColumnIndex("dDate")));
        Remarks  = cursor.getString(cursor.getColumnIndex("sRemarks"));
        date.setText(Date);
       PublicData.date = Date;
        PublicData.remakes = Remarks;
        warehouse.setText(helper.GetWarehouseById(warehouse_id));
        VoucherNo.setText("Voucher No :"+voucherNo);
        remarks.setText(Remarks);

    }

}

@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_frgment, container, false);
    date =   view.findViewById(R.id.title2);
    remarks = view.findViewById(R.id.remarks);
        warehouse = view.findViewById(R.id.warehouse_name);
        close = view.findViewById(R.id.goods_receipt);
        VoucherNo = view.findViewById(R.id.voucher_no);
        helper = new DatabaseHelper(getActivity());
    c = Calendar.getInstance().getTime();
    df = new SimpleDateFormat("dd-MM-yyyy");


    if(getArguments() != null) {
        warehouse_id = getArguments().getString("warehouse");
        EditMode = getArguments().getBoolean("EditMode");
        voucherNo = getArguments().getString("voucherNo");

        if(!EditMode){
            remarks.setFocusable(false);
            date.setClickable(false);
        if(!helper.GetWarehouseById(warehouse_id).equals("")){
            warehouse.setText(helper.GetWarehouseById(warehouse_id));
        }else{
            Objects.requireNonNull(getActivity()).finish();
        }
            VoucherNo.setText("Voucher No :"+helper.GetNewVoucherNo());
            date.setText(df.format(c));
           PublicData.date = Tools.dateFormat(df.format(c));

        }else {
            date.setClickable(false);
            setDataForEditing(voucherNo);
        }
    }else {
        Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
    Objects.requireNonNull(getActivity()).finish();
    }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        String StringDate = Tools.checkDigit(dayOfMonth)+
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-"+
                                year;
                        date.setText(StringDate);
                       PublicData.date=Tools.dateFormat(StringDate);
                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                if(EditMode)
                datePickerDialog.show();
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAlert();
            }
        });

    remarks.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            PublicData.remakes = editable.toString();
        }
    });
        return view;
    }
}
