package com.martinboy.imagecropper.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CheckPermissionManager {

    private static final String TAG = CheckPermissionManager.class.getSimpleName();
    public static final int MULTIPLE_PERMISSIONS = 10;
    public static final int SETTING_PERMISSIONS = 11;

    private Context mContext;

    public static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public CheckPermissionManager(Context context) {
        this.mContext = context;
    }

    public List<String> checkPermissions(String[] permissionList) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionList) {
            result = ContextCompat.checkSelfPermission(mContext, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        return listPermissionsNeeded;
    }

    public void requestPermission(Activity mAct, String[] permissionList) {
        ActivityCompat.requestPermissions(mAct, permissionList, CheckPermissionManager.MULTIPLE_PERMISSIONS);
    }

    public void requestPermissionForFragment(Fragment fragment, String[] permissionList) {
        fragment.requestPermissions(permissionList, CheckPermissionManager.MULTIPLE_PERMISSIONS);
    }

    public boolean checkRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean permissionGrant = true;

        if (grantResults.length > 0 && permissions.length > 0 && grantResults.length == permissions.length) {

            for (int i = 0; i < permissions.length; i++) {
                Log.e(TAG, "permissions: " + permissions[i] + " grantResults: " + grantResults[i]);
            }

            for (int grantResultCode : grantResults) {
                if (grantResultCode == PackageManager.PERMISSION_DENIED) {
                    permissionGrant = false;
                    break;
                }
            }
        }

        return permissionGrant;

    }

}
