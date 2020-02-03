package com.martinboy.imagecropper.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.activity.UploadPictureHistoryActivity
import com.martinboy.imagecropper.bean.ImgurBean

class UploadPicHistoryAdapter(private val mAct: UploadPictureHistoryActivity?)
    : RecyclerView.Adapter<UploadPicHistoryAdapter.ViewHolder>() {

    private var mList : List<ImgurBean>? = null

    fun setData(list : List<ImgurBean>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mAct)
                .inflate(R.layout.upload_img_histroy_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mAct).load(mList?.get(holder.adapterPosition)?.imgUrl).into(holder.img_photo)
        holder.text_img_url.text = mList?.get(holder.adapterPosition)?.imgUrl
        holder.text_upload_date.text = mList?.get(holder.adapterPosition)?.uploadDate
        holder.btn_copy_url.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = mAct?.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = mList?.get(holder.adapterPosition)?.imgUrl
            } else {
                val clipboard: ClipboardManager = mAct?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("text label", mList?.get(holder.adapterPosition)?.imgUrl)
                clipboard.primaryClip = clip
            }
            Toast.makeText(mAct, mAct.resources.getString(R.string.text_copy_url_success), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return if(mList != null) {
            mList?.size!!
        } else {
            0
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_photo : ImageView = itemView.findViewById(R.id.image_preview)
        val text_img_url : TextView = itemView.findViewById(R.id.text_img_url)
        val text_upload_date : TextView = itemView.findViewById(R.id.text_upload_date)
        val btn_copy_url : TextView = itemView.findViewById(R.id.btn_copy_url)
    }
}

