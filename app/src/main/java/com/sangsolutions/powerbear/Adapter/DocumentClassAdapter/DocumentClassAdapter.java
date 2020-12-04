package com.sangsolutions.powerbear.Adapter.DocumentClassAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;

import java.util.List;

public class DocumentClassAdapter extends RecyclerView.Adapter<DocumentClassAdapter.ViewHolder> {

    Context context;
    List<DocumentClass> list;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(DocumentClass documentClass, int position);
    }

    public DocumentClassAdapter(FragmentActivity context, List<DocumentClass> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public DocumentClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.document_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentClassAdapter.ViewHolder holder, int position) {
        DocumentClass documentClass=list.get(position);
        holder.doc_text.setText(Tools.subString(list.get(position).FILE_NAME));
        holder.imageView.setImageResource(R.drawable.file_logo);
        holder.relative_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(documentClass,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doc_text;
        RelativeLayout relative_doc;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doc_text=itemView.findViewById(R.id.text_doc);
            relative_doc=itemView.findViewById(R.id.relative_doc);
            imageView=itemView.findViewById(R.id.file_logo);
        }
    }
}