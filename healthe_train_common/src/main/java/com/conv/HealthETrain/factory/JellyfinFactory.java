package com.conv.HealthETrain.factory;

import com.conv.HealthETrain.utils.JellyfinUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JellyfinFactory {
    private JellyfinFactory () {};
    public static JellyfinUtil build(String JellyfinConfigPath) {
        try {
            // 通过反射获取私有构造函数
            Constructor<JellyfinUtil> constructor = JellyfinUtil.class.getDeclaredConstructor(String.class);
            // 设置可访问性
            constructor.setAccessible(true);
            // 创建实例
            return constructor.newInstance(JellyfinConfigPath);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("创建JellyfinUtil失败", e);
        }

    }
}
