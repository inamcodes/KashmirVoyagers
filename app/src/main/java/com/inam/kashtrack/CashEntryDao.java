package com.inam.kashtrack;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CashEntryDao {

    @Insert
    void insert(CashEntry entry);

    @Update
    void update(CashEntry entry);

    @Delete
    void delete(CashEntry entry);

    @Query("SELECT * FROM cash_entries ORDER BY timestamp DESC")
    LiveData<List<CashEntry>> getAllEntries();

    @Query("SELECT * FROM cash_entries WHERE description LIKE :query ORDER BY timestamp DESC")
    LiveData<List<CashEntry>> searchEntries(String query);

    @Query("SELECT * FROM cash_entries WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    List<CashEntry> getEntriesBetween(long start, long end);
}
