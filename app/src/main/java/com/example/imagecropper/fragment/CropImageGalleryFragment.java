package com.example.imagecropper.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.imagecropper.R;
import com.example.imagecropper.adapter.ChooseCropPhotoJavaAdapter;
import com.example.imagecropper.bean.PhotoBean;
import com.example.imagecropper.utils.PhotoItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CropImageGalleryFragment extends Fragment {

    private static final String TAG = CropImageGalleryFragment.class.getSimpleName();

    private RecyclerView recycler_view_choose_photo;
    private int leftRight = 15;
    private int topBottom = 15;
    private int spanCount = 3;
    private ChooseCropPhotoJavaAdapter adapter;
    private static int FINISH_CROP_IMAGE = 103;
    private RelativeLayout btn_back;

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
            new GetPhotoTask(this.getActivity()).execute();
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
        ContentResolver cr;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor;

        GetPhotoTask(Activity act) {
            this.mAct = act;
            cr = mAct.getContentResolver();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //查詢圖片
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.DATA + " like ? ",
                    new String[] {"%ImageCropper%"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

        }

        @Override
        protected List<PhotoBean> doInBackground(Void... voids) {

            List<PhotoBean> PhotoBeanList = new ArrayList<>();

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
                        PhotoBeanList.add(photoBean);
                    }
                }

                cursor.close();
            }

            return PhotoBeanList;
        }

        @Override
        protected void onPostExecute(List<PhotoBean> list) {
            super.onPostExecute(list);
            setList(list);
        }
    }

}
