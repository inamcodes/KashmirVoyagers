package com.inam.kashtrack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class CashViewModel extends AndroidViewModel {

    private final CashRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    // Unfiltered - used to calculate Cash in Hand / Today Balance
    private final LiveData<List<CashEntry>> allEntries;

    // Filtered by search - used to populate the RecyclerView
    private final LiveData<List<CashEntry>> displayEntries;

    public CashViewModel(@NonNull Application application) {
        super(application);
        repository = new CashRepository(application);
        allEntries = repository.getAllEntries();
        displayEntries = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return repository.getAllEntries();
            }
            return repository.searchEntries(query.trim());
        });
    }

    public LiveData<List<CashEntry>> getAllEntries() {
        return allEntries;
    }

    public LiveData<List<CashEntry>> getDisplayEntries() {
        return displayEntries;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void addEntry(double amount, String type, String description) {
        repository.insert(new CashEntry(amount, type, description, System.currentTimeMillis()));
    }
}
