package com.sangsolutions.powerbear.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter.SupplierSearch;
import com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter.SupplierSearchAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;

import com.sangsolutions.powerbear.Tools;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GoodsWithoutPOHeaderFragment extends Fragment {

    EditText et_date,et_narration,et_supplier,et_ref_no;
    List<SupplierSearch> supplierList;
    SupplierSearchAdapter supplierAdapter;
    DatabaseHelper helper;
    TextView tv_add_po,tv_doc_no;
    String DocNo  = "";
    Collection<String> c;
    boolean EditMode = false;
    AlertDialog supplierSearchAlert;
    EditText et_search_input;
    RecyclerView rv_search;

    private boolean selection_active = false ;

    public void LoadSupplier(String keyword){
        supplierList.clear();
        try {
            Cursor cursor = helper.GetSupplier(keyword);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    supplierList.add(new SupplierSearch(
                            cursor.getString(cursor.getColumnIndex("Cusomer")),
                            cursor.getString(cursor.getColumnIndex("HeaderId"))
                    ));
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(supplierAdapter);
                    }
                }
            }else {
                supplierList.add(new SupplierSearch(
                        "--Empty result--",
                        "0"
                ));
                rv_search.setAdapter(supplierAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void searchSupplierAlert(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_layout, null);
        et_search_input = view.findViewById(R.id.search_edit);
        rv_search = view.findViewById(R.id.search_recycler);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setView(view);
        supplierSearchAlert = builder.create();
        supplierSearchAlert.show();

        if (supplierSearchAlert.isShowing()) {
            LoadSupplier("");
            et_search_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    et_search_input.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    LoadSupplier(s.toString());
                }
            });

            supplierAdapter.setOnClickListener(new SupplierSearchAdapter.OnClickListener() {
                @Override
                public void onItemClick(SupplierSearch search_item) {
                    if(!search_item.getsSupplierId().equals("0"))
                        if(supplierSearchAlert.isShowing()) {
                            PublicData.supplier = search_item.getsSupplierId();
                            et_supplier.setText(search_item.getsSupplierName());
                            supplierSearchAlert.dismiss();
                        }
                }
            });
        }
    }

    public void LoadValueForEditing(){
        try {
            Cursor cursor = helper.GetGoodsHeaderData(DocNo);
            if(cursor!=null&&cursor.moveToFirst()){
                //LoadDate
                et_date.setText(Tools.dateFormat2(cursor.getString(cursor.getColumnIndex("DocDate"))));

                //LoadSupplier
                String supplier =  cursor.getString(cursor.getColumnIndex("iSupplier"));
                PublicData.supplier = supplier;
                if(supplierList.size()>0)
                {
                    for(int i = 0;i<supplierList.size();i++){
                        if(supplier.equals(supplierList.get(i).getsSupplierId())){
                            et_supplier.setText(supplierList.get(i).getsSupplierName());

                        }
                    }
                }




                String narration = cursor.getString(cursor.getColumnIndex("sNarration"));
                et_narration.setText(narration);

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = LayoutInflater.from(requireActivity()).inflate(R.layout.goods_without_po_header_fragment,container,false);
        helper = new DatabaseHelper(requireActivity());
        et_date = view.findViewById(R.id.date);
        et_narration = view.findViewById(R.id.narration);
        tv_add_po = view.findViewById(R.id.add_po);
        tv_doc_no = view.findViewById(R.id.doc_no);
        et_supplier = view.findViewById(R.id.supplier);
        et_ref_no = view.findViewById(R.id.refno);
        Bundle bundle = getArguments();

        supplierList = new ArrayList<>();
        supplierAdapter =new SupplierSearchAdapter(requireActivity(),supplierList);

        try {
            tv_doc_no.setText("Doc No: " + PublicData.voucher);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(bundle!=null)
        {
            DocNo = bundle.getString("DocNo");
            EditMode = bundle.getBoolean("EditMode");
            if(EditMode){
                final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        try {
                            LoadValueForEditing();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(r, 500);
            }
        }

        et_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSupplierAlert();
            }
        });

        LoadSupplier("");

        if(!EditMode) {
            et_date.setText(String.valueOf(DateFormat.format("dd-MM-yyyy", new Date())));
            PublicData.date = Tools.dateFormat(String.valueOf(DateFormat.format("dd-MM-yyyy", new Date())));
        }
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


        et_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PublicData.date = Tools.dateFormat(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_narration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PublicData.narration = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }
}
