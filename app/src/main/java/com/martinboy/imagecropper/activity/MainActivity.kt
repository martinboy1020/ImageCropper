package com.martinboy.imagecropper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.bean.ImgurBean
import com.martinboy.imagecropper.bean.PhotoBean
import com.martinboy.imagecropper.utils.CheckPermissionManager
import com.martinboy.imagecropper.utils.SharePreferenceManager
import com.martinboy.imagecropper.utils.UploadPhotoUtil
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {

    private var mCheckPermissionManager: CheckPermissionManager? = null
    private var checkPermissionSuccess = false

    private val FINISH_UPLOAD_PHOTO = 100

    private var layoutPreview : RelativeLayout? = null
    private var textImgDirectUrl : TextView? = null
    private var imgPreview : ImageView? = null
    private var btnImageCropperJava : Button? = null
    private var btnImageCropperKotlin : Button? = null
    private var btnUploadImage: Button? = null
    private var btnUploadImageHistory: Button? = null
    private var btnCleanTempImage : Button? = null
    private var btnPreviewClose : ImageView? = null
    private var btnCopy : Button? = null
    private val buttonClickListener = View.OnClickListener { view ->

        if(checkPermissionSuccess) {
            clickEvent(view.id)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mCheckPermissionManager == null) {
                    mCheckPermissionManager = CheckPermissionManager(this)
                }

                if (mCheckPermissionManager?.checkPermissions(CheckPermissionManager.permissions)?.isNotEmpty()!!) {
                    mCheckPermissionManager?.requestPermission(this, CheckPermissionManager.permissions)
                } else {
                    clickEvent(view.id)
                }
            } else {
                clickEvent(view.id)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView()
        setOnClick()
    }

    private fun findView() {
        btnImageCropperJava = findViewById(R.id.btn_cropper_java)
        btnImageCropperKotlin = findViewById(R.id.btn_cropper_kotlin)
        btnUploadImage = findViewById(R.id.btn_upload_image)
        btnUploadImageHistory = findViewById(R.id.btn_upload_image_history)
        btnCleanTempImage = findViewById(R.id.btn_clean_temp_image)
        btnPreviewClose = findViewById(R.id.btn_preview_close)
        btnCopy = findViewById(R.id.btn_copy)
        layoutPreview = findViewById(R.id.layout_preview)
        layoutPreview?.visibility = View.GONE
        textImgDirectUrl = findViewById(R.id.text_img_direct_url)
        imgPreview = findViewById(R.id.img_preview)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClick() {
        btnImageCropperJava?.setOnClickListener(buttonClickListener)
        btnImageCropperKotlin?.setOnClickListener(buttonClickListener)
        btnUploadImage?.setOnClickListener(buttonClickListener)
        btnUploadImageHistory?.setOnClickListener(buttonClickListener)
        btnCleanTempImage?.setOnClickListener(buttonClickListener)
        btnPreviewClose?.setOnClickListener(buttonClickListener)
        layoutPreview?.setOnTouchListener { view, motionEvent ->
            // TODO Auto-generated method stub
            true
        }
    }

    private fun showPreview(uploadImageId : String) {
        layoutPreview?.visibility = View.VISIBLE
        val imgDirectUrl = "http://i.imgur.com/$uploadImageId.jpg"
        val sdFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val imgurBean = ImgurBean()
        imgurBean.imgID = uploadImageId
        imgurBean.imgUrl = imgDirectUrl
        imgurBean.uploadDate = sdFormat.format(Date())
        SharePreferenceManager.addUploadImageInHistory(this, imgurBean)

        textImgDirectUrl?.text = imgDirectUrl
        Glide.with(this).load(imgDirectUrl).into(imgPreview)
        btnCopy?.setOnClickListener(View.OnClickListener {
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = imgDirectUrl
            } else {
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip : ClipData = ClipData.newPlainText("text label", imgDirectUrl)
                clipboard.primaryClip = clip
            }
            Toast.makeText(this, "Copy Image Link Success", Toast.LENGTH_SHORT).show()
        })
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

    private fun clickEvent(id : Int?) {
        when(id) {

            R.id.btn_cropper_java -> {
                val intent = Intent(this@MainActivity, ChoosePhotoJavaActivity::class.java)
                startActivityForResult(intent, FINISH_UPLOAD_PHOTO)
            }

            R.id.btn_cropper_kotlin -> {
                val intent = Intent(this@MainActivity, ChoosePhotoKotlinActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_upload_image -> {
                val intent = Intent(this@MainActivity, UploadCropImageActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_upload_image_history -> {
                val intent = Intent(this@MainActivity, UploadPictureHistoryActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_clean_temp_image -> {
                clearTempImage()
            }

            R.id.btn_preview_close -> {
                if(layoutPreview?.visibility == View.VISIBLE) {
                    Glide.clear(imgPreview)
                    layoutPreview?.visibility = View.GONE
                }
            }

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
                showPreview(uploadImageID)
            }
        }
    }

    override fun onBackPressed() {
        if(layoutPreview?.visibility == View.VISIBLE) {
            layoutPreview?.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

//    private fun testGson() {
//        val imgurBean = ImgurBean()
//        imgurBean.imgID = "imagePaths"
//        imgurBean.imgUrl = "thumbs"
//
//        val list = ArrayList<ImgurBean>()
//        list.add(imgurBean)
//        list.add(imgurBean)
//
//        val stringJson : String = GsonUtil.ArrayListToJson(list)
//
//        LogUtils.d("tag1 testConvertGson", stringJson)
//
//        val listType = object : TypeToken<List<ImgurBean>>() { }.type
//        val covertList = GsonUtil.JsonToArrayList(stringJson, listType) as? List<ImgurBean>
//
//        LogUtils.d("tag1 covertList.siz", covertList?.size.toString())
//
//    }

}
