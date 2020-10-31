package com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SupplierSearchAdapter extends RecyclerView.Adapter<SupplierSearchAdapter.ViewHolder> {
    private final Context context;
    private final List<SupplierSearch> list;
    private OnClickListener onClickListener;


    public SupplierSearchAdapter(Context context, List<SupplierSearch> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SupplierSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierSearchAdapter.ViewHolder holder, int position) {
        final SupplierSearch  search_item = list.get(position);
        holder.tv_supplier.setText(list.get(position).getsSupplierName());
        holder.tv_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_item.getsSupplierId().equals("") )
                    onClickListener.onItemClick(search_item);
            }
        });
    }

    public interface OnClickListener {
        void onItemClick(SupplierSearch search_item);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_supplier;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_supplier = itemView.findViewById(android.R.id.text1);
        }
    }
}
