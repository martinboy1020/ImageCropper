package com.martinboy.imagecropper.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.martinboy.imagecropper.R
import com.martinboy.imagecropper.fragment.CropImageGalleryFragment

class UploadCropImageActivity : AppCompatActivity() {

    private val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_crop_image)
        val bundle = this.intent.extras
        firstSwitchToFragment(CropImageGalleryFragment::class.java, bundle, false)
    }

    private fun firstSwitchToFragment(nextFragmentClass: Class<out Fragment>, args: Bundle?, needStack: Boolean?) {

        val trans = manager.beginTransaction()
        val frag: Fragment = nextFragmentClass.newInstance()
        val tag: String = nextFragmentClass.simpleName

//        LogUtils.d("tag1", "trans $trans")
//        LogUtils.d("tag1", "frag $frag")
//        LogUtils.d("tag1", "tag $tag")
//        LogUtils.d("tag1", "needStack $needStack")

        trans.setCustomAnimations(R.anim.poc_fade_entry_from_right, R.anim.poc_fade_exit_from_left,
                R.anim.poc_fade_entry_from_left, R.anim.poc_fade_exit_from_right)

        trans.replace(R.id.layout_main, frag, tag)

        when {
            args != null -> frag.arguments = args
        }

        when {
            needStack != null && needStack -> trans.addToBackStack(tag)
        }

        trans.commitAllowingStateLoss()
    }

    fun switchToFragment(nextFragmentClass: Class<out Fragment>, args: Bundle?, needStack: Boolean?) {

        val trans = manager.beginTransaction()
        val frag: Fragment = nextFragmentClass.newInstance()
        val tag: String = nextFragmentClass.simpleName

//        LogUtils.d("tag1", "trans $trans")
//        LogUtils.d("tag1", "frag $frag")
//        LogUtils.d("tag1", "tag $tag")
//        LogUtils.d("tag1", "args $args")
//        LogUtils.d("tag1", "needStack $needStack")

        trans.setCustomAnimations(R.anim.poc_fade_entry_from_right, R.anim.poc_fade_exit_from_left,
                R.anim.poc_fade_entry_from_left, R.anim.poc_fade_exit_from_right)

        val ls : List<Fragment> = manager.fragments
        for(i in ls) {
            trans.hide(i)
        }

        if(frag.isAdded) {
            trans.show(frag)
        } else {
            trans.add(R.id.layout_main, frag, tag)
        }

        when {
            args != null -> frag.arguments = args
        }

        when {
            needStack != null && needStack -> trans.addToBackStack(tag)
        }

        trans.commitAllowingStateLoss()

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}