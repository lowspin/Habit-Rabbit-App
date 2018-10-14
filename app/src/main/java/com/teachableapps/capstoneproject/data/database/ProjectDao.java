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
public interface ProjectDao {

    @Query("SELECT * FROM project WHERE profileId = :pid")
    LiveData<List<ProjectEntry>> loadAllProjects(int pid);

    @Insert
    void insertProject(ProjectEntry projectEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProject(ProjectEntry projectEntry);

    @Delete
    void deleteProject(ProjectEntry projectEntry);

}