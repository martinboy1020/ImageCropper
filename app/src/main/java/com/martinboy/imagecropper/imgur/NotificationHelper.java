package com.martinboy.imagecropper.imgur;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.martinboy.imagecropper.R;
import com.martinboy.imagecropper.bean.ImageResponse;
import com.martinboy.imagecropper.utils.NotificationUtils;

import java.lang.ref.WeakReference;

public class NotificationHelper {
    public final static String TAG = NotificationHelper.class.getSimpleName();

    private WeakReference<Context> mContext;


    public NotificationHelper(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void createUploadingNotification() {

        Notification.Builder mBuilder = new Notification.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_progress));
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = NotificationUtils.getNotificationChannel("imageCropperUploader", "Image Cropper Uploader", "Image Cropper Uploader");

        if(channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channel.getId());
        }

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());

    }

    public void createUploadedNotification(ImageResponse response) {
        Notification.Builder mBuilder = new Notification.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_success));
        mBuilder.setContentText(response.data.link);

        //mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));

        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(mContext.get(), 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.data.link));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(mContext.get(), 0, shareIntent, 0);
        mBuilder.addAction(new Notification.Action(R.drawable.abc_ic_menu_share_mtrl_alpha,
                mContext.get().getString(R.string.notification_share_link), pIntent));

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = NotificationUtils.getNotificationChannel("imageCropperUploader", "Image Cropper Uploader", "Image Cropper Uploader");

        if(channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channel.getId());
        }

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }

    public void createFailedUploadNotification() {
        Notification.Builder mBuilder = new Notification.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_fail));
        //mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = NotificationUtils.getNotificationChannel("imageCropperUploader", "Image Cropper Uploader", "Image Cropper Uploader");

        if(channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channel.getId());
        }

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }
}
