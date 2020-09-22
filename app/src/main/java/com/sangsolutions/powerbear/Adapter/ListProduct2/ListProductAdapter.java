package com.sangsolutions.powerbear.Adapter.ListProduct2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList;
import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountListAdapter;
import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    private Context context;
    private List<ListProduct> list;
    private OnClickListener onClickListener;
    private String EditMode = "";
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }

    public ListProductAdapter(Context context, List<ListProduct> list, String EditMode) {
        this.context = context;
        this.list = list;
        this.EditMode = EditMode;
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


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.img_check.setVisibility(View.VISIBLE);
            holder.ll_main.setBackgroundColor(Color.parseColor("#fca1a3"));
        } else {
            holder.img_check.setVisibility(View.GONE);
            holder.ll_main.setBackgroundColor(Color.WHITE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
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
        holder.remarks.setText(list.get(position).getsRemarks());

        if(!EditMode.equals("view")) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemDeleteClickListener(v, productList, position);
                }
            });
            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClick(view, productList, position);
                }
            });
            holder.ll_main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onClickListener == null) return false;
                    else {
                        onClickListener.onItemLongClick(view, productList, position);
                    }
                    return true;
                }
            });
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        toggleCheckedIcon(holder, position);
        // displayImage(holder, productList);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, ListProduct product, int pos);

        void onItemDeleteClickListener(View view, ListProduct product, int pos);

        void onItemLongClick(View view, ListProduct product, int pos);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Code, Qty, unit, remarks;
        ImageView delete;
        LinearLayout ll_main;
        ImageView img_check;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.product_name);
            Code = itemView.findViewById(R.id.product_code);
            Qty = itemView.findViewById(R.id.product_qty);
            unit = itemView.findViewById(R.id.product_unit);
            remarks = itemView.findViewById(R.id.product_remarks);
            delete = itemView.findViewById(R.id.delete);
            ll_main = itemView.findViewById(R.id.ll_main);
            img_check = itemView.findViewById(R.id.check);
        }
    }
}


