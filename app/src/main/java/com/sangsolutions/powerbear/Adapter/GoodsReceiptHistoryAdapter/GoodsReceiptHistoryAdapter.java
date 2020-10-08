package com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter;

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

public class GoodsReceiptHistoryAdapter extends RecyclerView.Adapter<GoodsReceiptHistoryAdapter.ViewHolder> {
    final List<GoodsReceiptHistory> list;
    final Context context;

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

        holder.DocNo.setText("DocNo :" + list.get(position).getDocNo());
        holder.Qty.setText("Total Qty :" + list.get(position).getQty());


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onEditItemClick(goodsReceiptHistory);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteItemClick(goodsReceiptHistory, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onEditItemClick(GoodsReceiptHistory goodsReceiptHistory);
        void onDeleteItemClick(GoodsReceiptHistory goodsReceiptHistory, int pos);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView DocNo;
        final TextView Qty;
        final ImageButton edit;
        final ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocNo = itemView.findViewById(R.id.DocNo);
            Qty = itemView.findViewById(R.id.total_qty);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}