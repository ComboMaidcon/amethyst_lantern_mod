package com.example.amethystlantern.item;

import com.example.amethystlantern.AmethystLanternMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * AmethystLanternItem - Đèn lồng thạch anh tím huyền bí
 *
 * Khi được trang bị vào slot "charm" (Curios API),
 * vật phẩm này sẽ ổn định thân nhiệt người chơi trong
 * khoảng an toàn (Tough As Nails integration).
 *
 * Cơ chế:
 * - Mỗi tick khi đang đeo, kiểm tra nhiệt độ hiện tại của player
 * - Nếu nhiệt độ vượt ngưỡng an toàn, kéo về giới hạn
 * - Sử dụng Tough As Nails TemperatureCapability để set nhiệt độ
 */
public class AmethystLanternItem extends Item implements ICurioItem {

    // Ngưỡng nhiệt độ an toàn của Tough As Nails
    // TAN dùng thang từ 0.0 (cực lạnh) đến 1.0 (cực nóng)
    // Vùng an toàn thường là 0.3 - 0.7
    private static final float TEMP_SAFE_MIN = 0.30f;
    private static final float TEMP_SAFE_MAX = 0.70f;
    private static final float TEMP_ADJUST_RATE = 0.002f; // Tốc độ điều chỉnh mỗi tick

    // Đèn lồng tiêu thụ durability khi hoạt động (tùy chọn)
    private static final int DURABILITY_DRAIN_INTERVAL = 6000; // ~5 phút/1 durability

    public AmethystLanternItem(Properties properties) {
        super(properties);
    }

    /**
     * Được gọi mỗi tick khi item đang được đeo trong slot Curios
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();

        // Chỉ xử lý phía server
        if (player.level().isClientSide()) return;

        // Kiểm tra Tough As Nails có được cài không
        if (!ModList.get().isLoaded("toughasnails")) return;

        // Delegate sang helper class để tránh classloading error khi TAN không có
        ToughAsNailsHelper.lockTemperature(player);
    }

    /**
     * Hiệu ứng khi lần đầu đeo vào
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        var player = slotContext.entity();
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(
                Component.literal("✦ Đèn Lồng Thạch Anh tỏa ra hơi ấm huyền bí...")
                    .withStyle(ChatFormatting.LIGHT_PURPLE)
            );
        }
    }

    /**
     * Hiệu ứng khi tháo ra
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        var player = slotContext.entity();
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(
                Component.literal("✦ Hơi ấm bảo vệ tan biến dần...")
                    .withStyle(ChatFormatting.GRAY)
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(
            Component.literal("Miễn dịch hoàn toàn với thay đổi thân nhiệt")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
        );
        tooltip.add(
            Component.literal("Trang bị: Belt slot")
                .withStyle(ChatFormatting.GRAY)
        );

        if (ModList.get().isLoaded("toughasnails")) {
            tooltip.add(
                Component.literal("✔ Tương thích Tough As Nails")
                    .withStyle(ChatFormatting.GREEN)
            );
        } else {
            tooltip.add(
                Component.literal("✘ Cần Tough As Nails mod")
                    .withStyle(ChatFormatting.RED)
            );
        }
    }
}
