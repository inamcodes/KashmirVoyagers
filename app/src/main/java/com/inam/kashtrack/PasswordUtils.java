package com.inam.kashtrack;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Salts + hashes the profile password with PBKDF2 so the raw password is
 * never written to Firestore. Each call to hash() generates a fresh
 * random salt; verify() re-derives the hash with the stored salt and
 * compares.
 */
public class PasswordUtils {

    private static final int ITERATIONS = 12000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static class HashResult {
        public final String hash;
        public final String salt;

        HashResult(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }

    public static HashResult hash(String password) {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP);
        String hash = deriveHash(password, salt);
        return new HashResult(hash, salt);
    }

    public static boolean verify(String password, String storedHash, String storedSalt) {
        if (storedHash == null || storedSalt == null) return false;
        String derived = deriveHash(password, storedSalt);
        return derived.equals(storedHash);
    }

    private static String deriveHash(String password, String salt) {
        try {
            byte[] saltBytes = Base64.decode(salt, Base64.NO_WRAP);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
