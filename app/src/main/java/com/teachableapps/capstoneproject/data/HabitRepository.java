package com.teachableapps.capstoneproject.data;

import android.arch.lifecycle.LiveData;
import com.teachableapps.capstoneproject.data.database.ProjectDao;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;
import com.teachableapps.capstoneproject.data.database.Sticker;
import com.teachableapps.capstoneproject.data.database.StickerDao;

import java.util.List;

public class HabitRepository {

    private static final String LOG_TAG = HabitRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static HabitRepository sInstance;
    private final ProjectDao mProjectDao;
    private final StickerDao mStickerDao;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private HabitRepository(ProjectDao projectDao, StickerDao stickerDao, AppExecutors executors) {
        mProjectDao = projectDao;
        mStickerDao = stickerDao;
        mExecutors = executors;
    }

    public synchronized static HabitRepository getInstance(
            ProjectDao projectDao,
            StickerDao stickerDao,
            AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new HabitRepository(projectDao, stickerDao, executors);
            }
        }
        return sInstance;
    }

    /*-------------- Projects operations -----------------*/

    public LiveData<List<ProjectEntry>> loadAllProjects(int profileId) {
        return mProjectDao.loadAllProjects(profileId);
    }

    public void delete(final ProjectEntry project){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mProjectDao.deleteProject(project);
            }
        });
    }

    public void insertProject(final ProjectEntry project){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mProjectDao.insertProject(project);
            }
        });
    }

    public void updateProject(final ProjectEntry project){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mProjectDao.updateProject(project);
            }
        });
    }

    /*--------------- Stickers operations ---------------*/

    public List<Sticker> getStickersByProjectId(final int profileId, final int projectId) {
        List<Sticker> stickers = mStickerDao.loadAllStickers(profileId, projectId);
        return stickers;
    }

    public void insertSticker(final Sticker sticker) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mStickerDao.insertSticker(sticker);
            }
        });
    }

    public void removeSticker(final Sticker sticker) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mStickerDao.deleteSticker(sticker);
            }
        });
    }

    public void updateSticker(final Sticker sticker) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mStickerDao.updateSticker(sticker);
            }
        });
    }

}
