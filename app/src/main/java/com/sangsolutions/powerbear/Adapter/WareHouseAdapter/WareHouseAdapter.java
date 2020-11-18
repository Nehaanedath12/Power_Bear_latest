package com.sangsolutions.powerbear.Adapter.WareHouseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class WareHouseAdapter extends RecyclerView.Adapter<WareHouseAdapter.ViewHolder> {

    Context context;
    List<WareHouse> list;
    private OnClickListener onClickListener;


    public WareHouseAdapter(Context context, List<WareHouse> list) {
        this.context = context;
        this.list = list;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_warehouselist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final WareHouse warehouse = list.get(position);

        holder.name_W.setText(list.get(position).getName());
        holder.Linear_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnItemClick(warehouse, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void OnItemClick(WareHouse warehouseClass, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_W;
        LinearLayout Linear_W;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_W = itemView.findViewById(R.id.warehouse_name_T);
            Linear_W = itemView.findViewById(R.id.W_Linear);
        }
    }
}