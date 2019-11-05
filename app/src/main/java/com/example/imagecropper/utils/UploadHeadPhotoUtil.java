package com.example.imagecropper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.example.imagecropper.activity.ImageCropperJavaActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadHeadPhotoUtil {

    private static final String TAG = UploadHeadPhotoUtil.class.getSimpleName();
    private static String tmpImageDir = Environment.getExternalStorageDirectory().getPath();
    private static String tmpImagePath = Environment.getExternalStorageDirectory().getPath() + "/imageCroper/temp.jpg";

    public static String saveBitmapAndGetImageUrl(Bitmap bitmap) {
        FileOutputStream fOut;
        try {
            File dir = new File(tmpImageDir, "imageCroper");
            if (!dir.exists()) {
                dir.mkdir();
            }
            fOut = new FileOutputStream(tmpImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
                if (checkHeadPhotoFileIsExist()) {
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
        try {
            File dir = new File(tmpImageDir, "imageCroper");
            if (!dir.exists()) {
                dir.mkdir();
            }
            fOut = new FileOutputStream(tmpImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
                if (checkHeadPhotoFileIsExist()) {
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

    private static boolean checkHeadPhotoFileIsExist() {
        File file = new File(tmpImagePath);
        return file.exists();
    }

    public static void deleteTempFile(Context context) {
        File dir = new File(tmpImageDir + "/imageCroper");
        File img = new File(tmpImagePath);
        if (dir.exists()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
            MediaScannerConnection.scanFile(context, new String[]{
                            img.getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public static void uploadImageToImgur(ImageCropperJavaActivity mAct, File chooseFile) {
        UploadImageManager mUploadImageManager = UploadImageManager.getInstance();
        mUploadImageManager.onUpload(mAct, chooseFile, "testName", "testDes", "", "", mAct);
    }

    public interface UploadHeadPhotoListener {
        void uploadImageSuccess();
        void uploadImageFail();
    }

}
