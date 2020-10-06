package com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sangsolutions.powerbear.R;

import java.util.List;

public class GoodsReceiptBodyAdapter extends RecyclerView.Adapter<GoodsReceiptBodyAdapter.ViewHolder> {

  Context context;
  List<GoodsReceiptBody> list;
  private OnClickListener onClickListener;

    public GoodsReceiptBodyAdapter(Context context, List<GoodsReceiptBody> list) {
        this.context = context;
        this.list = list;
    }



    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public GoodsReceiptBodyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.goods_receipt_body_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsReceiptBodyAdapter.ViewHolder holder, final int position) {
        final GoodsReceiptBody body = list.get(position);
        holder.PONo.setText(list.get(position).getsPONo());
        holder.Name.setText(list.get(position).getName());
        holder.Code.setText(list.get(position).getCode());
        holder.warehouse.setText(list.get(position).getsWarehouse());
        holder.poQty.setText(list.get(position).getfPOQty());
        holder.qty.setText(list.get(position).getfQty());
        holder.unit.setText(list.get(position).getUnit());
        holder.remarks.setText(list.get(position).getsRemarks());
        holder.minorQty.setText(list.get(position).getfMinorDamageQty());
        holder.minorRemarks.setText(list.get(position).getsMinorRemarks());
        holder.damagedQty.setText(list.get(position).getfDamagedQty());
        holder.damagedRemarks.setText(list.get(position).getsDamagedRemarks());

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(GoodsReceiptBody goodsReceiptBody,int pos);
        void ItemDeleteClick(GoodsReceiptBody goodsReceiptBody,int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView PONo,Name,Code,warehouse,poQty,qty,unit,remarks,minorQty,minorRemarks,damagedQty,damagedRemarks;
        ImageView delete;
        RelativeLayout rl_1;
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
            delete = itemView.findViewById(R.id.img_delete);
            rl_1 = itemView.findViewById(R.id.rl_1);
        }
    }
}
