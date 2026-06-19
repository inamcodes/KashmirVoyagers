package com.inam.kashtrack;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashRepository {

    private final CashEntryDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CashRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        dao = db.cashEntryDao();
    }

    public LiveData<List<CashEntry>> getAllEntries() {
        return dao.getAllEntries();
    }

    public LiveData<List<CashEntry>> searchEntries(String query) {
        return dao.searchEntries("%" + query + "%");
    }

    public void insert(CashEntry entry) {
        executor.execute(() -> dao.insert(entry));
    }

    public void update(CashEntry entry) {
        executor.execute(() -> dao.update(entry));
    }

    public void delete(CashEntry entry) {
        executor.execute(() -> dao.delete(entry));
    }

    public interface RangeCallback {
        void onResult(List<CashEntry> entries);
    }

    public void getEntriesBetween(long start, long end, RangeCallback callback) {
        executor.execute(() -> {
            List<CashEntry> result = dao.getEntriesBetween(start, end);
            callback.onResult(result);
        });
    }
}
