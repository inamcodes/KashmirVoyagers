package com.inam.kashtrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvCashInHand;
    private TextView tvTodayBalance;
    private EditText etSearch;
    private CashEntryAdapter adapter;
    private CashViewModel viewModel;
    private ProfileRepository profileRepository;

    // App-launch lock: true once the user has unlocked during this process's
    // lifetime. It's static so it survives across this Activity being
    // stopped/restarted by quick app-switching, but resets to false on a
    // fresh process (i.e. after the app has been fully closed and relaunched).
    private static boolean appUnlockedThisProcess = false;

    // --- Per-entry photo (attach via camera/gallery) state for the currently open add/edit dialog ---
    private String pendingDialogPhotoPath = null;
    private File pendingCameraFile = null;
    private ImageView ivEntryPhotoThumbRef = null;
    private TextView tvAddPhotoRef = null;
    private View btnRemoveEntryPhotoRef = null;
    private AlertDialog activeEntryDialog = null;

    private final ActivityResultLauncher<String> pickGalleryPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                File dest = EntryPhotoUtils.newPhotoFile(this);
                if (EntryPhotoUtils.copyUriToFile(this, uri, dest)) {
                    applyPickedEntryPhoto(dest.getAbsolutePath());
                } else {
                    Toast.makeText(this, "Couldn't load that photo", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takeEntryPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && pendingCameraFile != null) {
                    applyPickedEntryPhoto(pendingCameraFile.getAbsolutePath());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileRepository = new ProfileRepository(this);

        // Whole-app lock: only challenged once per process lifetime, so quick
        // task switches don't re-prompt; only a fully closed + relaunched app does.
        if (profileRepository.isPasswordSetCached() && !appUnlockedThisProcess) {
            showAppLockScreen();
            return;
        }

        initMainScreen();
    }

    /** Password gate shown before the main UI on a fresh app launch (see appUnlockedThisProcess). */
    private void showAppLockScreen() {
        setContentView(R.layout.activity_app_lock);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EditText etPassword = findViewById(R.id.etAppLockPassword);
        ImageButton btnToggle = findViewById(R.id.btnAppLockToggle);
        MaterialButton btnUnlock = findViewById(R.id.btnAppLockUnlock);
        TextView tvError = findViewById(R.id.tvAppLockError);

        final boolean[] passwordVisible = {false};
        btnToggle.setOnClickListener(v -> {
            passwordVisible[0] = !passwordVisible[0];
            etPassword.setInputType(passwordVisible[0]
                    ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnUnlock.setOnClickListener(v -> {
            String entered = etPassword.getText().toString();
            if (entered.isEmpty()) {
                tvError.setText("Enter your password");
                tvError.setVisibility(TextView.VISIBLE);
                return;
            }
            if (profileRepository.verifyPasswordCached(entered)) {
                tvError.setVisibility(TextView.GONE);
                appUnlockedThisProcess = true;
                initMainScreen();
            } else {
                tvError.setText("Incorrect password");
                tvError.setVisibility(TextView.VISIBLE);
            }
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            btnUnlock.performClick();
            return true;
        });
    }

    /** Sets up the normal Cash Book screen; called directly, or after the app lock is passed. */
    private void initMainScreen() {
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
        adapter.setOnEntryClickListener(entry -> showPhotoViewer(entry.getPhotoPath()));
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
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
        ImageView ivEntryPhotoThumb = view.findViewById(R.id.ivEntryPhotoThumb);
        TextView tvAddPhoto = view.findViewById(R.id.tvAddPhoto);
        ImageButton btnRemoveEntryPhoto = view.findViewById(R.id.btnRemoveEntryPhoto);

        boolean isEdit = existingEntry != null;
        String prefix = isEdit ? "Edit " : "";
        tvDialogTitle.setText(prefix + ("IN".equals(type) ? "Cash In" : "Cash Out"));

        if (isEdit) {
            etAmount.setText(DateUtils.formatAmount(existingEntry.getAmount()));
            etDescription.setText(existingEntry.getDescription());
        }

        // Wire up the optional photo (camera/gallery) attachment for this dialog.
        ivEntryPhotoThumbRef = ivEntryPhotoThumb;
        tvAddPhotoRef = tvAddPhoto;
        btnRemoveEntryPhotoRef = btnRemoveEntryPhoto;
        pendingDialogPhotoPath = isEdit ? existingEntry.getPhotoPath() : null;
        updateEntryPhotoPreview();

        View.OnClickListener choosePhotoListener = v -> showPhotoSourceChooser();
        ivEntryPhotoThumb.setOnClickListener(choosePhotoListener);
        tvAddPhoto.setOnClickListener(choosePhotoListener);
        btnRemoveEntryPhoto.setOnClickListener(v -> {
            pendingDialogPhotoPath = null;
            updateEntryPhotoPreview();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        activeEntryDialog = dialog;
        dialog.setOnDismissListener(d -> {
            activeEntryDialog = null;
            ivEntryPhotoThumbRef = null;
            tvAddPhotoRef = null;
            btnRemoveEntryPhotoRef = null;
        });

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
                existingEntry.setPhotoPath(pendingDialogPhotoPath);
                viewModel.updateEntry(existingEntry);
            } else {
                viewModel.addEntry(amount, type, description, pendingDialogPhotoPath);
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /** Lets the user pick where the entry photo comes from. */
    private void showPhotoSourceChooser() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Add Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        launchEntryCamera();
                    } else {
                        pickGalleryPhotoLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void launchEntryCamera() {
        try {
            pendingCameraFile = EntryPhotoUtils.newPhotoFile(this);
            Uri photoUri = EntryPhotoUtils.getUriForFile(this, pendingCameraFile);
            takeEntryPhotoLauncher.launch(photoUri);
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't open camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** Called once a photo has been picked/captured, while the entry dialog is still open. */
    private void applyPickedEntryPhoto(String path) {
        if (activeEntryDialog == null || !activeEntryDialog.isShowing()) return;
        pendingDialogPhotoPath = path;
        updateEntryPhotoPreview();
    }

    /** Refreshes the dialog's thumbnail/label/remove-button to match pendingDialogPhotoPath. */
    private void updateEntryPhotoPreview() {
        if (ivEntryPhotoThumbRef == null || tvAddPhotoRef == null || btnRemoveEntryPhotoRef == null) return;

        if (pendingDialogPhotoPath != null && !pendingDialogPhotoPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(pendingDialogPhotoPath);
            if (bitmap != null) {
                ivEntryPhotoThumbRef.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivEntryPhotoThumbRef.setPadding(0, 0, 0, 0);
                ivEntryPhotoThumbRef.setImageBitmap(bitmap);
            }
            tvAddPhotoRef.setText("Change photo");
            btnRemoveEntryPhotoRef.setVisibility(View.VISIBLE);
        } else {
            int pad = (int) (6 * getResources().getDisplayMetrics().density);
            ivEntryPhotoThumbRef.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ivEntryPhotoThumbRef.setPadding(pad, pad, pad, pad);
            ivEntryPhotoThumbRef.setImageResource(android.R.drawable.ic_menu_camera);
            tvAddPhotoRef.setText("Add photo (optional)");
            btnRemoveEntryPhotoRef.setVisibility(View.GONE);
        }
    }

    /** Opens a tap-to-dismiss full-screen viewer for an entry's attached photo, if any. */
    private void showPhotoViewer(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) return;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        if (bitmap == null) {
            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_photo_viewer, null);
        ImageView ivFullPhoto = view.findViewById(R.id.ivFullPhoto);
        ivFullPhoto.setImageBitmap(bitmap);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        view.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void showPdfExportDialog() {
        startActivity(new Intent(this, PdfExportActivity.class));
    }
}
