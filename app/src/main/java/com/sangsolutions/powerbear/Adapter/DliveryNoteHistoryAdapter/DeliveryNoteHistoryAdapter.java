package com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter;

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

public class DeliveryNoteHistoryAdapter extends RecyclerView.Adapter<DeliveryNoteHistoryAdapter.ViewHolder> {
    List<DeliveryNoteHistory> list;
    Context context;

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public DeliveryNoteHistoryAdapter(List<DeliveryNoteHistory> list, Context context) {
        this.list = list;
        this.context = context;
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

        holder.HeaderId.setText("Id :"+list.get(position).getHeaderId());
        holder.Qty.setText("Total Qty :"+list.get(position).getQty());

   holder.edit.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           onClickListener.onEditItemClick(view, deliveryNoteHistory, position);
       }
   });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onDeleteItemClick(view, deliveryNoteHistory, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onEditItemClick(View view, DeliveryNoteHistory deliveryNoteHistory, int pos);
        void onDeleteItemClick(View view, DeliveryNoteHistory deliveryNoteHistory, int pos);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView HeaderId,Qty;
        ImageButton edit,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HeaderId = itemView.findViewById(R.id.header_id);
            Qty = itemView.findViewById(R.id.total_qty);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
