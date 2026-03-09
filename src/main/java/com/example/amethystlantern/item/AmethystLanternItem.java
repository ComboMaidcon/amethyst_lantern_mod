package com.example.amethystlantern.item;

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

    // curioTick không cần nữa — TAN được xử lý qua PlayerTickEvent Phase.END
    // để đảm bảo chạy SAU khi TAN tính xong nhiệt độ

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        var player = slotContext.entity();
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(
                Component.literal("Your soul is now protected")
                    .withStyle(ChatFormatting.LIGHT_PURPLE)
            );
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        var player = slotContext.entity();
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(
                Component.literal("The proctection faded. Be careful")
                    .withStyle(ChatFormatting.GRAY)
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        MutableComponent slotLine = Component.literal("  ⬡ ")
            .withStyle(ChatFormatting.GOLD)
            .append(Component.literal("Curios Belt Slot")
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(slotLine);

        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.literal("  ❄ ")
            .withStyle(ChatFormatting.AQUA)
            .append(Component.literal("Điều hòa thân nhiệt")
                .withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("    Khóa nhiệt độ về vùng an toàn")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("    dù ở Nether hay The End.")
            .withStyle(ChatFormatting.GRAY));

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

        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
