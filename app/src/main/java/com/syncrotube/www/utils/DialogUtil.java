package com.syncrotube.www.utils;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.syncrotube.www.R;

/**
 * Dialog Util :: contain every recurring task dealing with Android Dialog
 */
final public class DialogUtil {
    private static ProgressDialog mLoaderDialog;

    public interface showBasicAlertDialogCallback {
        void onShowBasicAlertDialog(boolean isPositiveBtnTapped);
    }
    /**
     * Show alert dialog
     * @param context :: context
     * @param title :: dialog title
     * @param contentMessage :: dialog content message
     * @param positiveBtnText :: dialog positive button text
     * @param negativeBtnText :: dialog negative button text
     */
    public static void showBasicAlertDialog(Context context , String title, String contentMessage, String positiveBtnText, String negativeBtnText,boolean isCancelTapOutside, showBasicAlertDialogCallback showBasicAlertDialogCallback) {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert);

        // Set a title for alert dialog
        builder.setTitle(title);
        // Ask the final question
        builder.setMessage(contentMessage);
        builder.setCancelable(isCancelTapOutside);

        // Set the alert dialog yes button click listener
        builder.setPositiveButton(positiveBtnText, (dialog, which) -> {
            // Do something when user clicked the Yes button
            showBasicAlertDialogCallback.onShowBasicAlertDialog(true);
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton(negativeBtnText, (dialog, which) -> {
            // Do something when No button clicked
            showBasicAlertDialogCallback.onShowBasicAlertDialog(false);
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    /**
     * Show progress dialog
     * @param context :: context
     * @param message :: message dialog
     */
    public static void showProgressDialog(Context context, String message, boolean isCancelableTapOutside) {
        if (mLoaderDialog == null) {
            mLoaderDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            if (message != null)
                mLoaderDialog.setMessage(message);
            if (!isCancelableTapOutside) {
                mLoaderDialog.setCancelable(false);
                mLoaderDialog.setCanceledOnTouchOutside(false);
            }
        }
        mLoaderDialog.show();
    }

    /**
     * Hide progress dialog
     * @param context :: context
     */
    public static void hideProgressDialog(Context context) {
        if (mLoaderDialog == null) {
            mLoaderDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        }
        mLoaderDialog.hide();
    }
}
