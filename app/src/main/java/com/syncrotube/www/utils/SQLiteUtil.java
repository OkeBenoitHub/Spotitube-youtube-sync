package com.syncrotube.www.utils;

import android.content.Context;

import com.syncrotube.www.sqlitebase.AppExecutors;
import com.syncrotube.www.sqlitebase.database.AppDatabase;

/**
 * SQLite Util :: contain every recurring task dealing with SQLite db
 */
public class SQLiteUtil {

    // Member variable for the Database
    private static AppDatabase mDb;

    // interface for following method
    public interface insertEntryToDbCallback {
        void onInsertEntryToDb(AppDatabase appDatabase);
    }
    /**
     * Insert entry to database
     * @param context :: context
     */
    public static void insertEntryToDb(Context context, insertEntryToDbCallback insertEntryToDbCallback) {
        mDb = AppDatabase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(() -> {
            // insert new entry
            insertEntryToDbCallback.onInsertEntryToDb(mDb);
            //mDb.taskDao().insertTask(task);
        });
    }

    // interface for following method
    public interface updateEntryFromDbCallback {
        void onUpdateEntryFromDb(AppDatabase appDatabase);
    }
    /**
     * Update entry from database
     * @param context :: context
     */
    public static void updateEntryFromDb(Context context, updateEntryFromDbCallback updateEntryFromDbCallback) {
        mDb = AppDatabase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(() -> {
            // insert new entry
            updateEntryFromDbCallback.onUpdateEntryFromDb(mDb);
            //mDb.taskDao().updateTask(task);
        });
    }
}
