package com.example.amethystlantern.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Tích hợp Tough As Nails hoàn toàn qua reflection.
 * Không import bất kỳ class TAN nào → compile được dù TAN không có trong classpath.
 */
public class ToughAsNailsHelper {

    private static final Logger LOGGER = LogManager.getLogger("AmethystLantern");
    private static final int TEMP_NEUTRAL = 7;

    private static volatile Boolean tanPresent  = null;
    private static volatile boolean initialized = false;
    private static volatile boolean useFallback = false;

    // TAN 9.x
    private static Method m_getTemperatureData;
    private static Method m_getTemperature;
    private static Method m_setTemperature;

    // TAN 4.x fallback
    private static Object tan4_capability;
    private static Method m_getCapability;
    private static Method m_getRawValue;
    private static Method m_setTemp4;

    public static void lockTemperature(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (!isTANPresent()) return;
        if (!initialized) initReflection();
        if (!initialized || !tanPresent) return;

        try {
            if (!useFallback) {
                lockTAN9(player);
            } else {
                lockTAN4(player);
            }
        } catch (Exception e) {
            LOGGER.warn("[AmethystLantern] TAN lock error: {} - disabling.", e.getMessage());
            tanPresent = false;
        }
    }

    private static void lockTAN9(Player player) throws Exception {
        Object tempData = m_getTemperatureData.invoke(null, player);
        if (tempData == null) return;
        int current = (int) m_getTemperature.invoke(tempData);
        if (current != TEMP_NEUTRAL) {
            m_setTemperature.invoke(tempData, TEMP_NEUTRAL);
        }
    }

    private static void lockTAN4(Player player) throws Exception {
        Object lazyOpt = m_getCapability.invoke(player, tan4_capability);
        Method orElse = lazyOpt.getClass().getMethod("orElse", Object.class);
        Object tempCap = orElse.invoke(lazyOpt, (Object) null);
        if (tempCap == null) return;
        int current = (int) m_getRawValue.invoke(tempCap);
        if (current != TEMP_NEUTRAL) {
            Class<?> tempClass = Class.forName("toughasnails.api.temperature.Temperature");
            Object newTemp = tempClass.getDeclaredConstructor(int.class).newInstance(TEMP_NEUTRAL);
            m_setTemp4.invoke(tempCap, newTemp);
        }
    }

    private static boolean isTANPresent() {
        if (tanPresent == null) {
            tanPresent = isClassPresent("toughasnails.api.temperature.TemperatureHelper")
                      || isClassPresent("toughasnails.api.TANCapabilities");
            if (tanPresent) LOGGER.info("[AmethystLantern] TAN detected - temperature lock active.");
            else LOGGER.info("[AmethystLantern] TAN not found - temperature lock inactive.");
        }
        return tanPresent;
    }

    private static synchronized void initReflection() {
        if (initialized) return;
        try {
            if (tryInitTAN9()) {
                LOGGER.info("[AmethystLantern] TAN 9.x init OK.");
            } else if (tryInitTAN4()) {
                useFallback = true;
                LOGGER.info("[AmethystLantern] TAN 4.x init OK.");
            } else {
                LOGGER.warn("[AmethystLantern] TAN init failed.");
                tanPresent = false;
            }
        } catch (Exception e) {
            LOGGER.warn("[AmethystLantern] TAN init exception: {}", e.getMessage());
            tanPresent = false;
        }
        initialized = true;
    }

    private static boolean tryInitTAN9() {
        try {
            Class<?> helper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            m_getTemperatureData = helper.getMethod("getTemperatureData", Player.class);
            Class<?> iface = Class.forName("toughasnails.api.temperature.ITemperatureData");
            m_getTemperature = iface.getMethod("getTemperature");
            m_setTemperature = iface.getMethod("setTemperature", int.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean tryInitTAN4() {
        try {
            Class<?> capClass = Class.forName("toughasnails.api.TANCapabilities");
            Field capField = capClass.getField("TEMPERATURE");
            tan4_capability = capField.get(null);
            m_getCapability = Player.class.getMethod("getCapability", Capability.class);
            Class<?> iTempClass = Class.forName("toughasnails.api.temperature.ITemperature");
            m_getRawValue = iTempClass.getMethod("getRawValue");
            m_setTemp4 = iTempClass.getMethod("setTemperature",
                    Class.forName("toughasnails.api.temperature.ITemperature"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isClassPresent(String className) {
        try { Class.forName(className); return true; }
        catch (ClassNotFoundException e) { return false; }
    }
}
