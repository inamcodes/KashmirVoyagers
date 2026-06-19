package com.inam.kashtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * Gatekeeper shown when the user taps the Profile tab. If no password is
 * set yet, it passes straight through to ProfileActivity. If a password
 * is set, it asks for it (checked against the locally cached hash, so it
 * works offline) before allowing entry.
 */
public class ProfileGateActivity extends AppCompatActivity {

    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileRepository = new ProfileRepository(this);

        if (!profileRepository.isPasswordSetCached()) {
            // No password set — go straight in.
            openProfile();
            finish();
            return;
        }

        setContentView(R.layout.activity_profile_gate);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ImageButton btnBack = findViewById(R.id.btnGateBack);
        EditText etPassword = findViewById(R.id.etGatePassword);
        ImageButton btnTogglePassword = findViewById(R.id.btnTogglePassword);
        MaterialButton btnUnlock = findViewById(R.id.btnUnlock);
        TextView tvGateError = findViewById(R.id.tvGateError);

        btnBack.setOnClickListener(v -> finish());

        final boolean[] passwordVisible = {false};
        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible[0] = !passwordVisible[0];
            etPassword.setInputType(passwordVisible[0]
                    ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnUnlock.setOnClickListener(v -> {
            String entered = etPassword.getText().toString();
            if (entered.isEmpty()) {
                tvGateError.setText("Enter your password");
                tvGateError.setVisibility(TextView.VISIBLE);
                return;
            }
            if (profileRepository.verifyPasswordCached(entered)) {
                tvGateError.setVisibility(TextView.GONE);
                openProfile();
                finish();
            } else {
                tvGateError.setText("Incorrect password");
                tvGateError.setVisibility(TextView.VISIBLE);
            }
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            btnUnlock.performClick();
            return true;
        });
    }

    private void openProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
