package com.sangsolutions.powerbear.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StockCountHeaderFragment extends Fragment {
EditText date, narration, stock_date;
    TextView VoucherNo;
    DatabaseHelper helper;
String warehouse_id = "",voucherNo="",Date ="",dStockCountDate="", Narration ="";
Date c;
Spinner sp_warehouse;
List<Warehouse> list;
WarehouseAdapter adapter;
String EditMode = "";

    @SuppressLint("SimpleDateFormat") SimpleDateFormat df;


public void LoadWarehouse(){
        Cursor cursor = helper.GetWarehouse();
        list.clear();
        if(cursor!=null){
            for (int i = 0; i < cursor.getCount(); i++) {
                if(!cursor.getString(cursor.getColumnIndex("Name")).equals(" "))
                list.add(new Warehouse(cursor.getString(cursor.getColumnIndex("MasterId")), cursor.getString(cursor.getColumnIndex("Name"))));

                cursor.moveToNext();
                if(cursor.getCount()==i+1){
                    sp_warehouse.setAdapter(adapter);
                }

            }
        }
    }





private void setData(String voucherNo){
    Cursor cursor = helper.GetHeaderData(voucherNo);

    if(cursor!=null && cursor.moveToFirst()){
                warehouse_id = cursor.getString(cursor.getColumnIndex("iWarehouse"));
                voucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                Date  = Tools.dateFormat2(cursor.getString(cursor.getColumnIndex("dDate")));
                dStockCountDate = Tools.dateFormat2(cursor.getString(cursor.getColumnIndex("dStockCountDate")));
        Narration = cursor.getString(cursor.getColumnIndex("sNarration"));
        date.setText(Date);
        stock_date.setText(dStockCountDate);
       PublicData.stock_date = Tools.dateFormat(dStockCountDate);
       PublicData.date = Tools.dateFormat(Date);
        PublicData.narration = Narration;
        PublicData.warehouse = warehouse_id;

       // warehouse.setText(helper.GetWarehouseById(warehouse_id));
        VoucherNo.setText("Voucher No :"+voucherNo);
        narration.setText(Narration);

    }

}


public void SetWarehouseSpinner(String warehouse_id){

if(list.size()!=0){

    for(int i = 0;i<list.size();i++){
        if(list.get(i).getMasterId().equals(warehouse_id)){
            sp_warehouse.setSelection(i);
        }
    }
}

}


@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_frgment, container, false);
    date =   view.findViewById(R.id.date);
    narration = view.findViewById(R.id.narration);
        VoucherNo = view.findViewById(R.id.voucher_no);
        sp_warehouse = view.findViewById(R.id.warehouse);
        stock_date = view.findViewById(R.id.stock_date);
        helper = new DatabaseHelper(getActivity());

        //setting default warehouse
        PublicData.warehouse = "1";


    c = Calendar.getInstance().getTime();
    df = new SimpleDateFormat("dd-MM-yyyy");

    list = new ArrayList<>();
    adapter = new WarehouseAdapter(list);
    LoadWarehouse();
    if(getArguments() != null) {

        EditMode = getArguments().getString("EditMode");


        assert EditMode != null;
        if (EditMode.equals("edit")) {
            warehouse_id = getArguments().getString("warehouse");
            voucherNo = getArguments().getString("voucherNo");
            if (!helper.GetWarehouseById(warehouse_id).equals("")) {
                SetWarehouseSpinner(warehouse_id);
            } else {
                Objects.requireNonNull(getActivity()).finish();
            }
            setData(voucherNo);

        } else if (EditMode.equals("new")) {
            VoucherNo.setText("Voucher No :"+helper.GetNewVoucherNo());
            date.setText(df.format(c));
            stock_date.setText(df.format(c));
            PublicData.date = Tools.dateFormat(df.format(c));
            PublicData.stock_date = Tools.dateFormat(df.format(c));
        }



    }else {
        Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
    Objects.requireNonNull(getActivity()).finish();
    }



    sp_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PublicData.warehouse = list.get(adapterView.getSelectedItemPosition()).getMasterId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

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
                if(EditMode.equals("edit")||EditMode.equals("new"))
                datePickerDialog.show();
            }
        });


    stock_date.setOnClickListener(v -> {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                String StringDate = Tools.checkDigit(dayOfMonth)+
                        "-" +
                        Tools.checkDigit(month + 1) +
                        "-"+
                        year;
                stock_date.setText(StringDate);
                PublicData.stock_date=Tools.dateFormat(StringDate);
            }
        };
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), onDateSetListener, year, month, day);
        datePickerDialog.setTitle("Select Date");
        if(EditMode.equals("edit")||EditMode.equals("new"))
            datePickerDialog.show();
    });

    narration.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            PublicData.narration = editable.toString();
        }
    });
        return view;

}




    private static class Warehouse {

        final String MasterId;
        final String Name;

        public Warehouse(String masterId, String name) {
            MasterId = masterId;
            Name = name;
        }

        public String getMasterId() {
            return MasterId;
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
            warehouse.setText(list.get(position).Name);
            return view;
        }
    }


}
