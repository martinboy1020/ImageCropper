package com.martinboy.imagecropper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.martinboy.imagecropper.bean.ImgurBean;

import java.util.ArrayList;
import java.util.List;

public class SharePreferenceManager {

    private static String SHARED_PREFERENCES_IMAGE_CROPPER = "SHARED_PREFERENCES_IMAGE_CROPPER";
    private static String UPLOAD_IMAGE_HISTORY = "UPLOAD_IMAGE_HISTORY";

    public static void addUploadImageInHistory(Context context, ImgurBean bean) {
        List<ImgurBean> imgList = getUploadImageHistoryList(context);
        if(imgList != null) {
            imgList.add(bean);
        } else {
            imgList = new ArrayList<>();
            imgList.add(bean);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_IMAGE_CROPPER, Context.MODE_PRIVATE);
        Gson gson = GsonUtil.getCustomGson();
        String json = gson.toJson(imgList);
//        Log.d("tag1 addUploadImageInHistory", "json: " + json);
        sharedPreferences.edit().putString(UPLOAD_IMAGE_HISTORY, json).apply();
    }

    public static List<ImgurBean> getUploadImageHistoryList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_IMAGE_CROPPER, Context.MODE_PRIVATE);
        Gson gson = GsonUtil.getCustomGson();
        String stringJson = sharedPreferences.getString(UPLOAD_IMAGE_HISTORY, "");
//        Log.d("tag1 getUploadImageHistoryList", "stringJson: " + stringJson);
        if (!stringJson.equals("")) {
            return (List<ImgurBean>) GsonUtil.JsonToArrayList(stringJson, new TypeToken<List<ImgurBean>>(){}.getType());
        } else {
            return null;
        }
    }

}
