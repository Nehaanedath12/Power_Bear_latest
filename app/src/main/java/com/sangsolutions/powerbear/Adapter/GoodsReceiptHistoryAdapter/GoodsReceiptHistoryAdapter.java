package com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptHistoryAdapter extends RecyclerView.Adapter<GoodsReceiptHistoryAdapter.ViewHolder> {
    final List<GoodsReceiptHistory> list;
    final Context context;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }

    public void removeData(int position) {
        list.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public GoodsReceiptHistoryAdapter(List<GoodsReceiptHistory> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }


    private void displayImage(ViewHolder holder, GoodsReceiptHistory goodsReceiptHistory) {
        if (goodsReceiptHistory.getDocNo().isEmpty()) {
            holder.img_check.setImageDrawable(null);
        } else {
            holder.img_check.setImageResource(R.drawable.ic_check_red);
        }
    }


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.img_check.setVisibility(View.VISIBLE);
        } else {
            holder.img_check.setVisibility(View.GONE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
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
        holder.card_main.setActivated(selected_items.get(position,false));
        holder.DocNo.setText("DocNo :" + list.get(position).getDocNo());
        holder.Qty.setText("Total Qty :" + list.get(position).getQty());


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onEditItemClick(goodsReceiptHistory,position);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteItemClick(goodsReceiptHistory, position);
            }
        });

        holder.card_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(goodsReceiptHistory,position);
            }
        });

        holder.card_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onClickListener==null) return false;
                else {
                    onClickListener.onItemLongClick(position);
                }
                return true;
            }
        });

        toggleCheckedIcon(holder, position);
        displayImage(holder, goodsReceiptHistory);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onEditItemClick(GoodsReceiptHistory goodsReceiptHistory, int position);
        void onDeleteItemClick(GoodsReceiptHistory goodsReceiptHistory, int pos);
        void onItemClick(GoodsReceiptHistory goodsReceiptHistory, int pos);
        void onItemLongClick(int pos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView DocNo;
        final TextView Qty;
        final ImageButton edit;
        final ImageButton delete;
        final ImageView img_check;
        final CardView card_main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocNo = itemView.findViewById(R.id.DocNo);
            Qty = itemView.findViewById(R.id.total_qty);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            img_check = itemView.findViewById(R.id.check);
            card_main = itemView.findViewById(R.id.card);
        }
    }
}