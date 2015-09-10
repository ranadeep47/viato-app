package in.viato.app.utils;

import android.content.Context;

import java.util.Date;
import java.util.TimeZone;

import in.viato.app.R;

/**
 * Created by ranadeep on 11/09/15.
 */
public class TimeUtils {

    public int getUtcOffset() {
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        int offsetFromUtc = tz.getOffset(now.getTime()) / 1000;
        return offsetFromUtc;
    }

    public static int[] getHoursMinsSecs(long time) {
        int hours = (int) time / 3600;
        int remainder = (int) time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        return new int[]{hours, mins, secs};
    }

    public static long getCurrentEpochTime() {
        return System.currentTimeMillis() / 1000;
    }

    public String getElapsedTimeFormat(Context context,long timeEpoch) {

        long timeElapsed = getCurrentEpochTime() - timeEpoch;

        int[] timeValues = getHoursMinsSecs(timeElapsed);

        final int hours = timeValues[0];
        final int minutes = timeValues[1];
        final int seconds = timeValues[2];
        final int days = hours / 24;
        final int weeks = days / 7;


        if (hours < 1) {
            if (minutes < 1) {
                if (seconds < 10) {
                    return context.getString(R.string.just_now);
                } else {
                    return context.getString(R.string.seconds_ago, seconds);
                }

            } else {
                return context.getString(R.string.minutes_ago, minutes);
            }
        } else if (hours < 23) {
            return context.getString(R.string.hours_ago, hours);

        } else if (hours > 23 && hours < 167) {

            return context.getString(R.string.days_ago, days);


        } else if (weeks > 0) {
            return context.getString(R.string.weeks_ago, weeks);
        } else {
            return context.getString(R.string.long_time_ago);
        }

    }

}
