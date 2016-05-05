package com.zap.models;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.zap.activities.LoginActivity;

/**
 * This class is used to hold all data relevant to the entire session (authentication information,
 * etc.) This class also contains methods relevant to session workflows, for easy access throughout
 * the app.
 */
public final class Session {
    public static User user;
    public static MobileServiceClient mClient;

    /**
     * Returns whether the user's token with facebook is valid
     * @return true if the token is valid, false otherwise
     */
    public static boolean isFacebookSessionValid() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    /**
     * Returns whether all session values are valid
     * @return true if all values are valid, false otherwise
     */
    private static boolean isValidSession() {
        if (AccessToken.getCurrentAccessToken() != null
                && user != null
                && mClient != null) {
            return true;
        }
        return false;
    }

    /**
     * Verifies that there is a valid AccessToken with facebook and that session variables are
     * loaded. If not, the login activity is started.
     *
     * @param activity the activity performing the verification
     */
    public static void verifySession(Activity activity) {
        if (!isValidSession()) {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    /**
     * Performs a logout, then starts the login activity.
     *
     * @param activity the activity performing the logout
     */
    public static void doLogout(Activity activity) {
        // Logout logic
        LoginManager.getInstance().logOut();
        Session.user = null;
        Session.mClient = null;

        // Start login activity
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Instantiates a client to interact with the azure mobile service backend.
     *
     * @param activity the activity performing the instantiation
     * @return true on success, false on failure
     */
    public static boolean instantiateBackendClient(Activity activity) {
        try {
            Session.mClient = new MobileServiceClient(
                    "https://zap.azure-mobile.net/",
                    "gOXIOvUOmvTWGEknrVggIjHFdmzycc64",
                    activity.getApplicationContext()
            );
        } catch (Exception e) {

            // TODO: Proper error handling

            e.printStackTrace();
            return false;
        }

        return true;
    }
}
