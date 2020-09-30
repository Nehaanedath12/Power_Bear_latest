package com.sangsolutions.powerbear.Adapter.POSelectAdapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class POSelectAdapter extends RecyclerView.Adapter<POSelectAdapter.ViewHolder> {
    List<POSelect> list;
    Context context;
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
            holder.ll_1.setBackgroundResource(R.drawable.round_rect_lite_red);
            holder.close.setBackgroundResource(R.drawable.round_red);
            holder.close.setImageResource(R.drawable.ic_close_30dp);
        } else {
            holder.ll_1.setBackgroundResource(R.drawable.round_rect_lite_blue);
            holder.close.setBackgroundResource(R.drawable.round_blue);
            holder.close.setImageResource(R.drawable.ic_add);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }


    public POSelectAdapter(List<POSelect> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.po_select_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.ll_1.setActivated(selected_items.get(position,false));

        holder.title.setText(list.get(position).getDocNo());
        holder.ll_1.setOnClickListener(new View.OnClickListener() {
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
        TextView title;
        LinearLayout ll_1;
        ImageView close;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            ll_1 = itemView.findViewById(R.id.ll_1);
            close = itemView.findViewById(R.id.close);
        }
    }
}
