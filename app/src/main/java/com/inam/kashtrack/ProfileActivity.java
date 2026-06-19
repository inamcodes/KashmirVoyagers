package com.inam.kashtrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.material.button.MaterialButton;

/**
 * "Profile and more" screen: shows the user's name + photo, lets them edit
 * both, and manage password protection (set / change / remove).
 */
public class ProfileActivity extends AppCompatActivity {

    private ProfileRepository profileRepository;
    private UserProfile currentProfile = new UserProfile();
    private Uri pendingPhotoUri = null;

    private ImageView ivProfilePhoto;
    private TextView tvProfileName;
    private TextView tvPasswordStatus;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pendingPhotoUri = uri;
                    uploadAndSavePhoto(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        profileRepository = new ProfileRepository(this);

        ImageButton btnBack = findViewById(R.id.btnProfileBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ImageButton btnEditPhoto = findViewById(R.id.btnEditPhoto);
        tvProfileName = findViewById(R.id.tvProfileName);
        ImageButton btnEditName = findViewById(R.id.btnEditName);
        tvPasswordStatus = findViewById(R.id.tvPasswordStatus);
        MaterialButton btnSetPassword = findViewById(R.id.btnSetPassword);
        MaterialButton btnChangePassword = findViewById(R.id.btnChangePassword);
        MaterialButton btnRemovePassword = findViewById(R.id.btnRemovePassword);

        btnBack.setOnClickListener(v -> finish());
        btnEditPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        ivProfilePhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnEditName.setOnClickListener(v -> showEditNameDialog());

        btnSetPassword.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        btnChangePassword.setOnClickListener(v -> showSetPasswordDialog(true));
        btnRemovePassword.setOnClickListener(v -> confirmRemovePassword());

        loadProfile();
    }

    private void loadProfile() {
        // Local storage is synchronous, so this is always fully up to date.
        tvProfileName.setText(profileRepository.getCachedName().isEmpty()
                ? "Tap to set your name" : profileRepository.getCachedName());
        showPhotoFromPath(profileRepository.getCachedPhotoUrl());
        refreshPasswordStatusUi(profileRepository.isPasswordSetCached());

        profileRepository.loadProfile(new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                currentProfile = profile;
                tvProfileName.setText(profile.name == null || profile.name.isEmpty()
                        ? "Tap to set your name" : profile.name);
                showPhotoFromPath(profile.photoUrl);
                refreshPasswordStatusUi(profile.hasPassword());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this,
                        "Couldn't load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Loads a local image file path into the profile ImageView as a circular bitmap. */
    private void showPhotoFromPath(String path) {
        if (path == null || path.isEmpty()) {
            ivProfilePhoto.setImageResource(R.drawable.ic_nav_placeholder);
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            ivProfilePhoto.setImageResource(R.drawable.ic_nav_placeholder);
            return;
        }
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCircular(true);
        ivProfilePhoto.setImageDrawable(drawable);
    }

    private void refreshPasswordStatusUi(boolean hasPassword) {
        tvPasswordStatus.setText(hasPassword
                ? "Password protection is ON"
                : "Password protection is OFF");
        findViewById(R.id.btnSetPassword).setVisibility(hasPassword ? android.view.View.GONE : android.view.View.VISIBLE);
        findViewById(R.id.btnChangePassword).setVisibility(hasPassword ? android.view.View.VISIBLE : android.view.View.GONE);
        findViewById(R.id.btnRemovePassword).setVisibility(hasPassword ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void showEditNameDialog() {
        EditText input = new EditText(this);
        input.setHint("Your name");
        input.setText(currentProfile.name);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        new AlertDialog.Builder(this)
                .setTitle("Edit name")
                .setView(wrapWithPadding(input))
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentProfile.name = newName;
                    tvProfileName.setText(newName);
                    saveProfileSilently();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles both "set password" (no existing password) and "change
     * password" (must confirm the current one first) flows in a single
     * dialog stack.
     */
    private void showSetPasswordDialog(boolean isChange) {
        if (isChange) {
            EditText currentInput = new EditText(this);
            currentInput.setHint("Current password");
            currentInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            new AlertDialog.Builder(this)
                    .setTitle("Confirm current password")
                    .setView(wrapWithPadding(currentInput))
                    .setPositiveButton("Next", (dialog, which) -> {
                        String entered = currentInput.getText().toString();
                        if (!profileRepository.verifyPasswordCached(entered)) {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showNewPasswordDialog();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            showNewPasswordDialog();
        }
    }

    private void showNewPasswordDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText newPassword = new EditText(this);
        newPassword.setHint("New password");
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText confirmPassword = new EditText(this);
        confirmPassword.setHint("Confirm new password");
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = (int) (12 * getResources().getDisplayMetrics().density);

        layout.addView(newPassword);
        layout.addView(confirmPassword, lp);

        new AlertDialog.Builder(this)
                .setTitle("Set new password")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String pw1 = newPassword.getText().toString();
                    String pw2 = confirmPassword.getText().toString();
                    if (pw1.length() < 4) {
                        Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!pw1.equals(pw2)) {
                        Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PasswordUtils.HashResult result = PasswordUtils.hash(pw1);
                    currentProfile.passwordHash = result.hash;
                    currentProfile.passwordSalt = result.salt;
                    saveProfileSilently();
                    refreshPasswordStatusUi(true);
                    Toast.makeText(this, "Password saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmRemovePassword() {
        EditText currentInput = new EditText(this);
        currentInput.setHint("Current password");
        currentInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Remove password protection?")
                .setMessage("Confirm your current password to remove protection.")
                .setView(wrapWithPadding(currentInput))
                .setPositiveButton("Remove", (dialog, which) -> {
                    String entered = currentInput.getText().toString();
                    if (!profileRepository.verifyPasswordCached(entered)) {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentProfile.passwordHash = null;
                    currentProfile.passwordSalt = null;
                    saveProfileSilently();
                    refreshPasswordStatusUi(false);
                    Toast.makeText(this, "Password protection removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void uploadAndSavePhoto(Uri uri) {
        profileRepository.uploadProfilePhoto(uri, new ProfileRepository.UploadCallback() {
            @Override
            public void onSuccess(String localPath) {
                currentProfile.photoUrl = localPath;
                showPhotoFromPath(localPath);
                saveProfileSilently();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this,
                        "Couldn't save photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileSilently() {
        profileRepository.saveProfile(currentProfile, new ProfileRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                // No-op: UI was already updated optimistically.
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this,
                        "Couldn't save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private android.view.View wrapWithPadding(android.view.View view) {
        android.widget.FrameLayout frame = new android.widget.FrameLayout(this);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        frame.setPadding(pad, pad, pad, pad);
        frame.addView(view);
        return frame;
    }
}
