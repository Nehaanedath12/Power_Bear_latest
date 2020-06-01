package com.sangsolutions.powerbear.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.Database.StockCount;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.StockCountSingleton;
import com.sangsolutions.powerbear.Tools;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HeaderFragment extends Fragment {
EditText date,remarks;
TextView warehouse,VoucherNo;
DatabaseHelper helper;
String warehouse_id = "";
Button save,close;
    Date c;
    @SuppressLint("SimpleDateFormat") SimpleDateFormat df;
private void Save(){
    List<ListProduct> list = StockCountSingleton.getInstance().getList();
if(!remarks.getText().toString().isEmpty()&&!date.getText().toString().isEmpty()&&!list.isEmpty())
{
    String s_date,s_voucher_no,s_remarks;

    s_date = date.getText().toString();
    s_voucher_no = helper.GetNewVoucherNo();
    s_remarks = remarks.getText().toString();
    StockCount s = new StockCount();
    for(int i = 0 ; i < list.size(); i ++){
        s.setiVoucherNo(s_voucher_no);
        s.setdDate(s_date);
        s.setiWarehouse(warehouse_id);
        s.setiProduct(list.get(i).getiProduct());
        s.setfQty(list.get(i).getQty());
        s.setsUnit(list.get(i).getUnit());
        s.setsRemarks(s_remarks);
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


private void SaveAlert(){
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

private void CloseAlert(){
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


@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_frgment, container, false);
        date = view.findViewById(R.id.date);
        warehouse = view.findViewById(R.id.warehouse_name);
        save = view.findViewById(R.id.save);
        close = view.findViewById(R.id.close);
        VoucherNo = view.findViewById(R.id.voucher_no);
         remarks = view.findViewById(R.id.remarks);
        helper = new DatabaseHelper(getActivity());
        VoucherNo.setText("Voucher No :"+helper.GetNewVoucherNo());

    if(getArguments() != null) {
        warehouse_id = getArguments().getString("warehouse");
    }
        if(!helper.GetWarehouseById(warehouse_id).equals("")){
            warehouse.setText(helper.GetWarehouseById(warehouse_id));
        }else{
            Objects.requireNonNull(getActivity()).finish();
        }
         c = Calendar.getInstance().getTime();
         df = new SimpleDateFormat("yyyy-MM-dd");
        date.setText(df.format(c));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                       /* String StringDate = checkDigit(dayOfMonth) +
                                "-" +
                                checkDigit(month + 1) +
                                "-" +
                                year;*/

                        String StringDate = year +
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-" +
                                Tools.checkDigit(dayOfMonth);
                        date.setText(StringDate);
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


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAlert();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAlert();
            }
        });

        return view;
    }
}
