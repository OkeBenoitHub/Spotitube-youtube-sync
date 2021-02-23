package com.syncrotube.www.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.syncrotube.www.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared Preferences Util :: contain every recurring task dealing shared preferences
 */
final public class SharedPrefUtil {
    private static SharedPreferences mSharedPreferences;

    /**
     * Set up shared preferences
     * @param context :: context
     */
    private static void setUpSharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                context.getString(R.string.package_name_text), Context.MODE_PRIVATE);
    }

    /**
     * Write string data to preferences
     * @param keyValue :: key value
     * @param value :: value to be stored
     */
    public static void writeDataStringToSharedPreferences(Context context, String keyValue, String value) {
        setUpSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(keyValue,value);
        editor.apply();
    }

    /**
     * Get string data from preferences
     * @param keyValue :: key value
     * @return data string
     */
    public static String getDataStringFromSharedPreferences(Context context, String keyValue) {
        setUpSharedPreferences(context);
        return mSharedPreferences.getString(keyValue,"");
    }

    /**
     * Write int data to preferences
     * @param keyValue :: data key value
     * @param value :: int value to be stored
     */
    public static void writeDataIntToSharedPreferences(Context context, String keyValue, int value) {
        setUpSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(keyValue,value);
        editor.apply();
    }

    /**
     * Get int data from preferences
     * @param keyValue :: key for preference
     * @return data int
     */
    public static int getDataIntFromSharedPreferences(Context context, String keyValue) {
        setUpSharedPreferences(context);
        return mSharedPreferences.getInt(keyValue,0);
    }

    /**
     * Write boolean data to preferences
     * @param keyValue :: boolean key data
     * @param value :: boolean data to be stored
     */
    public static void writeDataBooleanToSharedPreferences(Context context, String keyValue, boolean value) {
        setUpSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(keyValue,value);
        editor.apply();
    }

    /**
     * Get boolean data from preferences
     * @param keyValue :: key for preference
     * @return boolean data
     */
    public static boolean getDataBooleanFromSharedPreferences(Context context, String keyValue) {
        setUpSharedPreferences(context);
        return mSharedPreferences.getBoolean(keyValue,false);
    }

    /**
     * Write array list data to preferences
     * @param keyValue :: key of array list data
     * @param arrayListValue :: array list value to be stored
     */
    public static void writeDataArrayListStringToSharedPreferences(Context context, String keyValue, ArrayList<String> arrayListValue) {
        setUpSharedPreferences(context);
        Set<String> setValue = new HashSet<>(arrayListValue);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(keyValue,setValue);
        editor.apply();
    }

    /**
     * Get array list data from preferences
     * @param keyValue :: key for preference
     * @return array list data
     */
    public static Set<String> getDataArrayListStringFromSharedPreferences(Context context, String keyValue) {
        setUpSharedPreferences(context);
        return mSharedPreferences.getStringSet(keyValue,null);
    }

    /**
     * Delete all data from preferences
     */
    public static void clearAllPreferencesData(Context context) {
        setUpSharedPreferences(context);
        mSharedPreferences.edit().clear().apply();
    }

    /**
     * Delete a specific preference by key value
     * @param keyName :: key for preference
     */
    public static void clearPreferenceDataByKey(Context context, String keyName) {
        setUpSharedPreferences(context);
        mSharedPreferences.edit().remove(keyName).apply();
    }
}
