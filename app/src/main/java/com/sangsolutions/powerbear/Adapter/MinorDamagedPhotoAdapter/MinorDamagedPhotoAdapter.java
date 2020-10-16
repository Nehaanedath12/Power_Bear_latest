package com.sangsolutions.powerbear.Adapter.MinorDamagedPhotoAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;

import java.util.List;

public class MinorDamagedPhotoAdapter extends RecyclerView.Adapter<MinorDamagedPhotoAdapter.ViewHolder> {

    OnClickListener listener;
    private final Context context;
    private final List<String> list;

    public MinorDamagedPhotoAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String photo = list.get(position);
        Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(list.get(position)), new Tools().GetPixels(100,context), new Tools().GetPixels(150,context));
        holder.photo.setImageBitmap(bm);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnDeleteListener(list.get(position), position);
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnImageClickListener(holder.photo,list,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void OnDeleteListener(String photo, int potions);
        void OnImageClickListener(ImageView view,List<String> photo, int potions);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView delete;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}
