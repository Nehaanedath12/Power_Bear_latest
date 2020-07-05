package com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class Goods_Delivery_Adapter extends RecyclerView.Adapter<Goods_Delivery_Adapter.ViewHolder> {
    List<Goods_Stock> list;
    Context context;


    public Goods_Delivery_Adapter(List<Goods_Stock> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public Goods_Delivery_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.product_item_goods_delivery,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Goods_Delivery_Adapter.ViewHolder holder, int position) {
        holder.customer.setText(list.get(position).getVendor());
        holder.code.setText(list.get(position).getProductCode());
        holder.product.setText(list.get(position).getProductName());
        holder.qty.setText(list.get(position).getQty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product,code,customer,qty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.product);
            qty = itemView.findViewById(R.id.qty);
            customer = itemView.findViewById(R.id.customer);
            code = itemView.findViewById(R.id.code);
        }
    }
}
