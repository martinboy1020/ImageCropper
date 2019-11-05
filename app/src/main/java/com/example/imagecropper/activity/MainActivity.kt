package com.example.imagecropper.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.imagecropper.R
import com.example.imagecropper.utils.CheckPermissionManager

class MainActivity : AppCompatActivity() {

    private var mCheckPermissionManager: CheckPermissionManager? = null
    private var checkPermissionSuccess = false

    private var btnImageCropperJava : Button? = null
    private var btnImageCropperKotlin : Button? = null
    private val buttonClickListener = View.OnClickListener { view ->

        if(checkPermissionSuccess) {
            setGoToPage(view.id)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mCheckPermissionManager == null) {
                    mCheckPermissionManager = CheckPermissionManager(this)
                }

                if (mCheckPermissionManager?.checkPermissions(CheckPermissionManager.permissions)?.isNotEmpty()!!) {
                    mCheckPermissionManager?.requestPermission(this, CheckPermissionManager.permissions)
                } else {
                    setGoToPage(view.id)
                }
            } else {
                setGoToPage(view.id)
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
    }

    private fun setOnClick() {
        btnImageCropperJava?.setOnClickListener(buttonClickListener)
        btnImageCropperKotlin?.setOnClickListener(buttonClickListener)
    }

    private fun setGoToPage(id : Int?) {
        when(id) {

            R.id.btn_cropper_java -> {
                val intent = Intent(this@MainActivity, ChoosePhotoJavaActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_cropper_kotlin -> {
                val intent = Intent(this@MainActivity, ImageCropperKotlinActivity::class.java)
                startActivity(intent)
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

}
