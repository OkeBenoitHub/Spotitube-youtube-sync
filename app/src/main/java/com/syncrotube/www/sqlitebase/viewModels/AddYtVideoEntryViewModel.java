package com.syncrotube.www.sqlitebase.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.syncrotube.www.sqlitebase.database.AppDatabase;
import com.syncrotube.www.sqlitebase.database.YtVideoEntry;

public class AddYtVideoEntryViewModel extends ViewModel {
    private LiveData<YtVideoEntry> mYtVideoEntry;

    public AddYtVideoEntryViewModel(AppDatabase database, int YtVideoEntryId) {
        mYtVideoEntry = database.mPlaylistDao().loadPlaylistById(YtVideoEntryId);
    }

    public LiveData<YtVideoEntry> getYtVideoEntry() {
        return mYtVideoEntry;
    }
}
