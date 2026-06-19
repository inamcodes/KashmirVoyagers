package com.inam.kashtrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Builds a multi-page PDF report of cash entries within a date range:
 *   - App logo/branding + title
 *   - User name & profile photo (if set)
 *   - Date range shown
 *   - Cash In / Cash Out / Net summary
 *   - Simple table: Date, Description, In, Out, Balance (running)
 *
 * Pure android.graphics.pdf.PdfDocument — no external dependency needed.
 */
public class PdfGenerator {

    private static final int PAGE_WIDTH = 595;  // A4 @ 72dpi
    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN = 36;
    private static final int ROW_HEIGHT = 22;

    public interface Callback {
        void onSuccess(File pdfFile);
        void onError(Exception e);
    }

    public static void generate(Context context, List<CashEntry> entries, long startMillis, long endMillis,
                                 String userName, Bitmap userPhoto, Callback callback) {
        try {
            PdfDocument document = new PdfDocument();

            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.parseColor("#F4511E"));
            titlePaint.setTextSize(20);
            titlePaint.setFakeBoldText(true);

            Paint subtitlePaint = new Paint();
            subtitlePaint.setColor(Color.parseColor("#757575"));
            subtitlePaint.setTextSize(11);

            Paint sectionPaint = new Paint();
            sectionPaint.setColor(Color.parseColor("#212121"));
            sectionPaint.setTextSize(13);
            sectionPaint.setFakeBoldText(true);

            Paint headerCellPaint = new Paint();
            headerCellPaint.setColor(Color.WHITE);
            headerCellPaint.setTextSize(10);
            headerCellPaint.setFakeBoldText(true);

            Paint headerBgPaint = new Paint();
            headerBgPaint.setColor(Color.parseColor("#F4511E"));

            Paint cellPaint = new Paint();
            cellPaint.setColor(Color.parseColor("#212121"));
            cellPaint.setTextSize(9.5f);

            Paint greenPaint = new Paint(cellPaint);
            greenPaint.setColor(Color.parseColor("#2E7D32"));

            Paint redPaint = new Paint(cellPaint);
            redPaint.setColor(Color.parseColor("#E53935"));

            Paint linePaint = new Paint();
            linePaint.setColor(Color.parseColor("#E0E0E0"));
            linePaint.setStrokeWidth(1f);

            SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat rangeFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            // Column layout: Date | Description | In | Out | Balance
            int tableLeft = MARGIN;
            int tableRight = PAGE_WIDTH - MARGIN;
            int tableWidth = tableRight - tableLeft;
            float colDate = tableLeft;
            float colDesc = tableLeft + tableWidth * 0.18f;
            float colIn = tableLeft + tableWidth * 0.55f;
            float colOut = tableLeft + tableWidth * 0.70f;
            float colBalance = tableLeft + tableWidth * 0.85f;

            double runningBalance = 0;
            double totalIn = 0;
            double totalOut = 0;
            for (CashEntry e : entries) {
                if ("IN".equals(e.getType())) totalIn += e.getAmount();
                else totalOut += e.getAmount();
            }

            int pageNumber = 1;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            float y = MARGIN;

            // ---- Branding header ----
            canvas.drawText("Kashmir Voyagers", MARGIN, y + 18, titlePaint);
            canvas.drawText("Cash Book Report", MARGIN, y + 36, subtitlePaint);
            y += 50;

            // ---- User info ----
            if (userPhoto != null) {
                Bitmap circular = getCircularBitmap(userPhoto, 60);
                canvas.drawBitmap(circular, MARGIN, y, null);
            }
            float userTextX = userPhoto != null ? MARGIN + 70 : MARGIN;
            float userTextY = y + 24;
            if (userName != null && !userName.trim().isEmpty()) {
                canvas.drawText(userName, userTextX, userTextY, sectionPaint);
                userTextY += 16;
            }
            canvas.drawText("Period: " + rangeFmt.format(new Date(startMillis)) + " - " + rangeFmt.format(new Date(endMillis)),
                    userTextX, userTextY, subtitlePaint);
            canvas.drawText("Generated: " + dateFmt.format(new Date()), userTextX, userTextY + 14, subtitlePaint);
            y += Math.max(userPhoto != null ? 70 : 0, 50);

            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
            y += 20;

            // ---- Summary ----
            canvas.drawText("Summary", MARGIN, y, sectionPaint);
            y += 18;
            canvas.drawText("Cash In:  Rs " + formatAmount(totalIn), MARGIN, y, greenPaint);
            canvas.drawText("Cash Out:  Rs " + formatAmount(totalOut), MARGIN + 180, y, redPaint);
            canvas.drawText("Net:  Rs " + formatAmount(totalIn - totalOut), MARGIN + 360, y, sectionPaint);
            y += 26;

            // ---- Table header ----
            canvas.drawRect(tableLeft, y, tableRight, y + ROW_HEIGHT, headerBgPaint);
            float headerTextY = y + ROW_HEIGHT - 7;
            canvas.drawText("Date", colDate + 4, headerTextY, headerCellPaint);
            canvas.drawText("Description", colDesc + 4, headerTextY, headerCellPaint);
            canvas.drawText("In", colIn + 4, headerTextY, headerCellPaint);
            canvas.drawText("Out", colOut + 4, headerTextY, headerCellPaint);
            canvas.drawText("Balance", colBalance + 4, headerTextY, headerCellPaint);
            y += ROW_HEIGHT;

            // Entries are passed newest-first (DB order); reverse to compute
            // running balance chronologically, then render oldest-first so
            // the table reads top-to-bottom like a ledger.
            List<CashEntry> chronological = new java.util.ArrayList<>(entries);
            java.util.Collections.reverse(chronological);

            boolean alternate = false;
            for (CashEntry e : chronological) {
                if (y + ROW_HEIGHT > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page);
                    pageNumber++;
                    pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = MARGIN;

                    // Repeat table header on new page.
                    canvas.drawRect(tableLeft, y, tableRight, y + ROW_HEIGHT, headerBgPaint);
                    float hY = y + ROW_HEIGHT - 7;
                    canvas.drawText("Date", colDate + 4, hY, headerCellPaint);
                    canvas.drawText("Description", colDesc + 4, hY, headerCellPaint);
                    canvas.drawText("In", colIn + 4, hY, headerCellPaint);
                    canvas.drawText("Out", colOut + 4, hY, headerCellPaint);
                    canvas.drawText("Balance", colBalance + 4, hY, headerCellPaint);
                    y += ROW_HEIGHT;
                }

                boolean isIn = "IN".equals(e.getType());
                runningBalance += isIn ? e.getAmount() : -e.getAmount();

                if (alternate) {
                    Paint zebraPaint = new Paint();
                    zebraPaint.setColor(Color.parseColor("#FAFAFA"));
                    canvas.drawRect(tableLeft, y, tableRight, y + ROW_HEIGHT, zebraPaint);
                }
                alternate = !alternate;

                float rowTextY = y + ROW_HEIGHT - 7;
                canvas.drawText(dateFmt.format(new Date(e.getTimestamp())), colDate + 4, rowTextY, cellPaint);
                canvas.drawText(truncate(e.getDescription(), 26), colDesc + 4, rowTextY, cellPaint);
                canvas.drawText(isIn ? formatAmount(e.getAmount()) : "", colIn + 4, rowTextY, greenPaint);
                canvas.drawText(!isIn ? formatAmount(e.getAmount()) : "", colOut + 4, rowTextY, redPaint);
                canvas.drawText(formatAmount(runningBalance), colBalance + 4, rowTextY,
                        runningBalance >= 0 ? greenPaint : redPaint);

                canvas.drawLine(tableLeft, y + ROW_HEIGHT, tableRight, y + ROW_HEIGHT, linePaint);
                y += ROW_HEIGHT;
            }

            if (chronological.isEmpty()) {
                canvas.drawText("No entries in this date range.", MARGIN, y + 16, subtitlePaint);
            }

            document.finishPage(page);

            File outDir = new File(context.getCacheDir(), "pdf_exports");
            if (!outDir.exists()) outDir.mkdirs();
            String fileName = "KashmirVoyagers_CashBook_" + System.currentTimeMillis() + ".pdf";
            File outFile = new File(outDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                document.writeTo(fos);
            }
            document.close();

            callback.onSuccess(outFile);
        } catch (IOException | RuntimeException e) {
            callback.onError(e);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String formatAmount(double amount) {
        return DateUtils.formatAmount(amount);
    }

    private static Bitmap getCircularBitmap(Bitmap source, int sizeDp) {
        Bitmap output = Bitmap.createBitmap(sizeDp, sizeDp, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF rect = new RectF(0, 0, sizeDp, sizeDp);
        canvas.drawOval(rect, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        Bitmap scaled = Bitmap.createScaledBitmap(source, sizeDp, sizeDp, true);
        canvas.drawBitmap(scaled, 0, 0, paint);
        return output;
    }
}
