package com.syncrotube.www.sqlitebase.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syncrotube.www.sqlitebase.database.AppDatabase;
import com.syncrotube.www.sqlitebase.database.YtVideoEntry;

import java.util.List;

public class YtVideoEntriesViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = YtVideoEntriesViewModel.class.getSimpleName();

    private final LiveData<List<YtVideoEntry>> mYtVideoEntries;

    public YtVideoEntriesViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving youtube video entries from the DataBase");
        mYtVideoEntries = database.mPlaylistDao().loadAllYtVideoEntries();
    }

    public LiveData<List<YtVideoEntry>> getYtVideoEntries() {
        return mYtVideoEntries;
    }
}
