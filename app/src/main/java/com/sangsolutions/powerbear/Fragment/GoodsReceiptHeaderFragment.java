package com.sangsolutions.powerbear.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.POListAdaptet.POList;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POListAdapter;
import com.sangsolutions.powerbear.Adapter.POSelectAdapter.POSelect;
import com.sangsolutions.powerbear.Adapter.POSelectAdapter.POSelectAdapter;
import com.sangsolutions.powerbear.Adapter.SupplierAdapter.SupplierAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;
import com.sangsolutions.powerbear.Tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GoodsReceiptHeaderFragment extends Fragment {
    EditText et_date;
    Spinner sp_supplier;
    List<SupplierAdapter.Supplier> supplierList;
    List<POList> poList;
    List<POSelect> poSelectList;
    POSelectAdapter poSelectAdapter;
    POListAdapter poListAdapter;
    SupplierAdapter supplierAdapter;
    DatabaseHelper helper;
    RecyclerView rv_pos;
    TextView tv_add_po,tv_doc_no;
    private boolean selection_active = false ;

    public void closeSelection(){
        poSelectAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleSelection(int position) {
        poSelectAdapter.toggleSelection(position);
        int count = poSelectAdapter.getSelectedItemCount();
        if(count==0){
            closeSelection();
        }
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    public void POSelectionDialog(){
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.select_po_layout,null,false);
        ImageView img_close = view.findViewById(R.id.close);
        Button btn_apply = view.findViewById(R.id.apply);
        RecyclerView rv_po_select = view.findViewById(R.id.rv_po_select);
        rv_po_select.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        if(poSelectList.size()>0) {
            rv_po_select.setAdapter(poSelectAdapter);
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                closeSelection();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(poSelectAdapter.getSelectedItemCount()>0){
                setPORecycler(poSelectAdapter.getSelectedItems());
                alertDialog.dismiss();
                }else {
                    Toast.makeText(getActivity(), "Select a document before applying it!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        poSelectAdapter.setOnClickListener(new POSelectAdapter.OnClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (selection_active) {
                    enableActionMode(pos);
                } else {
                    selection_active = true;
                    enableActionMode(pos);
                }
            }
        });
    }


public void setPORecycler(List<Integer> list){
    poList.clear();
    for(int i=0;i<list.size();i++){
        for (int j=0;j<poSelectList.size();j++){
            if(list.get(i)==j){
                poList.add(new POList(
                       poSelectList.get(j).getDocNo(),
                        poSelectList.get(j).getHeaderId()
                ));
            }
        }
        if(i+1==list.size()){
            GoodsReceiptPoSingleton.getInstance().setList(poList);
            poListAdapter.notifyDataSetChanged();
            closeSelection();
        }
    }

}

public void LoadPOs(String customer){
    poList.clear();
    poSelectList.clear();
    Cursor cursor = helper.GetPOs(customer);
    if(cursor!=null&&cursor.moveToFirst()){
        for(int i=0;i<cursor.getCount();i++){
            poSelectList.add(new POSelect(
                    cursor.getString(cursor.getColumnIndex("DocNo")),
                    cursor.getString(cursor.getColumnIndex("HeaderId"))
            ));
            cursor.moveToNext();
            if(i+1==cursor.getCount()){
                poListAdapter.notifyDataSetChanged();
            }
        }
    }
}


public void LoadSupplier(){
    supplierList.clear();
    supplierList.add(new SupplierAdapter.Supplier("---Select Supplier---","0"));
    try {
        Cursor cursor = helper.GetSupplier();
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                supplierList.add(new SupplierAdapter.Supplier(
                        cursor.getString(cursor.getColumnIndex("Cusomer")),
                        cursor.getString(cursor.getColumnIndex("HeaderId"))
                ));
                cursor.moveToNext();
                if (i + 1 == cursor.getCount()) {
                    sp_supplier.setAdapter(supplierAdapter);
                }
            }
        }
    }catch (Exception e) {
    e.printStackTrace();
    }
sp_supplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LoadPOs(supplierList.get(position).getsSupplierName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
});
}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.good_reseipt_header_fragment,container,false);
        helper = new DatabaseHelper(requireActivity());
        et_date = view.findViewById(R.id.ll_1);
        tv_add_po = view.findViewById(R.id.add_po);
        sp_supplier = view.findViewById(R.id.supplier);
        tv_doc_no = view.findViewById(R.id.doc_no);
        try {
            tv_doc_no.setText("Doc No: " + helper.GetNewVoucherNoGoodsReceipt());
        }catch (Exception e) {
        e.printStackTrace();
        }
        supplierList = new ArrayList<>();
        poList = new ArrayList<>();
        poSelectList = new ArrayList<>();
        supplierAdapter =new SupplierAdapter(supplierList,requireActivity());
        poListAdapter = new POListAdapter(poList,requireActivity());
        poSelectAdapter = new POSelectAdapter(poSelectList,requireActivity());

        rv_pos = view.findViewById(R.id.rv_po);
        rv_pos.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        rv_pos.setAdapter(poListAdapter);
        LoadSupplier();
        et_date.setText(String.valueOf(DateFormat.format("dd-MM-yyyy", new Date())));
        et_date.setOnClickListener(new View.OnClickListener() {
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
                        et_date.setText(StringDate);
                        PublicData.date=Tools.dateFormat(StringDate);
                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                    datePickerDialog.show();
            }
        });


        poListAdapter.setOnClickListener(new POListAdapter.OnClickListener() {
            @Override
            public void onRemoveItemClick(POList po, int pos) {
                if(poList!=null && poList.size()>0){
                    poList.remove(pos);
                        poListAdapter.notifyDataSetChanged();
                }
                GoodsReceiptPoSingleton.getInstance().setList(poList);
            }
        });

        tv_add_po.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POSelectionDialog();
            }
        });
        return view;
    }
}
