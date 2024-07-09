package com.conv.HealthETrain.utils;

import cn.hutool.core.date.DateTime;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UniqueIdGenerator {
    public static String generateUniqueId(String userId, String videoId) {
        String combined = userId + ":" + videoId + DateTime.now();
        UUID uuid = UUID.nameUUIDFromBytes(combined.getBytes(StandardCharsets.UTF_8));
        return uuid.toString();
    }
}
