package com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory;
import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistoryAdapter;
import com.sangsolutions.powerbear.R;

import java.util.List;

public class GoodsReceiptHistoryAdapter extends RecyclerView.Adapter<GoodsReceiptHistoryAdapter.ViewHolder> {
    List<GoodsReceiptHistory> list;
    Context context;

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GoodsReceiptHistoryAdapter(List<GoodsReceiptHistory> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GoodsReceiptHistory goodsReceiptHistory = list.get(position);

        holder.HeaderId.setText("Id :" + list.get(position).getHeaderId());
        holder.Qty.setText("Total Qty :" + list.get(position).getQty());


        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, goodsReceiptHistory, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, GoodsReceiptHistory goodsReceiptHistory, int pos);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView HeaderId, Qty;
        ImageButton menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HeaderId = itemView.findViewById(R.id.header_id);
            Qty = itemView.findViewById(R.id.total_qty);
            menu = itemView.findViewById(R.id.menu);
        }
    }
}