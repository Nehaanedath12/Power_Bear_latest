package com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteBodyAdapter extends RecyclerView.Adapter<DeliveryNoteBodyAdapter.ViewHolder> {

    Context context;
    List<DeliveryNoteBody> list;
    private OnClickListener onClickListener;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    DatabaseHelper helper;

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


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.img_check.setVisibility(View.VISIBLE);
            holder.rl_1.setBackgroundColor(Color.parseColor("#fca1a3"));
        } else {
            holder.img_check.setVisibility(View.GONE);
            holder.rl_1.setBackgroundColor(Color.WHITE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }



    public DeliveryNoteBodyAdapter(Context context, List<DeliveryNoteBody> list) {
        this.context = context;
        this.list = list;
        helper = new DatabaseHelper(context);
    }



    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_receipt_body_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DeliveryNoteBody body = list.get(position);
        //holder.PONo.setText(list.get(position).getsPONo());



        holder.rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(body,position);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.ItemDeleteClick(body,position);
            }
        });
        holder.rl_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                else {
                    onClickListener.onItemLongClick(position);
                }
                return true;
            }
        });

        toggleCheckedIcon(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(DeliveryNoteBody Body,int pos);
        void ItemDeleteClick(DeliveryNoteBody body,int pos);
        void onItemLongClick(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView PONo,Name,Code,warehouse,poQty,qty,unit,remarks,minorQty,minorRemarks,damagedQty,damagedRemarks,remarksMinorType,remarksDamagedType;
        final ImageView delete;
        final RelativeLayout rl_1;
        final ImageView img_check;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PONo = itemView.findViewById(R.id.pono);
            Name = itemView.findViewById(R.id.name);
            Code = itemView.findViewById(R.id.code);
            warehouse = itemView.findViewById(R.id.warehouse);
            poQty = itemView.findViewById(R.id.poQty);
            qty = itemView.findViewById(R.id.qty);
            unit = itemView.findViewById(R.id.unit);
            remarks = itemView.findViewById(R.id.remarks);
            minorQty = itemView.findViewById(R.id.minorQty);
            minorRemarks = itemView.findViewById(R.id.minorRemarks);
            damagedQty = itemView.findViewById(R.id.damagedQty);
            damagedRemarks = itemView.findViewById(R.id.damagedRemarks);
            remarksMinorType = itemView.findViewById(R.id.remarksMinorType);
            remarksDamagedType = itemView.findViewById(R.id.remarksDamagedType);
            delete = itemView.findViewById(R.id.img_delete);
            rl_1 = itemView.findViewById(R.id.rl_1);
            img_check = itemView.findViewById(R.id.check);
        }
    }

}
