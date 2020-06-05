package com.sangsolutions.powerbear.Adapter.StockCountListAdapter;

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

public class StockCountListAdapter extends RecyclerView.Adapter<StockCountListAdapter.ViewHolder> {
List<StockCountList> list;
Context context;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public StockCountListAdapter(List<StockCountList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.stock_count_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final StockCountList stockCountList = list.get(position);

    holder.Vno.setText("VNo :"+list.get(position).VNo);
    holder.date.setText("Date :"+list.get(position).Date);
    holder.totalQty.setText("Total Qty :"+list.get(position).TotalQty);
    holder.warehouse.setText("Warehouse :"+list.get(position).Warehouse);

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, stockCountList, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, StockCountList stockCountList, int pos);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Vno,date,totalQty,warehouse;
        ImageButton menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Vno = itemView.findViewById(R.id.vno);
            date = itemView.findViewById(R.id.date);
            totalQty = itemView.findViewById(R.id.total_qty);
            warehouse = itemView.findViewById(R.id.warehouse);
            menu = itemView.findViewById(R.id.menu);
        }
    }
}
