package com.github.fnwib.util;

import java.util.UUID;

public class UUIDUtils {

    private UUIDUtils() {
    }

    public static String getId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    public static String getHalfId() {
        return getId().substring(16);
    }
}