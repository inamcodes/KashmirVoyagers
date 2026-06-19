package com.inam.kashtrack;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helpers for saving per-entry photos (camera capture or gallery pick) as
 * private files under the app's internal storage, and for handing out a
 * FileProvider Uri so the camera app can write directly into one of those
 * files. Mirrors the approach ProfileRepository uses for the profile photo,
 * but supports one file per entry instead of a single shared file.
 */
public class EntryPhotoUtils {

    private static final String DIR_NAME = "entry_photos";

    /** Creates (but does not write to) a new unique file for an entry photo. */
    public static File newPhotoFile(Context context) {
        File dir = new File(context.getFilesDir(), DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "entry_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000) + ".jpg";
        return new File(dir, fileName);
    }

    /** A content:// Uri usable by the camera app (or other apps) to write into destFile. */
    public static Uri getUriForFile(Context context, File destFile) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", destFile);
    }

    /** Copies the bytes behind a picked gallery Uri into destFile. Returns true on success. */
    public static boolean copyUriToFile(Context context, Uri sourceUri, File destFile) {
        try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
             OutputStream out = new FileOutputStream(destFile)) {
            if (in == null) return false;
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
