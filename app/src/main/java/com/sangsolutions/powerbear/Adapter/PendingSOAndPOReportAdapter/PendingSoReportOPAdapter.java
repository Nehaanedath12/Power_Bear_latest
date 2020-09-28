package com.sangsolutions.powerbear.Adapter.PendingSOAndPOReportAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;

import java.util.List;

public class PendingSoReportOPAdapter extends RecyclerView.Adapter<PendingSoReportOPAdapter.ViewHolder> {
    final List<PendingSoReportOP> list;
    final Context context;


    public PendingSoReportOPAdapter(List<PendingSoReportOP> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.po_so_report_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.DocDate.setText(Tools.dateFormat2(Tools.ConvertDate(list.get(position).getDocDate())));
        holder.Customer.setText(list.get(position).getCusomer());
        holder.ProductName.setText(list.get(position).getProductName());
        holder.ProductCode.setText(list.get(position).getProductCode());
        holder.Qty.setText(list.get(position).getQty());
        holder.unit.setText(list.get(position).getUnit());
        holder.iVoucherNo.setText(list.get(position).getDocNo());

        if (position % 2 == 0) {
            holder.rl_parent.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.rl_parent.setBackgroundColor(Color.rgb(255, 255, 255));
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView DocDate;
        final TextView Customer;
        final TextView ProductName;
        final TextView ProductCode;
        final TextView Qty;
        final TextView unit;
        final TextView iVoucherNo;
        final RelativeLayout rl_parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocDate = itemView.findViewById(R.id.DocDate);
            Customer = itemView.findViewById(R.id.Customer);
            ProductName = itemView.findViewById(R.id.ProductName);
            ProductCode = itemView.findViewById(R.id.ProductCode);
            Qty = itemView.findViewById(R.id.Qty);
            unit = itemView.findViewById(R.id.unit);
            rl_parent = itemView.findViewById(R.id.parent);
            iVoucherNo = itemView.findViewById(R.id.Voucher_no);

        }
    }
}
