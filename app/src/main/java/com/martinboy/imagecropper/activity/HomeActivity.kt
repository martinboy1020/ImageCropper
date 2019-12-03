package com.martinboy.imagecropper.activity

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
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.adapter.HomeListAdapter
import com.martinboy.imagecropper.bean.ImgurBean
import com.martinboy.imagecropper.bean.PhotoBean
import com.martinboy.imagecropper.custom_ui.SpacesItemDecoration
import com.martinboy.imagecropper.dialog.BottonSheetDialog
import com.martinboy.imagecropper.utils.CheckPermissionManager
import com.martinboy.imagecropper.utils.SharePreferenceManager
import com.martinboy.imagecropper.utils.UploadPhotoUtil
import java.text.SimpleDateFormat
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
                    showBottonDialog()
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

            val photoBeanList = ArrayList<PhotoBean>()

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
                        photoBeanList.add(photoBean)
                    }
                }

                cursor!!.close()
            }

            return photoBeanList
        }

        override fun onPostExecute(list: List<PhotoBean>) {
            super.onPostExecute(list)
            UploadPhotoUtil.deleteTempFile(mAct, list)
        }
    }

    private fun showBottonDialog() {

        var intent = Intent()
        intent.setClass(this, UploadCropImageActivity::class.java)
        var bundle = Bundle()

        var bottonSheetDialog : BottonSheetDialog = BottonSheetDialog.instance()
        bottonSheetDialog.setWarningDialog(this, -1,
                resources.getString(R.string.text_please_you_want_to_do),
                "",
                resources.getString(R.string.text_cropped_pic),
                resources.getString(R.string.text_all_pic),
                {
                    bottonSheetDialog.dismiss()
                    bundle.putInt("choose source", 1)
                    intent.putExtras(bundle)
                    startActivity(intent)
                },
                {
                    bottonSheetDialog.dismiss()
                    bundle.putInt("choose source", 2)
                    intent.putExtras(bundle)
                    startActivity(intent)
                })
        bottonSheetDialog.showAllowingStateLoss(supportFragmentManager, BottonSheetDialog.TAG)

    }

    private fun showPreview(uploadImageId : String) {
//        layoutPreview?.visibility = View.VISIBLE
        val imgDirectUrl = "http://i.imgur.com/$uploadImageId.jpg"
        val sdFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val imgurBean = ImgurBean()
        imgurBean.imgID = uploadImageId
        imgurBean.imgUrl = imgDirectUrl
        imgurBean.uploadDate = sdFormat.format(Date())
        SharePreferenceManager.addUploadImageInHistory(this, imgurBean)

//        textImgDirectUrl?.text = imgDirectUrl
//        Glide.with(this).load(imgDirectUrl).into(imgPreview)
//        btnCopy?.setOnClickListener(View.OnClickListener {
//            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
//                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
//                clipboard.text = imgDirectUrl
//            } else {
//                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clip : ClipData = ClipData.newPlainText("text label", imgDirectUrl)
//                clipboard.primaryClip = clip
//            }
//            Toast.makeText(this, "Copy Image Link Success", Toast.LENGTH_SHORT).show()
//        })
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

//                    Toast.makeText(this, "Permission Denied: $no_permissions", Toast.LENGTH_SHORT)
//                            .show()
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
                showPreview(uploadImageID)
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