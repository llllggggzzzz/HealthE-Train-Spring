package com.conv.HealthETrain.utils;

import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author liusg
 */
@Component
public class CodeUtil {
    private final long expiredTime = TimeUnit.MINUTES.toMillis(5);
    private final ConcurrentHashMap<String, String> codeStorage = new ConcurrentHashMap<>();

    public void storeCode(String key ,String code) {
        codeStorage.put(key, code);
        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                codeStorage.remove(key);
            }
        }, expiredTime);
    }

    public boolean verifyCode(String key, String code) {
        String storeCode = codeStorage.get(key);
        return storeCode != null && storeCode.equalsIgnoreCase(code);
    }
}
