package com.sangsolutions.powerbear.Adapter.CustomerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class SearchCustomerAdapter extends RecyclerView.Adapter<SearchCustomerAdapter.ViewHolder> {
    private final Context context;
    private final List<SearchCustomer> list;
    private OnClickListener onClickListener;

    public SearchCustomerAdapter(Context context, List<SearchCustomer> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_products_or_customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SearchCustomer search_item = list.get(position);
        holder.Name.setText(list.get(position).Name);
        holder.Code.setText(list.get(position).Code);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_item.Code.equals("") )
                    onClickListener.onItemClick(search_item);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(SearchCustomer search_item);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Code;
        final TextView Name;
        final CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Code = itemView.findViewById(R.id.code);
            cardView = itemView.findViewById(R.id.cheque);
        }
    }
}
