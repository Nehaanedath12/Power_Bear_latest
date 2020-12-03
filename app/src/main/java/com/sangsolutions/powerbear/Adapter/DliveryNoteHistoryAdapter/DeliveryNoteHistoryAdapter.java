package com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter;

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

import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList;
import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountListAdapter;
import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteHistoryAdapter extends RecyclerView.Adapter<DeliveryNoteHistoryAdapter.ViewHolder> {
    final List<DeliveryNoteHistory> list;
    final Context context;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }
    public DeliveryNoteHistoryAdapter(List<DeliveryNoteHistory> list, Context context) {
        this.list = list;
        this.context = context;
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


    private void displayImage(ViewHolder holder, DeliveryNoteHistory deliveryNoteHistory) {
        if (deliveryNoteHistory.getiVoucherNo().isEmpty()) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DeliveryNoteHistory deliveryNoteHistory = list.get(position);

        holder.HeaderId.setText("DocNo :"+list.get(position).getiVoucherNo());
        holder.Qty.setText("Total Qty :"+list.get(position).getQty());

   holder.edit.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           onClickListener.onEditItemClick(deliveryNoteHistory,position);
       }
   });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onDeleteItemClick(deliveryNoteHistory, position);
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClick(deliveryNoteHistory,position);
            }
        });

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onClickListener==null) return false;
                else {
                    onClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
        toggleCheckedIcon(holder, position);
        displayImage(holder, deliveryNoteHistory);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onEditItemClick(DeliveryNoteHistory deliveryNoteHistory, int position);
        void onDeleteItemClick(DeliveryNoteHistory deliveryNoteHistory, int pos);

        void onItemClick(DeliveryNoteHistory deliveryNoteHistory, int pos);
        void onItemLongClick(int pos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView HeaderId;
        final TextView Qty;
        final ImageButton edit;
        final ImageButton delete;
        final CardView card;
        final ImageView img_check;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HeaderId = itemView.findViewById(R.id.DocNo);
            Qty = itemView.findViewById(R.id.total_qty);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            card = itemView.findViewById(R.id.card);
            img_check = itemView.findViewById(R.id.check);
        }
    }
}
