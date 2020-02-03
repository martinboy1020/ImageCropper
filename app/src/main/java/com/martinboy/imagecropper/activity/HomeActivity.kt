package com.martinboy.imagecropper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.adapter.HomeListAdapter
import com.martinboy.imagecropper.bean.ImgurBean
import com.martinboy.imagecropper.bean.PhotoBean
import com.martinboy.imagecropper.custom_ui.SpacesItemDecoration
import com.martinboy.imagecropper.dialog.BottomDialog
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
                    showBottomDialog()
                }
                2 -> {
                    startActivity(Intent().setClass(this, UploadPictureHistoryActivity::class.java))
                }
                else -> {
                    showCleanTempDialog()
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
            if(list.isNotEmpty()) {
                UploadPhotoUtil.deleteTempFile(mAct, list)
            } else {
                Toast.makeText(mAct, mAct.resources.getString(R.string.text_clear_temp_image_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBottomDialog() {

        var intent = Intent()
        intent.setClass(this, UploadCropImageActivity::class.java)
        var bundle = Bundle()

        var bottomDialog : BottomDialog = BottomDialog.instance()
        bottomDialog.setWarningDialog(this, -1,
                resources.getString(R.string.text_please_you_want_to_do),
                "",
                resources.getString(R.string.text_cropped_pic),
                resources.getString(R.string.text_all_pic),
                {
                    bottomDialog.dismiss()
                    bundle.putInt("choose source", 1)
                    intent.putExtras(bundle)
                    startActivity(intent)
                },
                {
                    bottomDialog.dismiss()
                    bundle.putInt("choose source", 2)
                    intent.putExtras(bundle)
                    startActivity(intent)
                })
        bottomDialog.showAllowingStateLoss(supportFragmentManager, BottomDialog.TAG)

    }

    private fun showPreview(uploadImageId : String) {
        val imgDirectUrl = "http://i.imgur.com/$uploadImageId.jpg"
        val sdFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val imgurBean = ImgurBean()
        imgurBean.imgID = uploadImageId
        imgurBean.imgUrl = imgDirectUrl
        imgurBean.uploadDate = sdFormat.format(Date())
        SharePreferenceManager.addUploadImageInHistory(this, imgurBean)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = imgDirectUrl
        } else {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("text label", imgDirectUrl)
            clipboard.primaryClip = clip
        }

        AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.text_upload_image_finish))
                .setMessage(resources.getString(R.string.text_upload_image_copy_url) + " " + imgDirectUrl)
                .setPositiveButton(resources.getString(R.string.text_accept)) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CheckPermissionManager.MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionSuccess = true
                    Toast.makeText(this, resources.getString(R.string.text_permission_pass), Toast.LENGTH_SHORT)
                            .show()
                } else {
                    var no_permissions = ""
                    for(per in permissions) {
                        no_permissions += "\n" + per
                    }

                    Toast.makeText(this, resources.getString(R.string.text_permission_denied), Toast.LENGTH_SHORT)
                            .show()

//                    Toast.makeText(this, "Permission Denied: $no_permissions", Toast.LENGTH_SHORT)
//                            .show()
////                    finish();
                }
            }
        }
    }

    private fun showCleanTempDialog() {
        AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.text_clean_temp_image))
                .setMessage(resources.getString(R.string.text_clean_temp_image_content))
                .setPositiveButton(resources.getString(R.string.text_accept)) { dialog, which ->
                    clearTempImage()
                    dialog.dismiss()
                }
                .setNegativeButton(resources.getString(R.string.text_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
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