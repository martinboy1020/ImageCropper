package com.martinboy.imagecropper.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.martinboy.imagecropper.R;
import com.martinboy.imagecropper.adapter.ChoosePhotoJavaAdapter;
import com.martinboy.imagecropper.bean.PhotoBean;
import com.martinboy.imagecropper.utils.PhotoItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ChoosePhotoJavaActivity extends AppCompatActivity {

    private static final String TAG = ChoosePhotoJavaActivity.class.getSimpleName();

    private RecyclerView recycler_view_choose_photo;
    private int leftRight = 15;
    private int topBottom = 15;
    private int spanCount = 3;
    private ChoosePhotoJavaAdapter adapter;
    private static int FINISH_CROP_IMAGE = 103;
    private RelativeLayout btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_photo_page_activity);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recycler_view_choose_photo = findViewById(R.id.recycler_view_choose_photo);
        recycler_view_choose_photo.setLayoutManager(new GridLayoutManager(this, spanCount));
        recycler_view_choose_photo.addItemDecoration(new PhotoItemDecoration(leftRight, topBottom));
        new GetPhotoTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setList(List<PhotoBean> list) {
        Log.d(TAG, "PhotoBeanList Size: " + list.size());
        adapter = new ChoosePhotoJavaAdapter(this, list, spanCount, leftRight, topBottom);
        recycler_view_choose_photo.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    class GetPhotoTask extends AsyncTask<Void, Void, List<PhotoBean>> {

        ContentResolver cr = getContentResolver();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //查詢圖片
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

        }

        @Override
        protected List<PhotoBean> doInBackground(Void... voids) {

            List<PhotoBean> photoBeanList = new ArrayList<>();

            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    String filepath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if(!filepath.contains("cropImage_")) {

                        if (filepath.endsWith(".jpg") || filepath.endsWith(".png")) {
                            PhotoBean photoBean = new PhotoBean();
                            int id = cursor.getInt(cursor
                                    .getColumnIndex(MediaStore.Images.Media._ID));// ID
                            photoBean.setThumbs(id + "");
                            photoBean.setImagePaths(filepath);
                            photoBeanList.add(photoBean);
                        }
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

    public void goToCropPicture(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(ChoosePhotoJavaActivity.this, ImageCropperJavaActivity.class);
        intent.putExtra("imageUrl", String.valueOf(uri));
        startActivityForResult(intent, FINISH_CROP_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FINISH_CROP_IMAGE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }

    }

}