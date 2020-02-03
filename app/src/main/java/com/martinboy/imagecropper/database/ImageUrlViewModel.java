package com.martinboy.imagecropper.database;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ImageUrlViewModel extends AndroidViewModel {

    private static final String TAG = ImageUrlViewModel.class.getSimpleName();
    private LiveData<List<ImageEntity>> imageUrlLiveData;
    private ImageRepository imageRepository;

    public ImageUrlViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
        imageUrlLiveData = imageRepository.getLiveImageList();
    }

    public LiveData<List<ImageEntity>> getImageEntityLiveData() {
        return imageUrlLiveData;
    }
}
