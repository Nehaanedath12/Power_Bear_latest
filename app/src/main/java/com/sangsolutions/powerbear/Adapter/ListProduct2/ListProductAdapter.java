package com.sangsolutions.powerbear.Adapter.ListProduct2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    private Context context;
    private List<ListProduct> list;
    private OnClickListener onClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ListProductAdapter(Context context, List<ListProduct> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item2, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ListProduct productList = list.get(position);
        holder.Name.setText(list.get(position).getName());
        holder.Code.setText(list.get(position).getCode());
        holder.Qty.setText(list.get(position).getQty());
        holder.unit.setText(list.get(position).getUnit());

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, productList, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, ListProduct product, int pos);

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Code, Qty,unit;
        ImageButton menu;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.product_name);
            Code = itemView.findViewById(R.id.product_code);
            Qty = itemView .findViewById(R.id.product_qty);
            unit = itemView.findViewById(R.id.product_unit);
            menu = itemView.findViewById(R.id.menu);
        }
    }
}


