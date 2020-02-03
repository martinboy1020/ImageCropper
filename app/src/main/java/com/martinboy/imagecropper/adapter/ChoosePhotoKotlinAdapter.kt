package com.martinboy.imagecropper.adapter

import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.activity.ChoosePhotoKotlinActivity
import com.martinboy.imagecropper.bean.PhotoBean
import java.io.File

class ChoosePhotoKotlinAdapter(private val mAct: ChoosePhotoKotlinActivity?, private val list: List<PhotoBean>, spanCount: Int, leftRight: Int, topBottom: Int) : RecyclerView.Adapter<ChoosePhotoKotlinAdapter.ViewHolder>() {
    private val metrics: DisplayMetrics
    private val spanCount: Int = 0
    private val leftRight: Int = 0
    private val topBottom: Int = 0
    private val screenWidth: Int
    private val itemImgWidth: Int
    private val itemImgHeight: Int

    init {
        val manager = mAct?.windowManager
        metrics = DisplayMetrics()
        manager?.defaultDisplay?.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        //獲取單張圖片寬度
        itemImgWidth = (screenWidth - leftRight * (spanCount + 1)) / spanCount
        itemImgHeight = (screenWidth - leftRight * (spanCount + 1)) / spanCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mAct)
                .inflate(R.layout.choose_image_layout, parent, false)
        return ViewHolder(view, mAct, itemImgWidth, itemImgHeight)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val params = holder.img_photo.layoutParams
        params.width = itemImgWidth
        params.height = itemImgHeight
        holder.img_photo.layoutParams = params
        Glide.with(mAct).load(list[holder.adapterPosition].imagePaths).into(holder.img_photo)
        holder.img_photo.setOnClickListener { mAct?.goToCropPicture(Uri.fromFile(File(list[holder.adapterPosition].imagePaths))) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View, var mAct: ChoosePhotoKotlinActivity?, private val itemImgWidth: Int, private val itemImgHeight: Int) : RecyclerView.ViewHolder(itemView) {
        val img_photo: ImageView = itemView.findViewById(R.id.img_choose_photo)
    }
}

