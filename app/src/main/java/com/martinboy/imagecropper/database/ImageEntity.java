package com.martinboy.imagecropper.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = DataBaseManager.IMAGE_TABLE)
public class ImageEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    protected ImageEntity(Parcel in) {
        uid = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {
        @Override
        public ImageEntity createFromParcel(Parcel in) {
            return new ImageEntity(in);
        }

        @Override
        public ImageEntity[] newArray(int size) {
            return new ImageEntity[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image_url) {
        this.imageUrl = imageUrl;
    }

    public ImageEntity() {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(imageUrl);
    }
}
