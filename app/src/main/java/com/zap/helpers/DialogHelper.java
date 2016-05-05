package com.zap.helpers;

import android.app.Activity;
import android.app.ProgressDialog;

import com.zap.R;

/**
 * This class contains methods to help build and display various dialogs
 */
public final class DialogHelper {

    /**
     * Builds, shows, and returns a progress dialog.
     *
     * @param activity activity displaying dialog
     * @param title title to display
     * @param message message to display
     * @return the built progress dialog
     */
    public static ProgressDialog buildAndShowProgressDialog(Activity activity, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }
}
