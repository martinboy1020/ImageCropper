package com.martinboy.imagecropper.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ImageRepository {
    private ImageDao mImageDao;
    private LiveData<List<ImageEntity>> mLiveUsers;
    private List<ImageEntity> mUsers;

    public ImageRepository(Application application){
        mImageDao = AppDatabase.getInstance(application).getImageDao();
        mLiveUsers = mImageDao.getAllLiveData();
        mUsers = mImageDao.getAll();
    }

    public LiveData<List<ImageEntity>> getLiveImageList() {
        Log.d("tag1", "mLiveUsers.getValue(): " + mLiveUsers.getValue());
        return mLiveUsers;
    }

    public List<ImageEntity> getImageList() {
        mUsers = mImageDao.getAll();
        return mUsers;
    }

    public int getImageUrlCount(){
        return mImageDao.getUserCount();
    }

    public ImageEntity getImageUrlByUid(int uid){
        return mImageDao.getUserByUid(uid);
    }

    public void addImageUrl(ImageEntity... userEntities){
        new AddUserAsyncTask(mImageDao).execute(userEntities);
    }

    public void deleteImageUrl(ImageEntity... userEntities){
        new DeleteImageUrlAsyncTask(mImageDao).execute(userEntities);
    }

    public void deleteImageUrlById(int uID){
        new DeleteImageUrlByUidAsyncTask(mImageDao).execute(uID);
    }

    public void deleteImageUrlByImagerEntity(ImageEntity... userEntities){
        new DeleteImageUrlByImageEntityAsyncTask(mImageDao).execute(userEntities);
    }

    public void updateImageUrls(ImageEntity... userEntities){
        new DeleteImageUrlAsyncTask(mImageDao).execute(userEntities);
    }

    private static class AddUserAsyncTask extends AsyncTask<ImageEntity[], Void, Void>{
        private ImageDao mAsyncImageDao;

        AddUserAsyncTask(ImageDao dao){
            mAsyncImageDao = dao;
        }

        @Override
        protected Void doInBackground(ImageEntity[]... userEntities) {
            mAsyncImageDao.insertUser(userEntities[0]);
            return null;
        }
    }

    private static class DeleteImageUrlAsyncTask extends AsyncTask<ImageEntity[], Void, Void>{
        private ImageDao mAsyncImageDao;

        DeleteImageUrlAsyncTask(ImageDao dao){
            mAsyncImageDao = dao;
        }

        @Override
        protected Void doInBackground(ImageEntity[]... userEntities) {
            mAsyncImageDao.deleteUser(userEntities[0]);
            return null;
        }
    }

    private static class DeleteImageUrlByUidAsyncTask  extends AsyncTask<Integer, Void, Void> {
        private ImageDao mAsyncImageDao;

        DeleteImageUrlByUidAsyncTask(ImageDao dao){
            mAsyncImageDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... memberId) {
            mAsyncImageDao.deleteUser(memberId[0]);
            return null;
        }
    }

    private static class DeleteImageUrlByImageEntityAsyncTask  extends AsyncTask<ImageEntity, Void, Void> {
        private ImageDao mAsyncImageDao;

        DeleteImageUrlByImageEntityAsyncTask(ImageDao dao){
            mAsyncImageDao = dao;
        }

        @Override
        protected Void doInBackground(ImageEntity... userEntities) {
            mAsyncImageDao.deleteUser(userEntities);
            return null;
        }
    }

    private static class UpdateImageUrlAsyncTask extends AsyncTask<ImageEntity[], Void, Void>{
        private ImageDao mAsyncImageDao;

        UpdateImageUrlAsyncTask(ImageDao dao){
            mAsyncImageDao = dao;
        }

        @Override
        protected Void doInBackground(ImageEntity[]... userEntities) {
            mAsyncImageDao.updateUser(userEntities[0]);
            return null;
        }
    }
}