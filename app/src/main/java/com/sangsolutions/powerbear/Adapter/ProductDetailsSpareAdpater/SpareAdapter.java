package com.sangsolutions.powerbear.Adapter.ProductDetailsSpareAdpater;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;

import java.util.List;

public class SpareAdapter extends RecyclerView.Adapter<SpareAdapter.ViewHolder>{

        List<SpareClass>list;
        Context context;

    public SpareAdapter(Context context, List<SpareClass> list) {
        this.list=list;
        this.context=context;
        }

    @NonNull
    @Override
    public SpareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.spare_adapter,parent,false);
        return new ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull SpareAdapter.ViewHolder holder, int position) {

        holder.name.setText(list.get(position).Name);
        holder.code.setText(list.get(position).Code2);
        holder.position.setText(list.get(position).iPosition);
        holder.images.setText(list.get(position).Imagescount);
        holder.files.setText(list.get(position).Filescount);
        if (position % 2 == 0) {
            holder.parent.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.parent.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        }

    @Override
    public int getItemCount() {
        return list.size();
        }

    public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView name,code,position,images,files;
        LinearLayout parent;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        code=itemView.findViewById(R.id.code);
        position=itemView.findViewById(R.id.position);
        images=itemView.findViewById(R.id.images);
        files=itemView.findViewById(R.id.files);
        parent=itemView.findViewById(R.id.parent);
    }
}
}
