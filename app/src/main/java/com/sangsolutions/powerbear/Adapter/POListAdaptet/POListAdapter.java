package com.sangsolutions.powerbear.Adapter.POListAdaptet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class POListAdapter extends RecyclerView.Adapter<POListAdapter.ViewHolder> {

    List<String> list;
    Context context;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public POListAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.po_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String poList = list.get(position);
        holder.DocNo.setText(list.get(position));
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onClickListener.onRemoveItemClick(poList,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onRemoveItemClick(String poList, int pos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView DocNo;
        ImageView close;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocNo = itemView.findViewById(R.id.title);
            close = itemView.findViewById(R.id.close);
        }
    }
}
