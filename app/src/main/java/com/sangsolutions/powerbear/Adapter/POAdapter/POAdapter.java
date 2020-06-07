package com.sangsolutions.powerbear.Adapter.POAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;

import java.util.List;

public class POAdapter extends RecyclerView.Adapter<POAdapter.ViewHolder>{
    private OnClickListener onClickListener;
    private Context context;
    private List<PO> list;


    public POAdapter(Context context, List<PO> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pono_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PO po = list.get(position);

        holder.DocNo.setText("Doc No:"+list.get(position).DocNo);
        holder.DocDate.setText("Doc Date:"+ Tools.ConvertDate(list.get(position).DocDate));
        if (position % 2 == 0) {
            holder.lyt_parent.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.lyt_parent.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, po, position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, PO po, int pos);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView DocNo, DocDate;
        LinearLayout lyt_parent;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            DocNo = itemView.findViewById(R.id.PONo);
            DocDate = itemView.findViewById(R.id.PODate);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }

}
