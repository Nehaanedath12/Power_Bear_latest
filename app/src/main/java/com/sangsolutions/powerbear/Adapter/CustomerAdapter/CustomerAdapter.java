package com.sangsolutions.powerbear.Adapter.CustomerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class CustomerAdapter extends BaseAdapter {


    private List<Customer> list;
    private Context context;


    public CustomerAdapter(List<Customer> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).getMasterId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.customer_item, parent, false);
        TextView name = view.findViewById(R.id.name);
        TextView code = view.findViewById(R.id.code);
        RelativeLayout parent_layout = view.findViewById(R.id.parent);
        if (position % 2 == 0) {
            parent_layout.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            parent_layout.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        name.setText(list.get(position).getName());
        code.setText(list.get(position).getCode());
        return view;
    }
}