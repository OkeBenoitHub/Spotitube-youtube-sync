package com.syncrotube.www.utils;

import android.content.Context;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.syncrotube.www.R;

/**
 * Time Util :: contain every recurring task dealing with Time
 */
final public class TimeUtil {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * Set default time zone UTC
     */
    private static void setDefaultTimeZone() {
        DateTimeUtils.setTimeZone("UTC");
    }

    /**
     * Get time ago based on timestamp from past
     * @param time :: timestamp
     * @return :: explicit time ago
     */
    public static String getTimeAgo(Context context, long time) {
        setDefaultTimeZone();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.just_now_ago_text);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.minute_ago_text);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " " + context.getResources().getString(R.string.minutes_ago_text);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.hour_ago_text);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " " + context.getResources().getString(R.string.hours_ago_text);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.yesterday_text);
        } else {
            return diff / DAY_MILLIS + " " + context.getResources().getString(R.string.days_ago_text);
        }
    }
}
