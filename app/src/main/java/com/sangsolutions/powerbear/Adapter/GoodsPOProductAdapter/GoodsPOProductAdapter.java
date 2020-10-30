package com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class GoodsPOProductAdapter extends RecyclerView.Adapter<GoodsPOProductAdapter.ViewHolder> {
List<GoodsPOProduct> list;
Context context;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private OnClickListener onClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
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
            holder.rl_1.setBackgroundColor(Color.parseColor("#FABABC"));
        } else {
            holder.rl_1.setBackground(null);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }

    public GoodsPOProductAdapter(List<GoodsPOProduct> list, Context context) {
        this.list = list;
        this.context = context;
    }


    public void DeselectAll(){
        if(list.size()>0){
            clearSelections();
        }
    }

    public void SelectAll(){
        if(list.size()>0){
            for(int i = 0;i<list.size();i++) {
                selected_items.put(i, true);
                notifyDataSetChanged();
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.good_po_product_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.rl_1.setActivated(selected_items.get(position,false));
        GoodsPOProduct goodsPOProduct = list.get(position);
        holder.poNo.setText(list.get(position).getDocNo());
        holder.productName.setText(list.get(position).getName());
        holder.POQty.setText(String.valueOf(Integer.parseInt(list.get(position).getPOQty())-Integer.parseInt(list.get(position).getTempQty())));
        holder.Unit.setText(list.get(position).getUnit());

        holder.rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null){
                    onClickListener.onItemClick(position);
                }
            }
        });

        toggleCheckedIcon(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnClickListener {
        void onItemClick(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView poNo,productName,POQty,Unit;
        RelativeLayout rl_1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poNo = itemView.findViewById(R.id.pono);
            productName = itemView.findViewById(R.id.product_name);
            POQty = itemView.findViewById(R.id.poQty);
            Unit = itemView.findViewById(R.id.unit);
            rl_1 = itemView.findViewById(R.id.rl_1);
        }
    }
}
