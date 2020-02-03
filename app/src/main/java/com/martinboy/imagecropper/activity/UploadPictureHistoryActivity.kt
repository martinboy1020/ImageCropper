package com.martinboy.imagecropper.activity;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.adapter.UploadPicHistoryAdapter
import com.martinboy.imagecropper.bean.ImgurBean
import com.martinboy.imagecropper.utils.SharePreferenceManager

class UploadPictureHistoryActivity : AppCompatActivity() {

    private var recyclerViewUploadPic : RecyclerView? = null
    private var adapter : UploadPicHistoryAdapter? = null
    private var dataList : List<ImgurBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_pic_history)
        findView()
        init()
    }

    private fun findView() {
        recyclerViewUploadPic = findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadPic?.layoutManager = LinearLayoutManager(this)
        adapter = UploadPicHistoryAdapter(this)
        recyclerViewUploadPic?.adapter = adapter
    }

    private fun init() {
        dataList = SharePreferenceManager.getUploadImageHistoryList(this)
//        LogUtils.d("tag1", "dataList.size: " + dataList?.size)

//        for(b : ImgurBean in (dataList as MutableList<ImgurBean>?)!!) {
//            LogUtils.d("tag1 b.imgUrl", b.imgUrl)
//        }

        if(dataList != null) {
            adapter?.setData(dataList as MutableList<ImgurBean>)
        }
    }

}
