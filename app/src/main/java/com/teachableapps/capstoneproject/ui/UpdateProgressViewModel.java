package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.ViewModel;

import com.teachableapps.capstoneproject.data.HabitRepository;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;
import com.teachableapps.capstoneproject.data.database.Sticker;

import java.util.List;

public class UpdateProgressViewModel extends ViewModel {
    private static final String TAG = AddProjectViewModel.class.getSimpleName();

    private HabitRepository mRepository;

    public UpdateProgressViewModel(HabitRepository repository) {
        mRepository = repository;
    }
    public List<Sticker> getStickers(int profileId, int projectId) { return mRepository.getStickersByProjectId(profileId, projectId); }
    public void updateSticker(Sticker sticker) { mRepository.updateSticker(sticker); }
    public void updateProject(ProjectEntry project) { mRepository.updateProject(project); }

}

