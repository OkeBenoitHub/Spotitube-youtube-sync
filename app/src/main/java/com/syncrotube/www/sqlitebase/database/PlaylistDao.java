package com.syncrotube.www.sqlitebase.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Query("SELECT * FROM playlist ORDER BY id DESC")
    LiveData<List<YtVideoEntry>> loadAllYtVideoEntries();

    @Insert
    void insertYtVideoEntry(YtVideoEntry ytVideoEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateYtVideoEntry(YtVideoEntry ytVideoEntry);

    @Delete
    void deleteYtVideoEntry(YtVideoEntry ytVideoEntry);

    @Query("SELECT * FROM playlist WHERE id = :id")
    LiveData<YtVideoEntry> loadPlaylistById(int id);
}
