package com.martinboy.imagecropper.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM " + DataBaseManager.IMAGE_TABLE)
    List<ImageEntity> getAll();

    @Query("SELECT * FROM " + DataBaseManager.IMAGE_TABLE)
    LiveData<List<ImageEntity>> getAllLiveData();

    @Query("SELECT * FROM " + DataBaseManager.IMAGE_TABLE + " WHERE uid IN (:userIds)")
    List<ImageEntity> getAllByUids(int[] userIds);

    @Query("SELECT * FROM " + DataBaseManager.IMAGE_TABLE + " WHERE uid=:uid")
    ImageEntity getUserByUid(int uid);

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND "
//            + "last_name LIKE :last LIMIT 1")
//    User findByName(String first, String last);

//    @Query("SELECT * FROM " + DataBaseManager.IMAGE_TABLE + " WHERE name LIKE :name LIMIT 1")
//    ImageEntity findByName(String name);

    @Query("SELECT COUNT(*) FROM " + DataBaseManager.IMAGE_TABLE)
    int getUserCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(ImageEntity... userEntities);

    @Update
    void updateUser(ImageEntity... userEntities);

    @Delete
    void deleteUser(ImageEntity... userEntities);

    @Query("DELETE FROM " + DataBaseManager.IMAGE_TABLE + " WHERE uid=:uid")
    void deleteUser(int uid);

}
