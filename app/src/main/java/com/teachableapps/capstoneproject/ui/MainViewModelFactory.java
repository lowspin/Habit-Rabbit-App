package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.teachableapps.capstoneproject.data.HabitRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final HabitRepository mRepository;
    private final int mProfileID;

    public MainViewModelFactory(HabitRepository repository, int pid) {
        this.mRepository = repository;
        mProfileID = pid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(mRepository, mProfileID);
    }
}
