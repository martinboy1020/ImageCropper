package com.martinboy.imagecropper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.martinboy.imagecropper.R;
import com.martinboy.imagecropper.cropimage.CropImageView;
import com.martinboy.imagecropper.dialog.ProgressDialog;
import com.martinboy.imagecropper.utils.CheckConnectStatusManager;
import com.martinboy.imagecropper.utils.UploadPhotoUtil;

import java.io.File;

public class ImageCropperJavaActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, UploadPhotoUtil.UploadHeadPhotoListener,
        CropImageView.OnCropImageCompleteListener {

    private static final String TAG = ImageCropperJavaActivity.class.getSimpleName();

    CropImageView mCropImageView;
    ImageView img_result, image_crop_shape, image_aspect_ratio;
    RelativeLayout btn_back;
    TextView btn_finish, text_crop_shape, text_aspect_ratio;
    LinearLayout btn_crop_image_rotation, btn_crop_shape, btn_aspect_Ratio;
    ProgressDialog mProgressDialog;
    boolean isFixedAspectRatio = true;
    boolean isRect = true;

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
        image_crop_shape = findViewById(R.id.image_crop_shape);
        image_aspect_ratio = findViewById(R.id.image_aspect_ratio);
        btn_crop_image_rotation = findViewById(R.id.sv_btn_crop_image_rotation);
        btn_crop_shape = findViewById(R.id.sv_btn_crop_shape);
        btn_aspect_Ratio = findViewById(R.id.sv_btn_aspect_ratio);
        btn_back = findViewById(R.id.sv_btn_back);
        btn_finish = findViewById(R.id.sv_btn_text_finish);
        text_crop_shape = findViewById(R.id.text_crop_shape);
        text_aspect_ratio = findViewById(R.id.text_aspect_ratio);
        btn_crop_image_rotation.setOnClickListener(onClickListener);
        btn_crop_shape.setOnClickListener(onClickListener);
        btn_aspect_Ratio.setOnClickListener(onClickListener);
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
        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        // 裁切範圍是否為等比例正方
        mCropImageView.setFixedAspectRatio(true);
        isFixedAspectRatio = true;
        isRect = true;
        mCropImageView.setOnCropImageCompleteListener(this);
        image_crop_shape.setBackgroundResource(R.drawable.ic_crop_square);
        text_crop_shape.setText(getResources().getString(R.string.text_crop_rect_shape));
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        Log.d(TAG, "onSetImageUriComplete");
        if (error == null) {
//            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to load image by URI", error);
            Toast.makeText(this, getResources().getString(R.string.text_load_image_fail), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    private void uploadPhoto(Bitmap bitmap) {

        File cropImgFile = UploadPhotoUtil.saveBitmapAndGetImageFile(bitmap);

        if(cropImgFile != null) {
            UploadPhotoUtil.refreshImageDataBase(this, cropImgFile);
        }

        if(CheckConnectStatusManager.checkNetWorkConnect(this)) {

            if (cropImgFile != null) {
                Toast.makeText(this, getResources().getString(R.string.text_during_upload_picture), Toast.LENGTH_SHORT).show();
                showProgressDialog();
                UploadPhotoUtil.uploadImageToImgur(this, cropImgFile);
            } else {
                Log.d("tag1", "No Image File");
                Toast.makeText(this, getResources().getString(R.string.text_file_error), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, getResources().getString(R.string.text_internet_error_upload), Toast.LENGTH_SHORT).show();
        }

    }

    private void checkUploadDialog(final Bitmap bitmap) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.text_crop_image_finish))
                .setMessage(getResources().getString(R.string.text_crop_image_finish_is_upload_or_not))
                .setPositiveButton(getResources().getString(R.string.text_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPhoto(bitmap);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = UploadPhotoUtil.saveBitmapAndGetImageFile(bitmap);
                        UploadPhotoUtil.refreshImageDataBase(ImageCropperJavaActivity.this, file);
                        onBackPressed();
                    }
                })
                .show();
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
                showProgressDialog();
                mCropImageView.getCroppedImageAsync();
            } else if (v.getId() == R.id.sv_btn_crop_shape) {

                if(isRect) {
                    isRect = false;
                    image_crop_shape.setBackgroundResource(R.drawable.ic_circle);
                    text_crop_shape.setText(getResources().getString(R.string.text_crop_oval_shape));
                    mCropImageView.setCropShape(CropImageView.CropShape.OVAL);
                } else {
                    isRect = true;
                    image_crop_shape.setBackgroundResource(R.drawable.ic_crop_square);
                    text_crop_shape.setText(getResources().getString(R.string.text_crop_rect_shape));
                    mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
                }

            } else if (v.getId() == R.id.sv_btn_aspect_ratio) {

                if(isFixedAspectRatio) {
                    isFixedAspectRatio = false;
                    text_aspect_ratio.setText(getResources().getString(R.string.text_no_fixed_aspect_ratio));
                    mCropImageView.clearAspectRatio();
                } else {
                    isFixedAspectRatio = true;
                    text_aspect_ratio.setText(getResources().getString(R.string.text_fixed_aspect_ratio));
                    mCropImageView.setFixedAspectRatio(true);
                }

            }
        }
    };

    private void handleCropResult(CropImageView.CropResult result) {

        dismissProgressDialog();

        if (result.getError() == null) {
            if (result.getBitmap() != null) {
                checkUploadDialog(result.getBitmap());
            } else {
                Toast.makeText(
                        this,
                        this.getResources().getString(R.string.text_crop_image_fail),
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Log.e(TAG, "Failed to crop image", result.getError());
            Toast.makeText(
                    this,
                    this.getResources().getString(R.string.text_crop_image_fail),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.instance();
                FragmentTransaction ft = ImageCropperJavaActivity.this.getSupportFragmentManager().beginTransaction();
                ft.add(mProgressDialog, ProgressDialog.TAG);
                ft.commitAllowingStateLoss();
            }
        });
    }

    private void dismissProgressDialog() {
        if(mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void uploadImageSuccess(String uploadImageID) {
        dismissProgressDialog();
        setResult(RESULT_OK, new Intent().putExtra("uploadImageID", uploadImageID));
        finish();
    }

    @Override
    public void uploadImageFail() {
        dismissProgressDialog();
    }
}