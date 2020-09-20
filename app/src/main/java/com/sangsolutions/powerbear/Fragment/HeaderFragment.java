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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.StockCount;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;
import com.sangsolutions.powerbear.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HeaderFragment extends Fragment {
EditText date, narration;
TextView warehouse,VoucherNo;
DatabaseHelper helper;
String warehouse_id = "",voucherNo="",Date ="" , Narration ="";
Button close,save;
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
                list.add(new Warehouse(cursor.getString(cursor.getColumnIndex("MasterId")),cursor.getString(cursor.getColumnIndex("Name"))));

                cursor.moveToNext();
                if(cursor.getCount()==i+1){
                    sp_warehouse.setAdapter(adapter);
                }

            }
        }
    }


private void CloseAlert() {
    if (EditMode.equals("edit")||EditMode.equals("new")) {
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
    else if(EditMode.equals("view")){
        StockCountSingleton.getInstance().clearList();
        Objects.requireNonNull(getActivity()).finish();
    }
}


private void setData(String voucherNo){
    Cursor cursor = helper.GetHeaderData(voucherNo);

    if(cursor!=null && cursor.moveToFirst()){
                warehouse_id = cursor.getString(cursor.getColumnIndex("iWarehouse"));
                voucherNo = cursor.getString(cursor.getColumnIndex("iVoucherNo"));
                Date  = Tools.dateFormat2(cursor.getString(cursor.getColumnIndex("dDate")));
        Narration = cursor.getString(cursor.getColumnIndex("sNarration"));
        date.setText(Date);

       PublicData.date = Date;
        PublicData.narration = Narration;
        PublicData.warehouse = warehouse_id;

       // warehouse.setText(helper.GetWarehouseById(warehouse_id));
        VoucherNo.setText("Voucher No :"+voucherNo);
        warehouse.setText(helper.GetWarehouseById(warehouse_id));
        narration.setText(Narration);

    }

}


public void SetWarehouseSpinner(String warehouse_id){

if(list.size()!=0){

    for(int i = 0;i<list.size();i++){
        if(list.get(i).getMasterId().equals(warehouse_id)){
            warehouse.setText(list.get(i).getName());
            sp_warehouse.setSelection(i);
        }
    }
}

}

    private void SaveAlert(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
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


    private void Save(){
        String s_date="",s_narration="" ,s_warehouse ="";

        s_date= PublicData.date;

        s_narration=PublicData.narration;

        s_warehouse = PublicData.warehouse;

        List<ListProduct> list = StockCountSingleton.getInstance().getList();

        if(!s_date.isEmpty()&&!list.isEmpty())
        {
            String str_date,s_voucher_no,str_narration;
            if(EditMode.equals("new")){
                str_date = s_date;
                s_voucher_no = helper.GetNewVoucherNo();
                str_narration = s_narration;
            }else {
                str_date = s_date;
                s_voucher_no = voucherNo;
                str_narration = s_narration;
            }
            StockCount s = new StockCount();
            if(EditMode.equals("edit")) {
                helper.DeleteStockCount(voucherNo);
            }
            for(int i = 0 ; i < list.size(); i ++){
                s.setiVoucherNo(s_voucher_no);
                s.setdDate(str_date);
                if(!s_warehouse.isEmpty()) {
                    s.setiWarehouse(s_warehouse);
                }else {
                    s.setiWarehouse(warehouse_id);
                }
                s.setiProduct(list.get(i).getiProduct());
                s.setfQty(list.get(i).getQty());
                s.setsUnit(list.get(i).getUnit());
                s.setsNarration(str_narration);
                s.setsRemarks(list.get(i).getsRemarks());
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

@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_frgment, container, false);
    date =   view.findViewById(R.id.date);
    narration = view.findViewById(R.id.narration);
        warehouse = view.findViewById(R.id.warehouse_name);
        close = view.findViewById(R.id.close);
        VoucherNo = view.findViewById(R.id.voucher_no);
        sp_warehouse = view.findViewById(R.id.warehouse);
        save = view.findViewById(R.id.save);
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
        if(EditMode.equals("view")){
            warehouse_id = getArguments().getString("warehouse");
            voucherNo = getArguments().getString("voucherNo");
            narration.setFocusable(false);
            date.setClickable(false);
            sp_warehouse.setEnabled(false);
            sp_warehouse.setClickable(false);
            save.setVisibility(View.GONE);
            if(!helper.GetWarehouseById(warehouse_id).equals("")){
                warehouse.setText(helper.GetWarehouseById(warehouse_id));
                SetWarehouseSpinner(warehouse_id);
            }else{
                Objects.requireNonNull(getActivity()).finish();
            }
            setData(voucherNo);

        }else if(EditMode.equals("edit")){
            warehouse_id = getArguments().getString("warehouse");
            voucherNo = getArguments().getString("voucherNo");
            if(!helper.GetWarehouseById(warehouse_id).equals("")){
                warehouse.setText(helper.GetWarehouseById(warehouse_id));
                SetWarehouseSpinner(warehouse_id);
            }else{
                Objects.requireNonNull(getActivity()).finish();
            }
            setData(voucherNo);

        }else if(EditMode.equals("new")){
            VoucherNo.setText("Voucher No :"+helper.GetNewVoucherNo());
            date.setText(df.format(c));
            PublicData.date = Tools.dateFormat(df.format(c));
        }



    }else {
        Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
    Objects.requireNonNull(getActivity()).finish();
    }



    sp_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                warehouse.setText(list.get(adapterView.getSelectedItemPosition()).getName());
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


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAlert();
            }
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

    save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SaveAlert();
        }
    });
        return view;

}




    private class Warehouse {

        String MasterId,Name;

        public Warehouse(String masterId, String name) {
            MasterId = masterId;
            Name = name;
        }

        public String getMasterId() {
            return MasterId;
        }

        public void setMasterId(String masterId) {
            MasterId = masterId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }


    private class WarehouseAdapter extends BaseAdapter {
        List<Warehouse> list;

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
