package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.teachableapps.capstoneproject.data.AppExecutors;
import com.teachableapps.capstoneproject.data.HabitRepository;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;
import com.teachableapps.capstoneproject.data.database.Sticker;

import java.util.Date;
import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private HabitRepository mRepository;
    private LiveData<List<ProjectEntry>> projects;

    public MainViewModel(HabitRepository repository, int profileId) {
        mRepository = repository;
        projects = mRepository.loadAllProjects(profileId);
    }

    public LiveData<List<ProjectEntry>> getProjects() {
        return projects;
    }

    public void deleteProject(ProjectEntry project) { mRepository.delete(project); }

    public List<Sticker> getStickers(int profileId, int projectId) { return mRepository.getStickersByProjectId(profileId, projectId); }

    public void updateStickerSlots(final ProjectEntry thisproject) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Sticker> stickers = getStickers(thisproject.getProfileId(), thisproject.getId());

                int numStickerSlots = stickers.size();
                int repTarget = thisproject.getRepTarget();

                if (repTarget > numStickerSlots) {
                    // increase slots
                    Date todaysdate = new Date();
                    for (int i = numStickerSlots; i < repTarget; i++) {
                        Sticker s = new Sticker(thisproject.getProfileId(), thisproject.getId(), i, "", todaysdate, false);
                        mRepository.insertSticker(s); // insert into dB
                    }
                } else if (repTarget < stickers.size()) {
                    // remove sticker slots from the end
                    for (int i=0; i<(numStickerSlots-repTarget); i++) {
                        if(stickers.size()>0) {
                            mRepository.removeSticker(stickers.get(stickers.size() - 1)); // remove from dB
                            stickers.remove(stickers.size() - 1); // remove from recyclerview
                        }
                    }
                }
            }
        });
    }

}