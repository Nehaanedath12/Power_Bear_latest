package com.sangsolutions.powerbear.Adapter.PendingSOAndPOReportAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class PendingSoReportOPAdapter extends RecyclerView.Adapter<PendingSoReportOPAdapter.ViewHolder> {
    List<PendingSoReportOP> list;
    Context context;


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

        holder.DocNo.setText(list.get(position).getDocNo());
        holder.DocDate.setText(list.get(position).getDocDate());
        holder.Customer.setText(list.get(position).getCusomer());
        holder.SINo.setText(list.get(position).getSINo());
        holder.ProductName.setText(list.get(position).getProductName());
        holder.ProductCode.setText(list.get(position).getProductCode());
        holder.Qty.setText(list.get(position).getQty());
        holder.unit.setText(list.get(position).getUnit());

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView DocNo,DocDate,Customer,SINo,ProductName,ProductCode,Qty,unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocNo = itemView.findViewById(R.id.DocNo);
            DocDate = itemView.findViewById(R.id.DocDate);
            Customer = itemView.findViewById(R.id.Customer);
            SINo = itemView.findViewById(R.id.SINo);
            ProductName = itemView.findViewById(R.id.ProductName);
            ProductCode = itemView.findViewById(R.id.ProductCode);
            Qty = itemView.findViewById(R.id.Qty);
            unit = itemView.findViewById(R.id.unit);


        }
    }
}
