package com.inam.kashtrack;

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

        btnBack.setOnClickListener(v -> finish());

        btnPdf.setOnClickListener(v ->
                Toast.makeText(this, "PDF export coming soon", Toast.LENGTH_SHORT).show());

//        btnHistory.setOnClickListener(v ->
//                Toast.makeText(this, "Full history coming soon", Toast.LENGTH_SHORT).show());

        btnCashIn.setOnClickListener(v -> showAddEntryDialog("IN"));
        btnCashOut.setOnClickListener(v -> showAddEntryDialog("OUT"));

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        });

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

    private void showAddEntryDialog(String type) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDescription = view.findViewById(R.id.etDescription);
        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText("IN".equals(type) ? "Cash In" : "Cash Out");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
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

            viewModel.addEntry(amount, type, description);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
