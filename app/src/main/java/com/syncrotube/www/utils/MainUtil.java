package com.syncrotube.www.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.syncrotube.www.R;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main util :: contain every recurring simple general task
 */
final public class MainUtil {
    private static Toast mToast;
    private static String uniqueID = null;

    /**
     * Show toast message
     * @param context :: context
     * @param message :: message to show
     */
    public static void showToastMessage(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

    /**
     * Show snack bar message
     * @param context :: context
     * @param contextView :: context view
     * @param message :: message to show
     * @param textHexColor :: text color hexadecimal
     * @param bgResColor :: background resource color
     */
    public static void showSnackBarMessage(Context context, View contextView, String message, String textHexColor, int bgResColor) {
        String messageBox = "<font color='" + textHexColor + "'>" + message + "</font>";
        Snackbar snackbar = Snackbar.make(contextView, Html.fromHtml(messageBox), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(bgResColor));
        snackbar.show();
    }

    /**
     * Set Action Bar background color
     * @param context :: context
     * @param actionBar :: action bar
     * @param bgColorRes :: color int from resources
     */
    public static void setActionBarBackgroundColor(Context context, androidx.appcompat.app.ActionBar actionBar, int bgColorRes) {
        // Define ActionBar object;
        if (actionBar == null) return;
        // Define ColorDrawable object and color res int
        // with color int res code as its parameter
        ColorDrawable colorDrawable;
        colorDrawable = new ColorDrawable(context.getResources().getColor(bgColorRes));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    /**
     * Set status bar background color
     * @param context :: context
     * @param window :: activity window
     */
    public static void setStatusBarBackgroundColor(Context context, Window window, int bgColorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(context, bgColorRes));
            window.setNavigationBarColor(ContextCompat.getColor(context, bgColorRes));
        }
    }

    /**
     * This method is used for checking valid email id format.
     *
     * @param email to check for
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Compose email intent
     * @param addresses :: address to send email to
     * @param subject :: email subject
     */
    public static void composeEmail(Context context, String[] addresses, String subject, String message, String sharerTitle) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,message);
        Intent chooser = Intent.createChooser(intent,sharerTitle);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }

    /**
     * Capitalize each word from string
     * @param stringInput :: the string to transform
     * @return new string with each word capitalized
     */
    public static String capitalizeEachWordFromString(String stringInput){
        String[] strArray = stringInput.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap).append(" ");
        }
        return builder.toString();
    }

    /*
     * This method checks for a valid name :: contains only letters
     * @param name :: name input
     * @return true or false
     */
    public static boolean isValidName(String name) {
        String nameRegX = "^[\\p{L} .'-]+$";
        return name.matches(nameRegX);
    }

    /**
     * Open a specific web page
     * @param url :: page address url
     */
    public static void openWebPage(Context context, String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * Share Text data through app
     * @param shareAboutTitle :: title of share dialog
     * @param textToShare :: text data to share
     */
    public static void shareTextData(Context context, String shareAboutTitle, String textToShare) {
        String mimeType = "text/plain";

        // Use ShareCompat.IntentBuilder to build the Intent and start the chooser
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from((Activity) context)
                .setType(mimeType)
                .setChooserTitle(shareAboutTitle)
                .setText(textToShare)
                .startChooser();
    }

    /**
     * Share App
     * @param context :: context
     */
    public static void shareApp(Context context) {
        String aboutAppText = context.getString(R.string.about_app_text);
        aboutAppText += "\n";
        aboutAppText += context.getString(R.string.about_app_share_3) + "\n";
        aboutAppText += context.getString(R.string.app_play_store_link);
        shareTextData(context, context.getString(R.string.share_app_via_text), aboutAppText);
    }

    /**
     * Report an issue with App
     * @param context :: context
     * @param emailSubjects :: subjects email
     */
    public static void sendUserIssueReport(Context context, String[] emailSubjects) {
        composeEmail(
                context,
                emailSubjects, context.getString(R.string.report_issue_item_text),
                context.getString(R.string.what_went_wrong_text),
                context.getString(R.string.send_report_via_text));
    }

    /**
     * Send user feedback
     * @param context :: context
     * @param emailSubjects :: subjects email
     */
    public static void sendUserFeedback(Context context, String[] emailSubjects) {
        MainUtil.composeEmail(
                context,
                emailSubjects,
                context.getString(R.string.feedback_menu_item_text),
                context.getString(R.string.give_us_feedback_text),
                context.getString(R.string.send_feedback_via_text));
    }

    /**
     * Get unique android device id
     * @param context :: context
     * @return :: return unique id
     */
    public static synchronized String getUniqueID(Context context) {
        if (uniqueID == null) {
            String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

    /**
     * Check if app is in night mode
     * @param context :: context
     * @return true or false
     */
    public static boolean isAppInNightMode(Context context) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
