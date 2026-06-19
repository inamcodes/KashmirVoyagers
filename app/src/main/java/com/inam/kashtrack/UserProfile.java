package com.inam.kashtrack;

/**
 * Mirrors a single document in the "profile" Firestore collection.
 * passwordHash/passwordSalt are null when no password is set.
 */
public class UserProfile {

    public String name = "";
    public String photoUrl = "";   // absolute local file path of the saved profile photo
    public String passwordHash = null;
    public String passwordSalt = null;

    public UserProfile() {
        // Required empty constructor for Firestore deserialization.
    }

    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isEmpty();
    }
}
