package com.nirman.ledger.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nirman.ledger.model.Role;

/**
 * Manages JWT token and user session data in SharedPreferences.
 */
public class SessionManager {

    private static final String PREF_NAME = "nirman_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ROLE = "role";
    private static final String KEY_SELECTED_SITE_ID = "selected_site_id";
    private static final String KEY_SELECTED_SITE_NAME = "selected_site_name";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public void saveUser(Long id, String username, String email, String mobile, String role) {
        prefs.edit()
                .putLong(KEY_USER_ID, id)
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .putString(KEY_MOBILE, mobile)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getMobile() {
        return prefs.getString(KEY_MOBILE, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public boolean isContractor() {
        return Role.ROLE_CONTRACTOR.name().equals(getRole());
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public void setSelectedSite(Long siteId, String siteName) {
        prefs.edit()
                .putLong(KEY_SELECTED_SITE_ID, siteId)
                .putString(KEY_SELECTED_SITE_NAME, siteName)
                .apply();
    }

    public Long getSelectedSiteId() {
        long id = prefs.getLong(KEY_SELECTED_SITE_ID, -1);
        return id == -1 ? null : id;
    }

    public String getSelectedSiteName() {
        return prefs.getString(KEY_SELECTED_SITE_NAME, "Select Site");
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
