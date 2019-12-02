package com.martinboy.imagecropper.activity

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.cropimage.CropImageView
import com.martinboy.imagecropper.dialog.ProgressDialog
import com.martinboy.imagecropper.utils.UploadPhotoUtil

class ImageCropperKotlinActivity : AppCompatActivity(), CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener, UploadPhotoUtil.UploadHeadPhotoListener {

    private val TAG = ImageCropperJavaActivity::class.java.simpleName

    private var mCropImageView: CropImageView? = null
    private var img_result: ImageView? = null
    private var btn_back: RelativeLayout? = null
    private var btn_finish: TextView? = null
    private var btn_crop_image_rotation: LinearLayout? = null
    private var mProgressDialog: ProgressDialog? = null
    private var onClickListener = View.OnClickListener{view ->
        clickEvent(view.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_photo_page_activity)
        setContentView(R.layout.activity_crop_image)
        findView()
        initCropImageView()
    }

    private fun findView() {
        img_result = findViewById(R.id.img_result)
        mCropImageView = findViewById(R.id.sv_cropImageView)
        btn_crop_image_rotation = findViewById(R.id.sv_btn_crop_image_rotation)
        btn_back = findViewById(R.id.sv_btn_back)
        btn_finish = findViewById(R.id.sv_btn_text_finish)
        btn_crop_image_rotation?.setOnClickListener(onClickListener)
        btn_back?.setOnClickListener(onClickListener)
        btn_finish?.setOnClickListener(onClickListener)
    }

    private fun initCropImageView() {
        img_result?.setOnClickListener {
            img_result?.setImageBitmap(null)
            img_result?.visibility = View.GONE
        }

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("imageUrl")) {
            val imageUri = bundle.getString("imageUrl", "")
            if (imageUri != null && imageUri != "") {
                mCropImageView?.setImageUriAsync(Uri.parse(imageUri))
            }
        }

        mCropImageView?.setOnSetImageUriCompleteListener(this)
        mCropImageView?.setOnCropImageCompleteListener(this)
        mCropImageView?.setFixedAspectRatio(true)
    }

    private fun handleCropResult(result: CropImageView.CropResult?) {
        if (result?.error == null) {
            if (result?.bitmap != null) {
                Toast.makeText(this, "上傳圖片中", Toast.LENGTH_SHORT).show()
                uploadPhoto(result.bitmap)
            } else {
                Toast.makeText(
                        this,
                        "裁剪圖片失敗",
                        Toast.LENGTH_LONG)
                        .show()
            }
        } else {
            Log.e(TAG, "Failed to crop image", result.error)
            Toast.makeText(
                    this,
                    "裁剪圖片失敗",
                    Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun uploadPhoto(bitmap: Bitmap) {

        val cropImgFile = UploadPhotoUtil.saveBitmapAndGetImageFile(bitmap)
        if (cropImgFile != null) {
            UploadPhotoUtil.uploadImageToImgur(this, cropImgFile)
        } else {
            Log.d("tag1", "No Image File")
            mProgressDialog?.dismissAllowingStateLoss()
        }
    }

    private fun clickEvent(id : Int) {
        when(id) {
            R.id.sv_btn_crop_image_rotation -> {
                mCropImageView?.rotateImage(90)
            }

            R.id.sv_btn_back -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            R.id.sv_btn_text_finish -> {
                mProgressDialog = ProgressDialog.instance()
                val ft = this.supportFragmentManager.beginTransaction()
                ft.add(mProgressDialog!!, ProgressDialog.TAG)
                ft.commitAllowingStateLoss()
                mCropImageView?.getCroppedImageAsync()
            }
        }
    }

    override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {
        Log.d(TAG, "onSetImageUriComplete")
        if (error == null) {
            //            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to load image by URI", error)
            Toast.makeText(this, "圖片加載失敗", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        handleCropResult(result)
    }

    override fun uploadImageSuccess(uploadImageID : String) {
        if (mProgressDialog != null) {
            mProgressDialog?.dismissAllowingStateLoss()
        }

//        UploadPhotoUtil.deleteTempFile(this)

    }

    override fun uploadImageFail() {
        if (mProgressDialog != null) {
            mProgressDialog?.dismissAllowingStateLoss()
        }

//        UploadPhotoUtil.deleteTempFile(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

}