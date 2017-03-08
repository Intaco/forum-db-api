package com.lonelyprogrammer.forum.auth.utils;

import java.util.concurrent.atomic.AtomicLong;

public class AuthorizationUtils {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);


    //TODO Временное решение, в дальнейшем будем генерить ID в БД
    public static long generateUID() {
        return ID_GENERATOR.getAndIncrement();
    }
}
