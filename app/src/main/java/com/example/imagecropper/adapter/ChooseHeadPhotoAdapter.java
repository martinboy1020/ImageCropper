package com.example.imagecropper.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.imagecropper.R;
import com.example.imagecropper.activity.ChoosePhotoJavaActivity;
import com.example.imagecropper.bean.HeadPhotoBean;

import java.io.File;
import java.util.List;

public class ChooseHeadPhotoAdapter extends RecyclerView.Adapter<ChooseHeadPhotoAdapter.ViewHolder> {

    private ChoosePhotoJavaActivity mAct;
    private DisplayMetrics metrics;
    private int spanCount, leftRight, topBottom;
    private List<HeadPhotoBean> list;
    private int screenWidth, itemImgWidth, itemImgHeight;

    public ChooseHeadPhotoAdapter(ChoosePhotoJavaActivity mAct, List<HeadPhotoBean> list, int spanCount, int leftRight, int topBottom) {
        this.mAct = mAct;
        this.list = list;
        WindowManager manager = mAct.getWindowManager();
        metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        //獲取單張圖片寬度
        itemImgWidth = (screenWidth - leftRight * (spanCount + 1)) / spanCount;
        itemImgHeight = (screenWidth - leftRight * (spanCount + 1)) / spanCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mAct)
                .inflate(R.layout.choose_image_layout, parent, false);
        return new ChooseHeadPhotoAdapter.ViewHolder(view, mAct, itemImgWidth, itemImgHeight);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ViewGroup.LayoutParams params = holder.img_photo.getLayoutParams();
        params.width = itemImgWidth;
        params.height = itemImgHeight;
        holder.img_photo.setLayoutParams(params);
        Glide.with(mAct).load(list.get(holder.getAdapterPosition()).getImagePaths()).into(holder.img_photo);
        holder.img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAct.goToCropPicture(Uri.fromFile(new File(list.get(holder.getAdapterPosition()).getImagePaths())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ChoosePhotoJavaActivity mAct;
        private ImageView img_photo;
        private int itemImgWidth, itemImgHeight;

        ViewHolder(View itemView, ChoosePhotoJavaActivity mAct, int itemImgWidth, int itemImgHeight) {
            super(itemView);
            this.mAct = mAct;
            this.itemImgWidth = itemImgWidth;
            this.itemImgHeight = itemImgHeight;
            img_photo = itemView.findViewById(R.id.img_choose_photo);
        }
    }
}