package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.ViewModel;

import com.teachableapps.capstoneproject.data.HabitRepository;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;

public class AddProjectViewModel extends ViewModel {
    private static final String TAG = AddProjectViewModel.class.getSimpleName();

    private HabitRepository mRepository;

    public AddProjectViewModel(HabitRepository repository) {
        mRepository = repository;
    }

    public void insertProject(ProjectEntry project) { mRepository.insertProject(project); }
    public void updateProject(ProjectEntry project) { mRepository.updateProject(project); }

}
