package com.syncrotube.www.sqlitebase.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.syncrotube.www.sqlitebase.database.AppDatabase;

public class AddYtVideoEntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mYtVideoEntryId;

    public AddYtVideoEntryViewModelFactory(AppDatabase database, int YtVideoEntryId) {
        mDb = database;
        mYtVideoEntryId = YtVideoEntryId;
    }

    // COMPLETED (4) Uncomment the following method
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddYtVideoEntryViewModel(mDb, mYtVideoEntryId);
    }
}
