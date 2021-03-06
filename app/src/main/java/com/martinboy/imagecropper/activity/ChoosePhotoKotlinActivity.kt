package com.martinboy.imagecropper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore

import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.adapter.ChoosePhotoKotlinAdapter
import com.martinboy.imagecropper.bean.PhotoBean
import com.martinboy.imagecropper.utils.LogUtils
import com.martinboy.imagecropper.utils.PhotoItemDecoration
import java.util.*

class ChoosePhotoKotlinActivity : AppCompatActivity() {

    private val TAG = ChoosePhotoKotlinActivity::class.java.simpleName

    private var recycler_view_choose_photo: RecyclerView? = null
    private val leftRight = 15
    private val topBottom = 15
    private val spanCount = 3
    private var adapter: ChoosePhotoKotlinAdapter? = null
    private val FINISH_CROP_IMAGE = 103
    private var btn_back: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_photo_page_activity)
        btn_back = findViewById(R.id.btn_back)
        btn_back?.setOnClickListener(View.OnClickListener { onBackPressed() })
        recycler_view_choose_photo = findViewById(R.id.recycler_view_choose_photo)
        recycler_view_choose_photo?.layoutManager = GridLayoutManager(this, spanCount)
        recycler_view_choose_photo?.addItemDecoration(PhotoItemDecoration(leftRight, topBottom))
        GetPhotoTask().execute()
    }

    private fun setList(list: List<PhotoBean>) {
        LogUtils.d(TAG, "PhotoBeanList Size: " + list.size)
        adapter = ChoosePhotoKotlinAdapter(this, list, spanCount, leftRight, topBottom)
        recycler_view_choose_photo?.adapter = adapter
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class GetPhotoTask : AsyncTask<Void, Void, List<PhotoBean>>() {

        var cr = contentResolver
        var projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME)
        var cursor: Cursor? = null

        override fun onPreExecute() {
            super.onPreExecute()

            //查詢圖片
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    arrayOf("image/jpeg", "image/png"),
                    MediaStore.Images.Media.DATE_MODIFIED + " desc")

        }

        override fun doInBackground(vararg voids: Void): List<PhotoBean> {

            val photoBeanList = ArrayList<PhotoBean>()

            if (cursor != null) {
                for (i in 0 until cursor!!.count) {
                    val photoBean = PhotoBean()
                    cursor!!.moveToPosition(i)
                    val filepath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))//抓路徑

                    if (filepath.endsWith(".jpg") || filepath.endsWith(".png")) {

                        val id = cursor!!.getInt(cursor!!
                                .getColumnIndex(MediaStore.Images.Media._ID))// ID
                        photoBean.thumbs = id.toString() + ""

                        photoBean.imagePaths = filepath
                        photoBeanList.add(photoBean)
                    }
                }

                cursor!!.close()
            }

            return photoBeanList
        }

        override fun onPostExecute(list: List<PhotoBean>) {
            super.onPostExecute(list)
            setList(list)
        }
    }

    fun goToCropPicture(uri: Uri) {
        val intent = Intent()
        intent.setClass(this, ImageCropperKotlinActivity::class.java)
        intent.putExtra("imageUrl", uri.toString())
        startActivityForResult(intent, FINISH_CROP_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FINISH_CROP_IMAGE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

}

