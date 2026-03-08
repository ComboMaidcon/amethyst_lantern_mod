package com.example.amethystlantern.item;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

import java.lang.reflect.Method;

/**
 * Tích hợp Tough As Nails hoàn toàn qua reflection.
 *
 * Lý do dùng PlayerTickEvent thay vì curioTick:
 * TAN 9.x tính lại nhiệt độ từ môi trường mỗi tick, sau đó mới commit kết quả.
 * Nếu set trong curioTick (Phase.START hoặc giữa tick), TAN sẽ overwrite ngay.
 * Dùng Phase.END đảm bảo code chạy SAU KHI TAN đã commit → giá trị không bị đè.
 */
public class ToughAsNailsHelper {

    private static final Logger LOGGER = LogManager.getLogger("AmethystLantern");
    private static final int TEMP_NEUTRAL = 7; // Thang 0-14, 7 = trung tính

    private static volatile Boolean tanPresent = null;
    private static volatile boolean initialized = false;

    // Reflection cache
    private static Method m_getTemperatureData;
    private static Method m_getTemperature;
    private static Method m_setTemperature;

    /**
     * Gọi từ AmethystLanternItem.curioTick() chỉ để verify TAN có mặt.
     * Logic thực tế nằm trong onPlayerTick bên dưới.
     */
    public static void lockTemperature(Player player) {
        // No-op: xử lý qua event
    }

    public static boolean isTANPresent() {
        if (tanPresent == null) {
            tanPresent = ModList.get().isLoaded("toughasnails");
            if (tanPresent) LOGGER.info("[AmethystLantern] TAN detected.");
            else LOGGER.info("[AmethystLantern] TAN not found.");
        }
        return tanPresent;
    }

    /**
     * Chạy cuối mỗi tick, SAU KHI TAN đã tính nhiệt độ xong.
     * Nếu player đang đeo Amethyst Lantern trong belt slot → kéo nhiệt về 7.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (!isTANPresent()) return;

        // Kiểm tra có đeo Amethyst Lantern trong Curios belt không
        boolean hasLantern = CuriosApi.getCuriosHelper()
            .findFirstCurio(player, com.example.amethystlantern.registry.ModItems.AMETHYST_LANTERN.get())
            .isPresent();

        if (!hasLantern) return;

        if (!initialized) initReflection();
        if (!initialized) return;

        try {
            Object tempData = m_getTemperatureData.invoke(null, player);
            if (tempData == null) return;
            int current = (int) m_getTemperature.invoke(tempData);
            if (current != TEMP_NEUTRAL) {
                m_setTemperature.invoke(tempData, TEMP_NEUTRAL);
            }
        } catch (Exception e) {
            LOGGER.warn("[AmethystLantern] Temperature lock error: {}", e.getMessage());
            tanPresent = false;
        }
    }

    private static synchronized void initReflection() {
        if (initialized) return;
        try {
            Class<?> helper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            m_getTemperatureData = helper.getMethod("getTemperatureData", Player.class);
            Class<?> iface = Class.forName("toughasnails.api.temperature.ITemperatureData");
            m_getTemperature = iface.getMethod("getTemperature");
            m_setTemperature = iface.getMethod("setTemperature", int.class);
            initialized = true;
            LOGGER.info("[AmethystLantern] TAN reflection init OK.");
        } catch (Exception e) {
            LOGGER.warn("[AmethystLantern] TAN reflection init failed: {}", e.getMessage());
            initialized = true; // đánh dấu đã thử, không retry
        }
    }
}
