package com.github.fearmygaze.mercury.util;

import android.os.Handler;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Tools {

    public static void setErrorToLayout(TextInputLayout layout, String message, boolean enabled) {
        layout.setErrorEnabled(enabled);
        layout.setError(message);
    }

    public static void setTimedErrorToLayout(TextInputLayout layout, String message, boolean enabled, int ms) {
        setErrorToLayout(layout, message, enabled);
        new Handler().postDelayed(() -> setErrorToLayout(layout, null, false), ms);
    }

    public static void closeKeyboard() {

    }

    public static void openKeyboard() {

    }

    public static String setDateFormat(long time) {// TODO: Show the correct timestamp and show to Short Version or the Long Version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm a");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return String.format("%s, %s", DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()), DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        }
    }


}
