package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.teachableapps.capstoneproject.data.HabitRepository;

public class UpdateProgressViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final HabitRepository mRepository;

    public UpdateProgressViewModelFactory(HabitRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new UpdateProgressViewModel(mRepository);
    }
}
