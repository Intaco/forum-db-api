package com.lonelyprogrammer.forum.auth.utils;

import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by nikita on 09.06.17.
 */
public class TimeUtil {
    @Nullable
    public static Timestamp timestampFromString(@Nullable String source){
        Timestamp sinceTime = null;
        if (source != null) {
            final String formatted = ZonedDateTime.parse(source).format(DateTimeFormatter.ISO_INSTANT);
            sinceTime = new Timestamp(ZonedDateTime.parse(formatted).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        return sinceTime;
    }
    @Nullable
    public static String stringFromTimestamp(@Nullable Timestamp ts){
        if (ts == null) return null;
        return ts.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
