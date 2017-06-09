package com.lonelyprogrammer.forum.auth.utils;

import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
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
}
