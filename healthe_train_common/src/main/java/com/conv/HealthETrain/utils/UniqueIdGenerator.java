package com.conv.HealthETrain.utils;

import cn.hutool.core.date.DateTime;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UniqueIdGenerator {
    public static String generateUniqueId(String key1, String key2) {
        String combined = key1 + ":" + key2 ;
        UUID uuid = UUID.nameUUIDFromBytes(combined.getBytes(StandardCharsets.UTF_8));
        return uuid.toString();
    }
}
