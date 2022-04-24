package com.ybsystem.tweetmate.models.entities.twitter;

import com.ybsystem.tweetmate.databases.PrefSystem;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TwitterTimeSpanConverter {

    private static final int ONE_HOUR_IN_SECONDS = 60 * 60;
    private static final int ONE_DAY_IN_SECONDS = 24 * ONE_HOUR_IN_SECONDS;
    private static final int ONE_MONTH_IN_SECONDS = 30 * ONE_DAY_IN_SECONDS;
    private final MessageFormat[] formats = new MessageFormat[6];
    private final SimpleDateFormat dateMonth;
    private final SimpleDateFormat dateMonthYear;

    private static final int NOW = 0;
    private static final int N_SECONDS_AGO = 1;
    private static final int A_MINUTE_AGO = 2;
    private static final int N_MINUTES_AGO = 3;
    private static final int AN_HOUR_AGO = 4;
    private static final int N_HOURS_AGO = 5;

    public TwitterTimeSpanConverter() {
        if (PrefSystem.getLanguage().equals("en")) {
            formats[NOW] = new MessageFormat("now");
            formats[N_SECONDS_AGO] = new MessageFormat("{0} seconds");
            formats[A_MINUTE_AGO] = new MessageFormat("1 minute");
            formats[N_MINUTES_AGO] = new MessageFormat("{0} minutes");
            formats[AN_HOUR_AGO] = new MessageFormat("1 hour");
            formats[N_HOURS_AGO] = new MessageFormat("{0} hours");
            dateMonth = new SimpleDateFormat("MMM d", Locale.ENGLISH);
            dateMonthYear = new SimpleDateFormat("MMM d, yy", Locale.ENGLISH);
        } else {
            formats[NOW] = new MessageFormat("今");
            formats[N_SECONDS_AGO] = new MessageFormat("{0}秒");
            formats[A_MINUTE_AGO] = new MessageFormat("1分");
            formats[N_MINUTES_AGO] = new MessageFormat("{0}分");
            formats[AN_HOUR_AGO] = new MessageFormat("1時間");
            formats[N_HOURS_AGO] = new MessageFormat("{0}時間");
            dateMonth = new SimpleDateFormat("M/d", Locale.JAPANESE);
            dateMonthYear = new SimpleDateFormat("yy/M/d", Locale.JAPANESE);
        }
    }

    public String toTimeSpanString(Date date) {
        return toTimeSpanString(date.getTime());
    }

    public String toTimeSpanString(long milliseconds) {
        int deltaInSeconds = (int) ((System.currentTimeMillis() - milliseconds) / 1000);
        if (deltaInSeconds >= ONE_DAY_IN_SECONDS) {
            if (deltaInSeconds >= ONE_MONTH_IN_SECONDS) {
                return dateMonthYear.format(new Date(milliseconds));
            } else {
                return dateMonth.format(new Date(milliseconds));
            }
        }
        return toTimeSpanString(deltaInSeconds);
    }

    private String toTimeSpanString(int deltaInSeconds) {
        if (deltaInSeconds <= 1) {
            return formats[NOW].format(null);
        } else if (deltaInSeconds < 60) {
            return formats[N_SECONDS_AGO].format(new Object[]{deltaInSeconds});
        }

        if (deltaInSeconds < 45 * 60) {
            int minutes = deltaInSeconds / 60;
            if (minutes == 1) {
                return formats[A_MINUTE_AGO].format(null);
            }
            return formats[N_MINUTES_AGO].format(new Object[]{minutes});
        }

        if (deltaInSeconds < 105 * 60) { // between 0:45 and 1:45 => 1
            return formats[AN_HOUR_AGO].format(null);
        }
        int hours = ((deltaInSeconds + 15 * 60) / ONE_HOUR_IN_SECONDS);
        return formats[N_HOURS_AGO].format(new Object[]{hours});
    }

}
