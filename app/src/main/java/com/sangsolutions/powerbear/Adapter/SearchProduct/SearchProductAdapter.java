package com.sangsolutions.powerbear.Adapter.SearchProduct;

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

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {
    private Context context;
    private List<SearchProduct> list;
    private OnClickListener onClickListener;

    public SearchProductAdapter(Context context, List<SearchProduct> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SearchProduct search_item = list.get(position);
        holder.Name.setText(list.get(position).Name);
        holder.Code.setText(list.get(position).Code);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_item.Code.equals("") )
                    onClickListener.onItemClick(v, search_item, position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, SearchProduct search_item, int pos);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Code, Name;
        CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Code = itemView.findViewById(R.id.code);
            cardView = itemView.findViewById(R.id.cheque);
        }
    }
}
