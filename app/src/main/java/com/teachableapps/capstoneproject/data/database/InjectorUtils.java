package com.teachableapps.capstoneproject.data.database;

import android.content.Context;

import com.teachableapps.capstoneproject.data.AppExecutors;
import com.teachableapps.capstoneproject.data.HabitRepository;
import com.teachableapps.capstoneproject.ui.AddProjectViewModelFactory;
import com.teachableapps.capstoneproject.ui.MainViewModelFactory;
import com.teachableapps.capstoneproject.ui.UpdateProgressViewModelFactory;

public class InjectorUtils {

    public static HabitRepository provideRepository(Context context) {
        HabitDatabase database = HabitDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return HabitRepository.getInstance(database.projectDao(), database.stickerDao(), executors);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context, int profileID) {
        HabitRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository, profileID);
    }

    public static AddProjectViewModelFactory provideAddProjectViewModelFactory(Context context) {
        HabitRepository repository = provideRepository(context.getApplicationContext());
        return new AddProjectViewModelFactory(repository);
    }

    public static UpdateProgressViewModelFactory provideUpdateProgressViewModelFactory(Context context) {
        HabitRepository repository = provideRepository(context.getApplicationContext());
        return new UpdateProgressViewModelFactory(repository);
    }

}
