package com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

public class RemarksTypeAdapter extends BaseAdapter {

    List<RemarksType> list;
    Context context;

    public RemarksTypeAdapter(List<RemarksType> list, Context context) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item,parent,false);
        TextView tv_name = view.findViewById(android.R.id.text1);
        tv_name.setText(list.get(position).getsName());
        return view;
    }
}
