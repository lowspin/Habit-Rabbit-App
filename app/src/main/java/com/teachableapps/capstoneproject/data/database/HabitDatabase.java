package com.teachableapps.capstoneproject.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {ProjectEntry.class, Sticker.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class HabitDatabase extends RoomDatabase {

    private static final String LOG_TAG = HabitDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "habitdb";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static HabitDatabase sInstance;

    public static HabitDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        HabitDatabase.class, HabitDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract ProjectDao projectDao();
    public abstract StickerDao stickerDao();

}