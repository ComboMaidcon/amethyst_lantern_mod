package com.example.amethystlantern.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BizarreBrainItem extends Item implements ICurioItem {

    public static final double SAFE_RADIUS = 4.0;
    private static final int TICK_INTERVAL = 10;
    private static final double SCAN_RADIUS = SAFE_RADIUS * 3;

    public BizarreBrainItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (player.tickCount % TICK_INTERVAL != 0) return;
        suppressMobAggro(player);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        player.sendSystemMessage(
            Component.literal("They might think your'e one of them now...")
                .withStyle(ChatFormatting.DARK_PURPLE)
        );
        suppressMobAggro(player);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        player.sendSystemMessage(
            Component.literal("Out of protechtion....Watch out")
                .withStyle(ChatFormatting.GRAY)
        );
    }

    private void suppressMobAggro(Player player) {
        AABB scanBox = player.getBoundingBox().inflate(SCAN_RADIUS);
        List<Mob> nearbyMobs = player.level().getEntitiesOfClass(Mob.class, scanBox);
        double safeRadiusSq = SAFE_RADIUS * SAFE_RADIUS;

        for (Mob mob : nearbyMobs) {
            if (mob.distanceToSqr(player) <= safeRadiusSq) continue;

            if (mob.getTarget() instanceof Player t && t.getUUID().equals(player.getUUID())) {
                mob.setTarget(null);
            }

            mob.targetSelector.getAvailableGoals().removeIf(wrapped -> {
                if (!(wrapped.getGoal() instanceof NearestAttackableTargetGoal<?> goal)) return false;
                try {
                    var field = NearestAttackableTargetGoal.class.getDeclaredField("targetType");
                    field.setAccessible(true);
                    Class<?> type = (Class<?>) field.get(goal);
                    return Player.class.isAssignableFrom(type);
                } catch (Exception e) {
                    return false;
                }
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // Dòng kẻ trên
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        // Slot info
        MutableComponent slotLine = Component.literal("  ⬡ ")
            .withStyle(ChatFormatting.GOLD)
            .append(Component.literal("Curios Head Slot")
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(slotLine);

        // Rarity flavor
        tooltip.add(Component.literal("  ✧ Cổ vật kỳ dị — nguồn gốc bất minh")
            .withStyle(ChatFormatting.DARK_PURPLE));

        // Dòng kẻ giữa
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));

        // Effect chính
        tooltip.add(Component.literal("  ☠ ")
            .withStyle(ChatFormatting.DARK_RED)
            .append(Component.literal("Bóp méo nhận thức")
                .withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("    Quái vật ngoài ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal((int) SAFE_RADIUS + " blocks")
                .withStyle(ChatFormatting.LIGHT_PURPLE))
            .append(Component.literal(" không thể")
                .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    nhắm mục tiêu vào bạn.")
            .withStyle(ChatFormatting.GRAY));

        // Cảnh báo
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("  ⚠ Cảnh báo: ")
            .withStyle(ChatFormatting.RED)
            .append(Component.literal("Quái trong " + (int) SAFE_RADIUS + " blocks")
                .withStyle(ChatFormatting.YELLOW)));
        tooltip.add(Component.literal("    vẫn tấn công bình thường!")
            .withStyle(ChatFormatting.GRAY));

        // Dòng kẻ dưới
        tooltip.add(Component.literal("─────────────────────")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
