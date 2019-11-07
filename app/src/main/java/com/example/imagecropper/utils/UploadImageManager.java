package com.example.imagecropper.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.imagecropper.imgur.ImageResponse;
import com.example.imagecropper.imgur.ImgurService;
import com.example.imagecropper.imgur.NotificationHelper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageManager {

    private static UploadImageManager mUploadImageManager;
    private File chosenFile;
    private static String clientID = "7c86e16b04d6c1a";
    private static String clientSecret = "29aa8e8d4820ce430787f21689de1561e5975ad1";
    private static String accessToken = "3141289fb8f8baf563825012f22faa26ece5e08e";

    public static synchronized UploadImageManager getInstance() {
        if (mUploadImageManager == null) {
            mUploadImageManager = new UploadImageManager();
        }
        return mUploadImageManager;
    }

    public UploadImageManager() {

    }

    public void onUpload(final Context context,
                         File chosenFile,
                         String imageName,
                         String imageDescription,
                         String albumId,
                         String userName,
                         final UploadPhotoUtil.UploadHeadPhotoListener mUploadHeadPhotoListener) {

        if (chosenFile == null) {
            Toast.makeText(context, "Choose a file before upload.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        final NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createUploadingNotification();

        ImgurService imgurService = ImgurService.retrofit.create(ImgurService.class);


        final Call<ImageResponse> call =
                imgurService.postImage(
                        imageName,
                        imageDescription, albumId, userName,
                        MultipartBody.Part.createFormData(
                                "image",
                                chosenFile.getName(),
                                RequestBody.create(MediaType.parse("image/*"), chosenFile)
                        ));

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                if (response == null) {
                    notificationHelper.createFailedUploadNotification();
                    mUploadHeadPhotoListener.uploadImageFail();
                    return;
                }
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Upload successful !", Toast.LENGTH_SHORT)
                            .show();
                    if (response.body() != null) {
                        Log.d("URL Picture", "http://imgur.com/" + response.body().data.id);
                        notificationHelper.createUploadedNotification(response.body());
                        mUploadHeadPhotoListener.uploadImageSuccess(response.body().data.id);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(context, "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
                notificationHelper.createFailedUploadNotification();
                mUploadHeadPhotoListener.uploadImageFail();
                t.printStackTrace();
            }
        });

    }


}
