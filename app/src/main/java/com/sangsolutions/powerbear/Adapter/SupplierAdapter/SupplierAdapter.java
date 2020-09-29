package com.sangsolutions.powerbear.Adapter.SupplierAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class SupplierAdapter extends BaseAdapter {

    List<Supplier> list;
    Context context;

    public SupplierAdapter(List<Supplier> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item,parent,false);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(list.get(position).getsSupplierName());
        return view;
    }


   public static class Supplier {

        String sSupplierName,sSupplierId;

        public Supplier(String sSupplierName, String sSupplierId) {
            this.sSupplierName = sSupplierName;
            this.sSupplierId = sSupplierId;
        }

        public String getsSupplierName() {
            return sSupplierName;
        }

        public void setsSupplierName(String sSupplierName) {
            this.sSupplierName = sSupplierName;
        }

        public String getsSupplierId() {
            return sSupplierId;
        }

        public void setsSupplierId(String sSupplierId) {
            this.sSupplierId = sSupplierId;
        }
    }

}
