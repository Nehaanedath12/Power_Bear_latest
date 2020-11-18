package com.sangsolutions.powerbear.Adapter.ProductClassAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class ProductClassAdapter extends RecyclerView.Adapter<ProductClassAdapter.ViewHolder> {
    Context context;
    List<ProductClass> list;
    private OnClickListener onClickListener;

    public ProductClassAdapter(Context context, List<ProductClass> list) {
        this.context = context;
        this.list = list;

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_product_rv, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final ProductClass search_item = list.get(position);
        holder.P_Name.setText(list.get(position).product_name);
        holder.P_code.setText(list.get(position).code_p);
        holder.P_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_item.getCode_p().equals(""))
                    onClickListener.onItemClick(search_item, position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(ProductClass productClass, int position);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView P_Name, P_code;
        LinearLayout P_linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            P_linear = itemView.findViewById(R.id.P_Linear);
            P_Name = itemView.findViewById(R.id.product_name_T);
            P_code = itemView.findViewById(R.id.product_code_T);
        }
    }
}