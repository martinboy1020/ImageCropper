package com.martinboy.imagecropper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.martinboy.imagecropper.R;
import com.martinboy.imagecropper.activity.ImageCropperJavaActivity;
import com.martinboy.imagecropper.activity.ImageCropperKotlinActivity;
import com.martinboy.imagecropper.bean.PhotoBean;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadPhotoUtil {

    private static final String TAG = UploadPhotoUtil.class.getSimpleName();
    private static String tmpImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
//    private static String tmpImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/ImageCropper/temp.jpg";

    public static String saveBitmapAndGetImageUrl(Bitmap bitmap) {
        FileOutputStream fOut;
        String tmpImagePath = tmpImageDir + getTempImageName();
        try {
            File dir = new File(tmpImageDir, "ImageCropper");
            if (!dir.exists()) {
                dir.mkdir();
            }
            fOut = new FileOutputStream(tmpImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
                if (checkHeadPhotoFileIsExist(tmpImagePath)) {
                    File file = new File(tmpImagePath);
                    return file.getPath();
                } else {
                    return "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static File saveBitmapAndGetImageFile(Bitmap bitmap) {
        FileOutputStream fOut;
        String tmpImagePath = tmpImageDir + getTempImageName();
        try {
            File dir = new File(tmpImageDir, "ImageCropper");
            if (!dir.exists()) {
                dir.mkdir();
            }
            fOut = new FileOutputStream(tmpImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
                if (checkHeadPhotoFileIsExist(tmpImagePath)) {
                    return new File(tmpImagePath);
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean checkHeadPhotoFileIsExist(String tmpImagePath) {
        File file = new File(tmpImagePath);
        return file.exists();
    }

    public static void deleteTempFile(final Context context, List<PhotoBean> list) {
        File dir = new File(tmpImageDir + "/ImageCropper");
        if (dir.exists()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0 ; i < list.size(); i++) {
            File img = new File(list.get(i).getImagePaths());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                String[] paths = new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()};
                MediaScannerConnection.scanFile(context, new String[]{
                                img.getAbsolutePath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                if(i == list.size() - 1) Toast.makeText(context, context.getResources().getString(R.string.text_clear_temp_image_success), Toast.LENGTH_SHORT).show();
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                if(i == list.size() - 1) Toast.makeText(context, context.getResources().getString(R.string.text_clear_temp_image_success), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static String getTempImageName() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return "/ImageCropper/cropImage_" + sdFormat.format(new Date()) + ".jpg";
    }

    public static void uploadImageToImgur(ImageCropperJavaActivity mAct, File chooseFile) {
        UploadImageManager mUploadImageManager = UploadImageManager.getInstance();
        mUploadImageManager.onUpload(mAct, chooseFile, "", "", "", "", mAct);
    }

    public static void uploadImageToImgur(ImageCropperKotlinActivity mAct, File chooseFile) {
        UploadImageManager mUploadImageManager = UploadImageManager.getInstance();
        mUploadImageManager.onUpload(mAct, chooseFile, "", "", "", "", mAct);
    }

    public static void refreshImageDataBase(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
            MediaScannerConnection.scanFile(context, new String[]{
                            file.getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public interface UploadHeadPhotoListener {
        void uploadImageSuccess(String uploadImageID);
        void uploadImageFail();
    }

}
