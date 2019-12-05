package com.martinboy.imagecropper.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.martinboy.imagecropper.R;
import com.martinboy.imagecropper.adapter.ChooseCropPhotoJavaAdapter;
import com.martinboy.imagecropper.bean.ImgurBean;
import com.martinboy.imagecropper.bean.PhotoBean;
import com.martinboy.imagecropper.dialog.ProgressDialog;
import com.martinboy.imagecropper.utils.CheckConnectStatusManager;
import com.martinboy.imagecropper.utils.PhotoItemDecoration;
import com.martinboy.imagecropper.utils.SharePreferenceManager;
import com.martinboy.imagecropper.utils.UploadPhotoUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CropImageGalleryFragment extends Fragment implements UploadPhotoUtil.UploadHeadPhotoListener {

    private static final String TAG = CropImageGalleryFragment.class.getSimpleName();

    private RecyclerView recycler_view_choose_photo;
    private int leftRight = 15;
    private int topBottom = 15;
    private int spanCount = 3;
    private ChooseCropPhotoJavaAdapter adapter;
    private static int FINISH_CROP_IMAGE = 103;
    private RelativeLayout btn_back;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_photo_page_activity, container, false);
        findView(rootView, this.getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(this.getActivity() != null) {

            int sourceFrom = 0;
            Bundle bundle = getArguments();
            if(bundle != null) {
                sourceFrom = bundle.getInt("choose source", 0);
            }

            new GetPhotoTask(this.getActivity(), sourceFrom).execute();
        }
    }

    private void findView(View view, final Activity mAct) {
        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAct.onBackPressed();
            }
        });
        recycler_view_choose_photo = view.findViewById(R.id.recycler_view_choose_photo);
        recycler_view_choose_photo.setLayoutManager(new GridLayoutManager(mAct, spanCount));
        recycler_view_choose_photo.addItemDecoration(new PhotoItemDecoration(leftRight, topBottom));
    }

    private void setList(List<PhotoBean> list) {
        Log.d(TAG, "PhotoBeanList Size: " + list.size());
        adapter = new ChooseCropPhotoJavaAdapter(this, list, spanCount, leftRight, topBottom);
        recycler_view_choose_photo.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    class GetPhotoTask extends AsyncTask<Void, Void, List<PhotoBean>> {

        Activity mAct;
        int mSourceFrom;
        ContentResolver cr;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor;

        GetPhotoTask(Activity act, int sourceFrom) {
            this.mAct = act;
            this.mSourceFrom = sourceFrom;
            cr = mAct.getContentResolver();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mSourceFrom == 1) {
                //查詢已裁切圖片
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.DATA + " like ? ",
                        new String[] {"%ImageCropper%"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");
            } else if (mSourceFrom == 2) {
                //查詢全部圖片
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");
            }

        }

        @Override
        protected List<PhotoBean> doInBackground(Void... voids) {

            List<PhotoBean> photoBeanList = new ArrayList<>();

            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String filepath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //只找出副檔名為.jpg或.png的檔案
                    if(filepath.endsWith(".jpg") || filepath.endsWith(".png")) {
                        PhotoBean photoBean = new PhotoBean();
                        int id = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Images.Media._ID));// ID
                        photoBean.setThumbs(id + "");
                        photoBean.setImagePaths(filepath);
                        photoBeanList.add(photoBean);
                    }
                }

                cursor.close();
            }

            return photoBeanList;
        }

        @Override
        protected void onPostExecute(List<PhotoBean> list) {
            super.onPostExecute(list);
            setList(list);
        }
    }

    public void uploadPhoto(Bitmap bitmap) {

        File cropImgFile = UploadPhotoUtil.saveBitmapAndGetImageFile(bitmap);

        if(CheckConnectStatusManager.checkNetWorkConnect(getActivity())) {

            if (cropImgFile != null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.text_during_upload_picture), Toast.LENGTH_SHORT).show();
                showProgressDialog();
                UploadPhotoUtil.uploadImageToImgur(this, cropImgFile);
            } else {
                Log.d("tag1", "No Image File");
                Toast.makeText(getActivity(), getResources().getString(R.string.text_file_error), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_internet_error_upload), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void uploadImageSuccess(String uploadImageID) {
        dismissProgressDialog();
        String imgDirectUrl = "http://i.imgur.com/" + uploadImageID + ".jpg";
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        ImgurBean imgurBean = new ImgurBean();
        imgurBean.setImgID(uploadImageID);
        imgurBean.setImgUrl(imgDirectUrl);
        imgurBean.setUploadDate(sdFormat.format(new Date()));
        SharePreferenceManager.addUploadImageInHistory(getActivity(), imgurBean);
        Objects.requireNonNull(this.getActivity()).onBackPressed();
    }

    @Override
    public void uploadImageFail() {
        dismissProgressDialog();
        Objects.requireNonNull(this.getActivity()).onBackPressed();
    }

    private void showProgressDialog() {
        Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.instance();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
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

}
