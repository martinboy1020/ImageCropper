package com.example.imagecropper.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagecropper.R;
import com.example.imagecropper.cropimage.CropImageView;
import com.example.imagecropper.dialog.ProgressDialog;
import com.example.imagecropper.utils.UploadHeadPhotoUtil;

import java.io.File;

public class ImageCropperJavaActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, UploadHeadPhotoUtil.UploadHeadPhotoListener,
        CropImageView.OnCropImageCompleteListener {

    private static final String TAG = ImageCropperJavaActivity.class.getSimpleName();

    CropImageView mCropImageView;
    ImageView img_result;
    RelativeLayout btn_back;
    TextView btn_finish;
    LinearLayout btn_crop_image_rotation;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        findView();
        initCropImageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findView() {
        img_result = findViewById(R.id.img_result);
        mCropImageView = findViewById(R.id.sv_cropImageView);
        btn_crop_image_rotation = findViewById(R.id.sv_btn_crop_image_rotation);
        btn_back = findViewById(R.id.sv_btn_back);
        btn_finish = findViewById(R.id.sv_btn_text_finish);
        btn_crop_image_rotation.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_finish.setOnClickListener(onClickListener);
    }

    private void initCropImageView() {
        img_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_result.setImageBitmap(null);
                img_result.setVisibility(View.GONE);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("imageUrl")) {
            String imageUri = bundle.getString("imageUrl", "");
            if (imageUri != null && !imageUri.equals("")) {
                mCropImageView.setImageUriAsync(Uri.parse(imageUri));
            }
        }

        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        mCropImageView.setFixedAspectRatio(true);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        Log.d(TAG, "onSetImageUriComplete");
        if (error == null) {
//            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to load image by URI", error);
            Toast.makeText(this, "圖片加載失敗", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    private void uploadPhoto(Bitmap bitmap) {

        File cropImgFile = UploadHeadPhotoUtil.saveBitmapAndGetImageFile(bitmap);
        if (cropImgFile != null) {
            UploadHeadPhotoUtil.uploadImageToImgur(this, cropImgFile);
        } else {
            Log.d("tag1", "No Image File");
            if(mProgressDialog != null) {
                mProgressDialog.dismissAllowingStateLoss();
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sv_btn_crop_image_rotation) {
                mCropImageView.rotateImage(90);
            } else if (v.getId() == R.id.sv_btn_back) {
                setResult(RESULT_CANCELED);
                finish();
            } else if (v.getId() == R.id.sv_btn_text_finish) {
                mProgressDialog = ProgressDialog.instance();
                FragmentTransaction ft = ImageCropperJavaActivity.this.getSupportFragmentManager().beginTransaction();
                ft.add(mProgressDialog, ProgressDialog.TAG);
                ft.commitAllowingStateLoss();
                mCropImageView.getCroppedImageAsync();
            }
        }
    };

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            if (result.getBitmap() != null) {
                Toast.makeText(this, "上傳圖片中", Toast.LENGTH_SHORT).show();
                uploadPhoto(result.getBitmap());
            } else {
                Toast.makeText(
                        this,
                        "裁剪圖片失敗",
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Log.e(TAG, "Failed to crop image", result.getError());
            Toast.makeText(
                    this,
                    "裁剪圖片失敗",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void uploadImageSuccess() {
        if(mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
        }
        UploadHeadPhotoUtil.deleteTempFile(this);
    }

    @Override
    public void uploadImageFail() {
        if(mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
        }
        UploadHeadPhotoUtil.deleteTempFile(this);
    }
}