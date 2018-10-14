package com.teachableapps.capstoneproject.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface StickerDao {

    @Query("SELECT * FROM sticker WHERE profileId = :pfid AND projectId = :pid")
    List<Sticker> loadAllStickers(int pfid, int pid);

    @Insert
    void insertSticker(Sticker sticker);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSticker(Sticker sticker);

    @Delete
    void deleteSticker(Sticker sticker);

}