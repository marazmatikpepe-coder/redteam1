package com.redteam.client.modules;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import com.redteam.client.CheatLogger;

/**
 * Автобот: сам выбирает оружие в хотбаре (меч, иначе топор), идёт к ближайшей цели
 * (враждебный моб или другой игрок), бьёт её и ест золотое яблоко при <= 4 сердцах.
 */
public class AutoFight extends Module {
    private final Random rng = new Random();
    private int eatTicks = 0;
    private int eatCooldown = 0;

    public AutoFight() { super("AutoFight"); }

    @Override
    protected void onDisable() { releaseKeys(); eatTicks = 0; }

    private void releaseKeys() {
        mc().options.forwardKey.setPressed(false);
        mc().options.sprintKey.setPressed(false);
        mc().options.jumpKey.setPressed(false);
        mc().options.useKey.setPressed(false);
    }

    @Override
    public void onTick() {
        var p = mc().player;
        var world = mc().world;
        if (p == null || world == null || mc().interactionManager == null) return;

        if (eatCooldown > 0) eatCooldown--;

        // --- еда ---
        if (eatTicks > 0) {
            eatTicks--;
            mc().options.useKey.setPressed(true);
            if (eatTicks == 0) { mc().options.useKey.setPressed(false); eatCooldown = 100; }
            return;
        }
        if (p.getHealth() <= 8.0f && eatCooldown == 0) {
            for (int i = 0; i < 9; i++) {
                ItemStack s = p.getInventory().getStack(i);
                if (s.isOf(Items.GOLDEN_APPLE) || s.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                    p.getInventory().setSelectedSlot(i);
                    eatTicks = 36;
                    CheatLogger.log(name, "EAT", "slot=" + i + ";hp=" + p.getHealth());
                    return;
                }
            }
        }

        // --- цель ---
        LivingEntity target = null;
        double best = 20.0;
        for (Entity e : world.getEntities()) {
            if (!Targets.valid(p, e, false)) continue;
            double d = p.distanceTo(e);
            if (d < best) { best = d; target = (LivingEntity) e; }
        }
        if (target == null) {
            mc().options.forwardKey.setPressed(false);
            mc().options.sprintKey.setPressed(false);
            mc().options.jumpKey.setPressed(false);
            return;
        }

        selectWeapon();
        aim(target);

        // --- движение ---
        boolean far = best > 2.6;
        mc().options.forwardKey.setPressed(far);
        mc().options.sprintKey.setPressed(far);
        mc().options.jumpKey.setPressed(p.horizontalCollision && p.isOnGround());

        // --- удар ---
        if (best <= 3.0 && p.getAttackCooldownProgress(0.5f) >= 0.92f) {
            mc().interactionManager.attackEntity(p, target);
            p.swingHand(Hand.MAIN_HAND);
            CheatLogger.log(name, "ATTACK", "dist=" + String.format("%.2f", best));
        }
    }

    private void selectWeapon() {
        var inv = mc().player.getInventory();
        int sword = -1, axe = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (sword == -1 && s.isIn(ItemTags.SWORDS)) sword = i;
            if (axe == -1 && s.isIn(ItemTags.AXES)) axe = i;
        }
        int pick = sword != -1 ? sword : axe;
        if (pick != -1 && inv.getSelectedSlot() != pick) inv.setSelectedSlot(pick);
    }

    private void aim(LivingEntity t) {
        var p = mc().player;
        var to = t.getBoundingBox().getCenter().subtract(p.getEyePos());
        float yaw = (float) (MathHelper.atan2(to.z, to.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(MathHelper.atan2(to.y, Math.sqrt(to.x * to.x + to.z * to.z)) * 180.0 / Math.PI);
        float dy = MathHelper.wrapDegrees(yaw - p.getYaw());
        float dp = pitch - p.getPitch();
        float step = 0.3f + rng.nextFloat() * 0.2f;
        p.setYaw(p.getYaw() + dy * step + (rng.nextFloat() - 0.5f) * 0.8f);
        p.setPitch(MathHelper.clamp(p.getPitch() + dp * step + (rng.nextFloat() - 0.5f) * 0.6f, -90f, 90f));
    }
}
