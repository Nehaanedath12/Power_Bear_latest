package com.sangsolutions.powerbear.Adapter.Report_SO_PO_Product_Details;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class SO_PO_DetailsAdapter extends RecyclerView.Adapter<SO_PO_DetailsAdapter.ViewHolder> {
    List<SO_PO_Details>list;
    Context context;
    public SO_PO_DetailsAdapter(Context context, List<SO_PO_Details> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.po_so_product_report_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).customerName);
        holder.voucherNo.setText(list.get(position).VoucherNo);
        holder.voucherDate.setText(list.get(position).VoucherDate);
        holder.qty.setText(String.valueOf(list.get(position).qty));
        holder.delivery_date.setText(list.get(position).DeliveryDate);
        holder.knno.setText(list.get(position).KNNo);
        if (position % 2 == 0) {
            holder.parent.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.parent.setBackgroundColor(Color.rgb(255, 255, 255));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,voucherNo,voucherDate,qty,delivery_date,knno;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            name=itemView.findViewById(R.id.customer_PR);
            voucherNo=itemView.findViewById(R.id.voucher_no_PR);
            voucherDate=itemView.findViewById(R.id.voucher_date_PR);
            qty=itemView.findViewById(R.id.qty_PR);
            delivery_date=itemView.findViewById(R.id.delivery_date);
            knno=itemView.findViewById(R.id.knno);
        }
    }
}
