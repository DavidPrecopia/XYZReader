package com.example.xyzreader.util;

import android.text.Html;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public final class FormatDate {
    private FormatDate() {
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.sss";

    public static String getFormattedDate(String date) {
        return Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                        parsePublishedDate(date),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL
                ).toString()
        ).toString();
    }

    private static long parsePublishedDate(String publishedDate) {
        try {
            return new SimpleDateFormat(DATE_FORMAT,Locale.US)
                    .parse(publishedDate)
                    .getTime();
        } catch (ParseException ex) {
            Timber.e(ex);
            Timber.i("passing today's date");
            return new Date().getTime();
        }
    }
}
