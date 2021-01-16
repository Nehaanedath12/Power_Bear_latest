package com.sangsolutions.powerbear.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.sangsolutions.powerbear.Adapter.DocumentClassAdapter.DocumentClass;
import com.sangsolutions.powerbear.Adapter.DocumentClassAdapter.DocumentClassAdapter;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailAttachmentsFragment extends Fragment {
    String iProduct;
    RecyclerView RV_image,RV_doc;

    List<ImageClass> imageClassList;
    ImageAdapter imageAdapter;

    List<DocumentClass>docClassList;
    DocumentClassAdapter docAdapter;

    View view,viewMain;
    ImageView image_show,cancel;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    FrameLayout frame_layout;
    WebView webView;

    ImageView forward,backward;
    int current_position=0,current_position_doc=0;

    ImageView mProgressBar, mProgressBar_alert;
    private AnimationDrawable animationDrawable,animationDrawable_alert;

    CardView image_text,file_text;

    FrameLayout emptyIcon;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMain=LayoutInflater.from(getContext()).inflate(R.layout.product_detail_attachments_fragment,container,false);
        RV_image=viewMain.findViewById(R.id.image_RV);
        RV_doc=viewMain.findViewById(R.id.word_RV);
        mProgressBar =viewMain.findViewById(R.id.main_progress);
        image_text=viewMain.findViewById(R.id.card_image);
        file_text=viewMain.findViewById(R.id.card_doc);

        image_text.setVisibility(View.GONE);
        file_text.setVisibility(View.GONE);


        emptyIcon=viewMain.findViewById(R.id.empty_frame);

        assert getArguments() != null;
        iProduct = getArguments().getString("iProduct");

        imageClassList=new ArrayList<>();
        imageAdapter=new ImageAdapter(getActivity(),imageClassList);

        docClassList=new ArrayList<>();
        docAdapter=new DocumentClassAdapter(getActivity(),docClassList);

        mProgressBar.setBackgroundResource(R.drawable.loading);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();


        RV_image.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        RV_doc.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

//        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        } else {

        loadImage();
        loadDocument();
//        }
        return viewMain;
    }





    private void loadImage() {
        mProgressBar.setVisibility(View.VISIBLE);
        animationDrawable.start();

        AndroidNetworking.get("http://"+new Tools().getIP(requireActivity())+ URLs.GetProductDetails)
                .addQueryParameter("iProduct",iProduct)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        TaskImage(response);

                        RV_image.setAdapter(imageAdapter);
                        imageAdapter.setOnClickListener(new ImageAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(ImageClass imageClass, int position) {
                                Image_View(imageClass,position);
                            }
                        });
                    }

                    @Override
                    public void onError(ANError anError) {
                        animationDrawable.stop();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void TaskImage(JSONObject response) {
        animationDrawable.stop();
        mProgressBar.setVisibility(View.INVISIBLE);
        try {
            JSONArray jsonArray = new JSONArray(response.getString("images"));

            if(jsonArray.length()>0) {
                emptyIcon.setVisibility(View.GONE);
                image_text.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ImageClass imageClass = new ImageClass(jsonObject.getString("path"));
                    imageClassList.add(imageClass);
                }
            }else {
                //empty icon

                emptyIcon.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void Image_View(ImageClass imageClass, int position) {
        AlertDialog_forAll();
        forward.setVisibility(View.VISIBLE);
        backward.setVisibility(View.VISIBLE);
        image_show.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        frame_layout.setVisibility(View.INVISIBLE);
        Glide.with(view).load(imageClassList.get(position).getImageURL()).into(image_show);
        current_position=position;

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageClassList.size()>0){
                    if(current_position<imageClassList.size()){
                        Glide.with(view).load(imageClassList.get(current_position).getImageURL()).into(image_show);
                        current_position++;
                    }
                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_position>0){
                    current_position--;
                    Glide.with(view).load(imageClassList.get(current_position).getImageURL()).into(image_show);
                }
            }
        });
    }



    private void loadDocument(){
        mProgressBar.setVisibility(View.VISIBLE);
        animationDrawable.start();

        AndroidNetworking.get("http://"+new Tools().getIP(requireActivity())+ URLs.GetProductDetails)
                .addQueryParameter("iProduct",iProduct)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        TaskFiles(response);

                        RV_doc.setAdapter(docAdapter);
                        docAdapter.setOnClickListener(new DocumentClassAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(DocumentClass documentClass, int position) {
                                DocumentView(documentClass,position);
                            }
                        });
                    }

                    @Override
                    public void onError(ANError anError) {
                        animationDrawable.stop();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.d("response",anError.getErrorBody());

                    }
                });

    }

    private void TaskFiles(JSONObject response) {
        animationDrawable.stop();
        mProgressBar.setVisibility(View.INVISIBLE);

        try {
            JSONArray jsonArray=new JSONArray(response.getString("files"));
            if(jsonArray.length()>0) {
                emptyIcon.setVisibility(View.GONE);
                file_text.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DocumentClass documentClass = new DocumentClass(jsonObject.getString("path"),
                            jsonObject.getString("path"));
                    docClassList.add(documentClass);
                }
            }else {
                //empty icon

                emptyIcon.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }



    private void DocumentView(DocumentClass documentClass, int position) {
        AlertDialog_forAll();
        forward.setVisibility(View.GONE);
        backward.setVisibility(View.GONE);
        frame_layout.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mProgressBar_alert.setVisibility(View.VISIBLE);
        animationDrawable_alert.start();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        String docUrl="http://docs.google.com/gview?embedded=true&url=";
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.loadUrl(docUrl+documentClass.getUrl_doc());
        current_position_doc=position;

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(docClassList.size()>0){
                    if(current_position_doc<docClassList.size()){
                        Log.d("current_position_doc",current_position_doc+"");
                        webView.loadUrl(docUrl+docClassList.get(current_position_doc).getUrl_doc());
                        current_position_doc++;
                    }
                }
            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_position_doc>0){
                    current_position_doc--;
                    Log.d("current_position_doc",current_position_doc+"");
                    webView.loadUrl(docUrl+docClassList.get(current_position_doc).getUrl_doc());

                }
            }
        });


    }


    private void AlertDialog_forAll() {
        view=LayoutInflater.from(getContext()).inflate(R.layout.doucument_view,null,false);
        cancel=view.findViewById(R.id.close_doc_alert);
        image_show=view.findViewById(R.id.image_show);
        forward=view.findViewById(R.id.forward);
        backward=view.findViewById(R.id.backward);
        mProgressBar_alert =view.findViewById(R.id.main_progress);


        frame_layout=view.findViewById(R.id.frame_layout);

        webView=view.findViewById(R.id.webView);

        mProgressBar_alert.setBackgroundResource(R.drawable.loading);
        animationDrawable_alert = (AnimationDrawable) mProgressBar_alert.getBackground();

        builder=new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen).setView(view);
        builder.setCancelable(false);
        dialog=builder.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    //image class
    private static class ImageClass {
        String imageURL;
        public ImageClass(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getImageURL() {
            return imageURL;
        }
    }

    private static class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
        List<ImageClass>list;
        Context context;
        private OnClickListener onClickListener;
        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
        public interface OnClickListener {
            void onItemClick(ImageClass imageClass, int position);
        }
        public ImageAdapter(FragmentActivity context, List<ImageClass> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(context).inflate(R.layout.image_adapter,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
            ImageClass imageClass=list.get(position);
            Glide.with(context).load(list.get(position).imageURL).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(imageClass,position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.image);
            }
        }
    }




}