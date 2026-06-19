package com.inam.kashtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvCashInHand;
    private TextView tvTodayBalance;
    private EditText etSearch;
    private CashEntryAdapter adapter;
    private CashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We use a custom header, so hide the default ActionBar/title bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvCashInHand = findViewById(R.id.tvCashInHand);
        tvTodayBalance = findViewById(R.id.tvTodayBalance);
        etSearch = findViewById(R.id.etSearch);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnPdf = findViewById(R.id.btnPdf);
//        LinearLayout btnHistory = findViewById(R.id.btnHistory);
        MaterialButton btnCashIn = findViewById(R.id.btnCashIn);
        MaterialButton btnCashOut = findViewById(R.id.btnCashOut);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        adapter = new CashEntryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CashViewModel.class);

        // Used for the Cash in Hand / Today Balance totals (always unfiltered).
        viewModel.getAllEntries().observe(this, this::updateSummary);

        // Used to populate the list (respects the search box).
        viewModel.getDisplayEntries().observe(this, entries ->
                adapter.submitList(CashEntryAdapter.buildRows(entries)));

        // Swipe right = delete (with confirmation), swipe left = edit.
        SwipeActionCallback swipeCallback = new SwipeActionCallback(this, adapter, new SwipeActionCallback.SwipeListener() {
            @Override
            public void onSwipedRight(CashEntry entry, int position) {
                confirmDelete(entry);
            }

            @Override
            public void onSwipedLeft(CashEntry entry, int position) {
                showEditEntryDialog(entry);
            }
        });
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);

        btnBack.setOnClickListener(v -> finish());

        btnPdf.setOnClickListener(v -> showPdfExportDialog());

//        btnHistory.setOnClickListener(v ->
//                Toast.makeText(this, "Full history coming soon", Toast.LENGTH_SHORT).show());

        btnCashIn.setOnClickListener(v -> showAddEntryDialog("IN"));
        btnCashOut.setOnClickListener(v -> showAddEntryDialog("OUT"));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_cash) {
                return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileGateActivity.class));
                return false;
            }
            Toast.makeText(this, item.getTitle() + " coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void updateSummary(List<CashEntry> entries) {
        double total = 0;
        double today = 0;
        for (CashEntry e : entries) {
            double signed = "IN".equals(e.getType()) ? e.getAmount() : -e.getAmount();
            total += signed;
            if (DateUtils.isToday(e.getTimestamp())) {
                today += signed;
            }
        }
        tvCashInHand.setText("Rs " + DateUtils.formatAmount(total));
        tvTodayBalance.setText("Rs " + DateUtils.formatAmount(today));
    }

    private void confirmDelete(CashEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry?")
                .setMessage("This will permanently delete \"" + entry.getDescription() + "\" (Rs "
                        + DateUtils.formatAmount(entry.getAmount()) + "). This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteEntry(entry))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showAddEntryDialog(String type) {
        showEntryDialog(type, null);
    }

    private void showEditEntryDialog(CashEntry entry) {
        showEntryDialog(entry.getType(), entry);
    }

    /**
     * Shared dialog for both adding a new entry and editing an existing one.
     * If existingEntry is null this behaves as "add"; otherwise it pre-fills
     * the fields and saves back onto the same entry (preserving its id and
     * original timestamp).
     */
    private void showEntryDialog(String type, CashEntry existingEntry) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDescription = view.findViewById(R.id.etDescription);
        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);

        boolean isEdit = existingEntry != null;
        String prefix = isEdit ? "Edit " : "";
        tvDialogTitle.setText(prefix + ("IN".equals(type) ? "Cash In" : "Cash Out"));

        if (isEdit) {
            etAmount.setText(DateUtils.formatAmount(existingEntry.getAmount()));
            etDescription.setText(existingEntry.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        if (isEdit) {
            btnSave.setText("UPDATE");
        }

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim().replace(",", "");
            if (amountStr.isEmpty()) {
                etAmount.setError("Enter an amount");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Invalid amount");
                return;
            }

            String description = etDescription.getText().toString().trim();
            if (description.isEmpty()) {
                description = "IN".equals(type) ? "Cash in hand" : "Cash out";
            }

            if (isEdit) {
                existingEntry.setAmount(amount);
                existingEntry.setDescription(description);
                viewModel.updateEntry(existingEntry);
            } else {
                viewModel.addEntry(amount, type, description);
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPdfExportDialog() {
        startActivity(new Intent(this, PdfExportActivity.class));
    }
}
