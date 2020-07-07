package com.sangsolutions.powerbear.Adapter.POAdapter;

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

public class POAdapter  extends BaseAdapter {
        List<PO> list;
        Context context;
        public POAdapter(List<PO> list,Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).DocNo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.dno_item,parent,false);

            TextView doc_no = view.findViewById(R.id.dno);
            TextView date = view.findViewById(R.id.title2);
            RelativeLayout lyt_parent= view.findViewById(R.id.lay_parent);
            TextView customer = view.findViewById(R.id.customer);
            doc_no.setText("Doc No : "+ list.get(position).DocNo);
            date.setText(list.get(position).DocDate);
            customer.setText(list.get(position).Cusomer);

            if (position % 2 == 0) {
               lyt_parent.setBackgroundColor(Color.rgb(234, 234, 234));
            } else {

                lyt_parent.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            return view ;
        }

}
