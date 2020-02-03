package com.martinboy.imagecropper.database;

import android.content.Context;

public class DataBaseManager {

    private final String TAG = DataBaseManager.class.getSimpleName();

    public static final String IMAGE_TABLE = "image_table";

    private ImageDao getImageDao;
    private Context context;

    private static DataBaseManager INSTANCE;

    private DataBaseManager(Context context){
        this.context = context;
        this.getImageDao = AppDatabase.getInstance(context).getImageDao();
    }


    public static DataBaseManager getManager(Context context){

        if(INSTANCE == null){
            INSTANCE = new DataBaseManager(context);
        }

        return INSTANCE;
    }

    public void insertImage(String imageUrl) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageUrl(imageUrl);
        getImageDao.insertUser(imageEntity);
    }



}
