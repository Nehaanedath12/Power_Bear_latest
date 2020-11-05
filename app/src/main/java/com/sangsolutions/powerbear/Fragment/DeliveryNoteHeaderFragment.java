package com.sangsolutions.powerbear.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POListAdapter;
import com.sangsolutions.powerbear.Adapter.POSelectAdapter.POSelectAdapter;
import com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter.SupplierSearch;
import com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter.SupplierSearchAdapter;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteBodySingleton;
import com.sangsolutions.powerbear.Singleton.DeliveryNoteSOSingleton;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptBodySingleton;
import com.sangsolutions.powerbear.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DeliveryNoteHeaderFragment extends Fragment {

    EditText et_date,et_narration,et_supplier;
    List<SupplierSearch> supplierList;
    List<String> soList;
    List<String> deliverySoList;
    List<String> soSelectList;
    POSelectAdapter soSelectAdapter;
    POListAdapter soListAdapter;
    SupplierSearchAdapter customerAdapter;
    DatabaseHelper helper;
    RecyclerView rv_pos;
    TextView tv_add_so,tv_doc_no;
    String DocNo  = "";
    Collection<String> c;
    boolean EditMode = false;
    AlertDialog supplierSearchAlert;
    EditText et_search_input;
    RecyclerView rv_search;


    private boolean selection_active = false ;

    public void closeSelection(){
        soSelectAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleSelection(int position) {
        soSelectAdapter.toggleSelection(position);
        int count = soSelectAdapter.getSelectedItemCount();
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
        RecyclerView rv_so_select = view.findViewById(R.id.rv_po_select);
        rv_so_select.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        if(soSelectList.size()>0) {
            rv_so_select.setAdapter(soSelectAdapter);
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
                if(soSelectAdapter.getSelectedItemCount()>0){
                    setPORecycler(soSelectAdapter.getSelectedItems());
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(getActivity(), "Select a document before applying it!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        soSelectAdapter.setOnClickListener(new POSelectAdapter.OnClickListener() {
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
        soList.clear();
        deliverySoList.clear();
        for(int i=0;i<list.size();i++){
            for (int j = 0; j< soSelectList.size(); j++){
                if(list.get(i)==j){
                    soList.add(soSelectList.get(j));
                }
            }
            if(i+1==list.size()){
                if(EditMode) {
                    deliverySoList.addAll(soList);
                    DeliveryNoteSOSingleton.getInstance().setList(deliverySoList);
                }else {
                    DeliveryNoteSOSingleton.getInstance().setList(soList);
                }
                soListAdapter.notifyDataSetChanged();
                closeSelection();
            }
        }
    }

    public void LoadSOs(String customer){
        soList.clear();
        soSelectList.clear();
        Cursor cursor = helper.GetSOs(customer);
        if(cursor!=null&&cursor.moveToFirst()){
            for(int i=0;i<cursor.getCount();i++){
                soSelectList.add(cursor.getString(cursor.getColumnIndex("DocNo")));
                cursor.moveToNext();
                if(i+1==cursor.getCount()){
                    soSelectAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    public void LoadSupplier(String keyword){
        supplierList.clear();
        try {
            Cursor cursor = helper.GetCustomer(keyword);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    supplierList.add(new SupplierSearch(
                            cursor.getString(cursor.getColumnIndex("Cusomer")),
                            cursor.getString(cursor.getColumnIndex("HeaderId"))
                    ));
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        rv_search.setAdapter(customerAdapter);
                    }
                }
            }else {
                supplierList.add(new SupplierSearch(
                        "--Empty result--",
                        "0"
                ));
                rv_search.setAdapter(customerAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void searchSupplierAlert(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_layout, null);
        customerAdapter =new SupplierSearchAdapter(requireActivity(),supplierList);
        et_search_input = view.findViewById(R.id.search_edit);
        rv_search = view.findViewById(R.id.search_recycler);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setView(view);
        supplierSearchAlert = builder.create();
        supplierSearchAlert.show();
        LoadSupplier("");
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

            customerAdapter.setOnClickListener(new SupplierSearchAdapter.OnClickListener() {
                @Override
                public void onItemClick(SupplierSearch search_item) {
                    if(!search_item.getsSupplierId().equals("0"))
                        if(supplierSearchAlert.isShowing()) {
                            soSelectList.clear();
                            LoadSOs(search_item.getsSupplierId());
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
            soList.clear();
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
                            LoadSOs(supplier);
                        }
                    }
                }

                //Load SOs
                String pos =  cursor.getString(cursor.getColumnIndex("sPONo"));
                if(!pos.isEmpty()){
                    String[] array =   pos.split("\\s*,\\s*");
                    c =new ArrayList<>(Arrays.asList(array));
                    deliverySoList.addAll(c);
                    soListAdapter.notifyDataSetChanged();
                    if(EditMode){
                        DeliveryNoteSOSingleton.getInstance().setList(deliverySoList);
                    }
                }


                String narration = cursor.getString(cursor.getColumnIndex("sNarration"));
                et_narration.setText(narration);

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private void LoadBodyValues() {
        List<DeliveryNoteBody> listMain = new ArrayList<>();
        try {
            Cursor cursor =  helper.GetDeliveryBodyData(DocNo);

            if(cursor!=null&&cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    listMain.add(new DeliveryNoteBody(
                            cursor.getString(cursor.getColumnIndex("sSONo")),
                            cursor.getString(cursor.getColumnIndex("fSOQty")),
                            helper.GetProductName(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            helper.GetProductCode(cursor.getString(cursor.getColumnIndex("iProduct"))),
                            helper.GetPendingSOTempQty(cursor.getString(cursor.getColumnIndex("sPONo")),cursor.getString(cursor.getColumnIndex("iProduct"))),
                            cursor.getString(cursor.getColumnIndex("iProduct")),
                            cursor.getString(cursor.getColumnIndex("iWarehouse")),
                            cursor.getString(cursor.getColumnIndex("sAttachment")),
                            cursor.getString(cursor.getColumnIndex("sRemarks")),
                            cursor.getString(cursor.getColumnIndex("fQty")),
                            cursor.getString(cursor.getColumnIndex("Unit"))));
                    cursor.moveToNext();
                    if(i+1==cursor.getCount()){
                        DeliveryNoteBodySingleton.getInstance().setList(listMain);
                    }
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.delivery_note_header_fragment,container,false);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        helper = new DatabaseHelper(requireActivity());
        et_date = view.findViewById(R.id.date);
        et_narration = view.findViewById(R.id.narration);
        tv_add_so = view.findViewById(R.id.add_po);
        tv_doc_no = view.findViewById(R.id.doc_no);
        et_supplier = view.findViewById(R.id.supplier);
        Bundle bundle = getArguments();


        supplierList = new ArrayList<>();
        soList = new ArrayList<>();
        soSelectList = new ArrayList<>();
        deliverySoList = new ArrayList<>();


        soSelectAdapter = new POSelectAdapter(soSelectList,requireActivity());
        soListAdapter = new POListAdapter(soList,requireActivity());


        rv_pos = view.findViewById(R.id.rv_po);
        rv_pos.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        rv_pos.setAdapter(soListAdapter);

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
                            soListAdapter = new POListAdapter(deliverySoList, requireActivity());
                            rv_pos.setAdapter(soListAdapter);
                            LoadBodyValues();
                            LoadValueForEditing();

                            soListAdapter.setOnClickListener(new POListAdapter.OnClickListener() {
                                @Override
                                public void onRemoveItemClick(String po, int pos) {
                                    if (deliverySoList != null && deliverySoList.size() > 0) {
                                        deliverySoList.remove(pos);
                                        soListAdapter.notifyDataSetChanged();
                                    }
                                    DeliveryNoteSOSingleton.getInstance().setList(soList);
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                handler.postDelayed(r, 500);

            }else {
                soListAdapter = new POListAdapter(soList,requireActivity());
                rv_pos.setAdapter(soListAdapter);

                soListAdapter.setOnClickListener(new POListAdapter.OnClickListener() {
                    @Override
                    public void onRemoveItemClick(String po, int pos) {
                        if(soList !=null && soList.size()>0){
                            soList.remove(pos);
                            soListAdapter.notifyDataSetChanged();
                        }
                        DeliveryNoteSOSingleton.getInstance().setList(soList);
                    }
                });
            }
        }
        et_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSupplierAlert();
            }
        });



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







        tv_add_so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POSelectionDialog();
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
