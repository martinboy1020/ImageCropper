package com.example.imagecropper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.imagecropper.R
import com.example.imagecropper.adapter.HomeListAdapter
import com.example.imagecropper.bean.PhotoBean
import com.example.imagecropper.custom_ui.SpacesItemDecoration
import com.example.imagecropper.utils.CheckPermissionManager
import com.example.imagecropper.utils.UploadPhotoUtil
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var recycleViewHome : RecyclerView? = null
    private var homeListAdapter : HomeListAdapter? = null
    private var spacesItemDecoration : SpacesItemDecoration? = null
    private var mCheckPermissionManager: CheckPermissionManager? = null
    private var checkPermissionSuccess = false
    private val FINISH_UPLOAD_PHOTO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findView()
        mCheckPermissionManager = CheckPermissionManager(this)
        homeListAdapter = HomeListAdapter(this)
        spacesItemDecoration = SpacesItemDecoration(30)
        recycleViewHome?.addItemDecoration(spacesItemDecoration!!)
        recycleViewHome?.adapter = homeListAdapter
    }

    private fun findView() {
        recycleViewHome = findViewById(R.id.recycler_view_home)
        recycleViewHome?.layoutManager = LinearLayoutManager(this)
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCheckPermissionManager == null) {
                mCheckPermissionManager = CheckPermissionManager(this)
            }
            !mCheckPermissionManager?.checkPermissions(CheckPermissionManager.permissions)?.isNotEmpty()!!
        } else {
            true
        }
    }

    fun goToOtherPage(position : Int) {
        if(checkPermission()) {
            when (position) {
                0 -> {
                    startActivityForResult(Intent().setClass(this, ChoosePhotoJavaActivity::class.java), FINISH_UPLOAD_PHOTO)
                }
                1 -> {
                    startActivity(Intent().setClass(this, UploadCropImageActivity::class.java))
                }
                2 -> {
                    startActivity(Intent().setClass(this, UploadPictureHistoryActivity::class.java))
                }
                else -> {
                    clearTempImage()
                }
            }
        } else {
            mCheckPermissionManager?.requestPermission(this, CheckPermissionManager.permissions)
        }
    }

    private fun clearTempImage() {
        GetPhotoTask(this).execute()
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class GetPhotoTask(act: Activity) : AsyncTask<Void, Void, List<PhotoBean>>() {
        var mAct = act
        var cr: ContentResolver
        var projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME)
        var cursor: Cursor? = null

        init {
            cr = mAct.contentResolver
        }

        override fun onPreExecute() {
            super.onPreExecute()

            //查詢圖片
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.DATA + " like ? ",
                    arrayOf("%ImageCropper%"),
                    MediaStore.Images.Media.DATE_MODIFIED + " desc")

        }

        override fun doInBackground(vararg voids: Void): List<PhotoBean> {

            val PhotoBeanList = ArrayList<PhotoBean>()

            if (cursor != null) {
                for (i in 0 until cursor!!.count) {
                    cursor!!.moveToPosition(i)
                    val filepath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))
                    //只找出副檔名為.jpg或.png的檔案
                    if (filepath.endsWith(".jpg") || filepath.endsWith(".png")) {
                        val photoBean = PhotoBean()
                        val id = cursor!!.getInt(cursor!!
                                .getColumnIndex(MediaStore.Images.Media._ID))// ID
                        photoBean.thumbs = id.toString() + ""
                        photoBean.imagePaths = filepath
                        PhotoBeanList.add(photoBean)
                    }
                }

                cursor!!.close()
            }

            return PhotoBeanList
        }

        override fun onPostExecute(list: List<PhotoBean>) {
            super.onPostExecute(list)
            UploadPhotoUtil.deleteTempFile(mAct, list)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CheckPermissionManager.MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionSuccess = true
                    Toast.makeText(this, "Permission Pass, Please Click Again", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    var no_permissions = ""
                    for(per in permissions) {
                        no_permissions += "\n" + per
                    }

                    Toast.makeText(this, "Permission Denied: $no_permissions", Toast.LENGTH_SHORT)
                            .show()
////                    finish();
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FINISH_UPLOAD_PHOTO && resultCode == Activity.RESULT_OK) {
            val uploadImageID : String = data?.getStringExtra("uploadImageID") ?: ""
            if(uploadImageID != "") {
//                showPreview(uploadImageID)
            }
        }
    }

    override fun onBackPressed() {
//        if(layoutPreview?.visibility == View.VISIBLE) {
//            layoutPreview?.visibility = View.GONE
//        } else {
            super.onBackPressed()
//        }
    }

}