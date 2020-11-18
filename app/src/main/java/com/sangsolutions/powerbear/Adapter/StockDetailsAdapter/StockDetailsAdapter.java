package com.sangsolutions.powerbear.Adapter.StockDetailsAdapter;

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

public class StockDetailsAdapter extends RecyclerView.Adapter<StockDetailsAdapter.ViewHolder> {
    Context context;
    List<StockDetailsClass>list;
    String iType;

    public StockDetailsAdapter(Context context, List<StockDetailsClass> list, String iType) {
        this.context = context;
        this.list = list;
        this.iType=iType;
    }

    @NonNull
    @Override
    public StockDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.stock_count_report_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockDetailsAdapter.ViewHolder holder, int position) {
        holder.product.setText(list.get(position).getProduct());
        holder.productCode.setText(list.get(position).getProductCode());
        holder.balQty.setText(list.get(position).getBalQty());

        if(iType.equals("1")) {
            holder.warehouse.setVisibility(View.VISIBLE);
            holder.warehouse.setText(list.get(position).getWarehouse());
        }

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
        TextView product,productCode,warehouse,balQty;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product=itemView.findViewById(R.id.product);
            productCode=itemView.findViewById(R.id.product_code);
            warehouse=itemView.findViewById(R.id.warehouse);
            balQty=itemView.findViewById(R.id.balQty);
            parent=itemView.findViewById(R.id.parent);
        }
    }
}