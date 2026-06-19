package com.inam.kashtrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Lets the user pick a date range, then generates a PDF report of that
 * range's cash entries (Cash In/Out summary, user name & photo, app
 * branding, and a simple Date/Description/In/Out/Balance table) and
 * opens the system share sheet.
 */
public class PdfExportActivity extends AppCompatActivity {

    private final Calendar startCal = Calendar.getInstance();
    private final Calendar endCal = Calendar.getInstance();
    private final SimpleDateFormat displayFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private TextView tvStartDate, tvEndDate;
    private CashViewModel viewModel;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_export);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        viewModel = new ViewModelProvider(this).get(CashViewModel.class);
        profileRepository = new ProfileRepository(this);

        // Default range: start of current month -> today.
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        zeroOutTime(startCal);
        endOfDay(endCal);

        ImageButton btnBack = findViewById(R.id.btnExportBack);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        MaterialButton btnExport = findViewById(R.id.btnGenerateExport);

        btnBack.setOnClickListener(v -> finish());

        updateDateLabels();

        tvStartDate.setOnClickListener(v -> pickDate(true));
        tvEndDate.setOnClickListener(v -> pickDate(false));

        btnExport.setOnClickListener(v -> exportPdf());
    }

    private void pickDate(boolean isStart) {
        Calendar target = isStart ? startCal : endCal;
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            target.set(year, month, dayOfMonth);
            if (isStart) {
                zeroOutTime(startCal);
            } else {
                endOfDay(endCal);
            }
            updateDateLabels();
        }, target.get(Calendar.YEAR), target.get(Calendar.MONTH), target.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabels() {
        tvStartDate.setText(displayFmt.format(startCal.getTime()));
        tvEndDate.setText(displayFmt.format(endCal.getTime()));
    }

    private void zeroOutTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void endOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
    }

    private void exportPdf() {
        if (startCal.after(endCal)) {
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return;
        }

        long start = startCal.getTimeInMillis();
        long end = endCal.getTimeInMillis();

        Toast.makeText(this, "Generating PDF...", Toast.LENGTH_SHORT).show();

        viewModel.getEntriesBetween(start, end, entries -> runOnUiThread(() ->
                generateWithLocalPhoto(entries, start, end)));
    }

    private void generateWithLocalPhoto(List<CashEntry> entries, long start, long end) {
        String name = profileRepository.getCachedName();
        String photoPath = profileRepository.getCachedPhotoUrl();

        Bitmap photo = null;
        if (photoPath != null && !photoPath.isEmpty()) {
            photo = BitmapFactory.decodeFile(photoPath);
        }
        generateAndShare(entries, start, end, name, photo);
    }

    private void generateAndShare(List<CashEntry> entries, long start, long end, String name, Bitmap photo) {
        PdfGenerator.generate(this, entries, start, end, name, photo, new PdfGenerator.Callback() {
            @Override
            public void onSuccess(File pdfFile) {
                runOnUiThread(() -> sharePdf(pdfFile));
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(PdfExportActivity.this,
                        "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void sharePdf(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Cash Book PDF"));
    }
}
