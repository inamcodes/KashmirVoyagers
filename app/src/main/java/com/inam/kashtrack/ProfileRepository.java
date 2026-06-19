package com.inam.kashtrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stores the Profile (name, photo, password) entirely on-device:
 *   - name + password hash/salt -> SharedPreferences
 *   - photo -> a JPEG file in the app's internal storage
 *
 * No network or cloud account required; everything works fully offline.
 */
public class ProfileRepository {

    private static final String PREFS = "profile_cache";
    private static final String KEY_HASH = "cached_hash";
    private static final String KEY_SALT = "cached_salt";
    private static final String KEY_NAME = "cached_name";
    private static final String PHOTO_FILE_NAME = "profile_photo.jpg";

    private final SharedPreferences prefs;
    private final Context context;

    public interface ProfileCallback {
        void onSuccess(UserProfile profile);
        void onError(Exception e);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface UploadCallback {
        void onSuccess(String localPath);
        void onError(Exception e);
    }

    public ProfileRepository(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isPasswordSetCached() {
        String hash = prefs.getString(KEY_HASH, null);
        return hash != null && !hash.isEmpty();
    }

    public String getCachedName() {
        return prefs.getString(KEY_NAME, "");
    }

    /** Returns the absolute file path of the saved profile photo, or "" if none is set. */
    public String getCachedPhotoUrl() {
        File photoFile = new File(context.getFilesDir(), PHOTO_FILE_NAME);
        return photoFile.exists() ? photoFile.getAbsolutePath() : "";
    }

    public boolean verifyPasswordCached(String password) {
        String hash = prefs.getString(KEY_HASH, null);
        String salt = prefs.getString(KEY_SALT, null);
        return PasswordUtils.verify(password, hash, salt);
    }

    /** Loads the profile synchronously from local storage; callback runs immediately. */
    public void loadProfile(ProfileCallback callback) {
        UserProfile profile = new UserProfile();
        profile.name = getCachedName();
        profile.photoUrl = getCachedPhotoUrl();
        profile.passwordHash = prefs.getString(KEY_HASH, null);
        profile.passwordSalt = prefs.getString(KEY_SALT, null);
        callback.onSuccess(profile);
    }

    /** Saves name + password fields. Photo is handled separately via savePhoto(). */
    public void saveProfile(UserProfile profile, SimpleCallback callback) {
        try {
            prefs.edit()
                    .putString(KEY_NAME, profile.name)
                    .putString(KEY_HASH, profile.passwordHash)
                    .putString(KEY_SALT, profile.passwordSalt)
                    .apply();
            callback.onSuccess();
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    /** Copies the picked image into internal storage as the profile photo. */
    public void uploadProfilePhoto(Uri imageUri, UploadCallback callback) {
        try {
            File destFile = new File(context.getFilesDir(), PHOTO_FILE_NAME);
            try (InputStream in = context.getContentResolver().openInputStream(imageUri);
                 OutputStream out = new FileOutputStream(destFile)) {
                if (in == null) throw new IOException("Could not open selected image");
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            callback.onSuccess(destFile.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e);
        }
    }

    /** Loads the saved profile photo as a Bitmap, or null if none is set / it fails to decode. */
    public Bitmap loadProfilePhotoBitmap() {
        String path = getCachedPhotoUrl();
        if (path.isEmpty()) return null;
        return BitmapFactory.decodeFile(path);
    }
}
