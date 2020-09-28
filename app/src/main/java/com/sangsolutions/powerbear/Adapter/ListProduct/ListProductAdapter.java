package com.sangsolutions.powerbear.Adapter.ListProduct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    private final Context context;
    private final List<ListProduct> list;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ListProductAdapter(Context context, List<ListProduct> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
      final ListProduct listProduct = list.get(position);

        holder.Name.setText(list.get(position).getName());
        holder.Code.setText(list.get(position).getCode());
        holder.Qty.setText(list.get(position).getQty());
        holder.PickedQty.setText(list.get(position).getPickedQty());
        holder.Unit.setText(list.get(position).getUnit());

        holder.PickedQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onClickListener.onTextChangedListener(holder.PickedQty,listProduct, position, s.toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onTextChangedListener(EditText et,ListProduct products, int pos, String text);

    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Name;
        final TextView Code;
        final TextView Qty;
        final TextView Unit;
        final EditText PickedQty;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

Name = itemView.findViewById(R.id.product_name);
Code = itemView.findViewById(R.id.product_code);
Qty = itemView .findViewById(R.id.product_qty);
Unit = itemView.findViewById(R.id.product_unit);
PickedQty = itemView.findViewById(R.id.product_picked_qty);

        }
    }
}


