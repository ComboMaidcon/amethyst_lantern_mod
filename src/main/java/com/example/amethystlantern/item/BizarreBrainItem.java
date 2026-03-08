package com.example.amethystlantern.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CurioSlot;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

@CurioSlot(identifier = "head")
/**
 * BizarreBrainItem - Bộ Não Kỳ Quái
 *
 * Khi đeo trên đầu (Curios "head" slot):
 * - Quái vật trong bán kính > 8 blocks hoàn toàn không nhắm vào player
 * - Quái đang đuổi sẽ bị force-drop target nếu cách xa hơn 8 blocks
 * - Quái trong phạm vi 8 blocks vẫn hoạt động bình thường
 *
 * Cơ chế kép:
 * 1. RESET TARGET  — mỗi 10 tick quét tất cả Mob xung quanh,
 *    nếu target là player này VÀ khoảng cách > 8 → setTarget(null)
 * 2. BLOCK PATHFINDING — xóa goal NearestAttackableTargetGoal
 *    khỏi mob ở xa để chúng không thể re-target
 */
public class BizarreBrainItem extends Item implements ICurioItem {

    public static final double SAFE_RADIUS = 8.0;
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
            Component.literal("✦ Bộ Não Kỳ Quái bắt đầu bóp méo nhận thức của quái vật...")
                .withStyle(ChatFormatting.DARK_PURPLE)
        );
        suppressMobAggro(player);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        player.sendSystemMessage(
            Component.literal("✦ Màn che tâm trí tan biến — hãy cẩn thận!")
                .withStyle(ChatFormatting.GRAY)
        );
    }

    private void suppressMobAggro(Player player) {
        AABB scanBox = player.getBoundingBox().inflate(SCAN_RADIUS);
        List<Mob> nearbyMobs = player.level().getEntitiesOfClass(Mob.class, scanBox);
        double safeRadiusSq = SAFE_RADIUS * SAFE_RADIUS;

        for (Mob mob : nearbyMobs) {
            if (mob.distanceToSqr(player) <= safeRadiusSq) continue;

            // Cơ chế 1: Reset target
            if (mob.getTarget() instanceof Player t && t.getUUID().equals(player.getUUID())) {
                mob.setTarget(null);
            }

            // Cơ chế 2: Block pathfinding - xóa goal nhắm vào Player
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
        tooltip.add(Component.literal("Quái vật ngoài " + (int) SAFE_RADIUS + " blocks không nhắm vào bạn")
            .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Quái trong phạm vi " + (int) SAFE_RADIUS + " blocks vẫn nguy hiểm!")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Trang bị: Head slot (Curios)")
            .withStyle(ChatFormatting.GRAY));
    }
}
