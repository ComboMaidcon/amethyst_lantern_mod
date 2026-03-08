package com.example.amethystlantern.item;

import com.example.amethystlantern.AmethystLanternMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class AmethystLanternItem extends Item implements ICurioItem {

    public AmethystLanternItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        if (player.level().isClientSide()) return;
        if (!ModList.get().isLoaded("toughasnails")) return;
        ToughAsNailsHelper.lockTemperature(player);
    }

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
        // Dòng kẻ trên — style Enigmatic Legacy
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        // Slot info
        MutableComponent slotLine = Component.literal("  ⬡ ")
            .withStyle(ChatFormatting.GOLD)
            .append(Component.literal("Curios Belt Slot")
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(slotLine);

        // Dòng kẻ giữa
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        // Effect chính
        tooltip.add(Component.literal("  ❄ ")
            .withStyle(ChatFormatting.AQUA)
            .append(Component.literal("Điều hòa thân nhiệt")
                .withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("    Khóa nhiệt độ về vùng an toàn")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("    mỗi tick khi đang đeo.")
            .withStyle(ChatFormatting.GRAY));

        // TAN status
        tooltip.add(Component.empty());
        if (ModList.get().isLoaded("toughasnails")) {
            tooltip.add(Component.literal("  ✔ Tough As Nails: ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal("Đang hoạt động")
                    .withStyle(ChatFormatting.WHITE)));
        } else {
            tooltip.add(Component.literal("  ✘ Tough As Nails: ")
                .withStyle(ChatFormatting.RED)
                .append(Component.literal("Không tìm thấy")
                    .withStyle(ChatFormatting.GRAY)));
        }

        // Dòng kẻ dưới
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
