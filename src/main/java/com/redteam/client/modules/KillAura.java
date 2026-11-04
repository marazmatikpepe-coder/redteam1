package com.redteam.client.modules;

import java.util.Random;

import com.redteam.client.CheatLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Режимы:
 *  RAGE  - грубая версия: большая дистанция, мгновенный поворот, без проверок (лёгкая цель для античита).
 *  LEGIT - «человечная»: дистанция как у ванили, угол обзора, плавный поворот с шумом,
 *          случайная задержка, только видимые цели.
 */
public class KillAura extends Module {
    public enum Mode { RAGE, LEGIT }

    private Mode mode = Mode.LEGIT;
    private final Random rng = new Random();
    private int extraDelayTicks = 0;

    public KillAura() { super("KillAura"); }

    @Override public void cycleMode() {
        mode = mode == Mode.RAGE ? Mode.LEGIT : Mode.RAGE;
        CheatLogger.log(name, "MODE", getDetails());
    }

    @Override public String getDetails() { return "mode=" + mode; }

    @Override
    public void onTick() {
        var player = mc.player;
        var world = mc.world;
        if (player == null || world == null || mc.interactionManager == null) return;

        double reach = mode == Mode.RAGE ? 6.0 : 3.0;
        LivingEntity target = null;
        double best = Double.MAX_VALUE;

        for (Entity e : world.getEntities()) {
            if (!(e instanceof LivingEntity le) || e == player || !le.isAlive()) continue;
            if (!(e instanceof PlayerEntity) && !(e instanceof HostileEntity)) continue;
            double d = player.distanceTo(e);
            if (d > reach || d >= best) continue;
            if (mode == Mode.LEGIT) {
                if (!player.canSee(e)) continue;
                if (angleTo(player, e) > 70.0) continue; // только то, что почти перед глазами
            }
            best = d;
            target = le;
        }
        if (target == null) return;

        aim(target);

        if (extraDelayTicks > 0) { extraDelayTicks--; return; }
        float needed = mode == Mode.RAGE ? 0.0f : 0.92f;
        if (player.getAttackCooldownProgress(0.5f) < needed) return;

        mc.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
        CheatLogger.log(name, "ATTACK", "mode=" + mode + ";dist=" + String.format("%.2f", best));
        if (mode == Mode.LEGIT) extraDelayTicks = rng.nextInt(3); // 0-2 тика случайной паузы
    }

    private void aim(LivingEntity t) {
        var p = mc.player;
        Vec3d eye = p.getEyePos();
        Vec3d to = t.getBoundingBox().getCenter().subtract(eye);
        float yaw = (float) (MathHelper.atan2(to.z, to.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(MathHelper.atan2(to.y, Math.sqrt(to.x * to.x + to.z * to.z)) * 180.0 / Math.PI);

        if (mode == Mode.RAGE) {
            p.setYaw(yaw);
            p.setPitch(pitch);
        } else {
            float dy = MathHelper.wrapDegrees(yaw - p.getYaw());
            float dp = pitch - p.getPitch();
            float step = 0.25f + rng.nextFloat() * 0.2f; // 25-45% остатка за тик
            p.setYaw(p.getYaw() + dy * step + (rng.nextFloat() - 0.5f) * 0.8f);
            p.setPitch(MathHelper.clamp(p.getPitch() + dp * step + (rng.nextFloat() - 0.5f) * 0.6f, -90f, 90f));
        }
    }

    private static double angleTo(PlayerEntity p, Entity e) {
        Vec3d look = p.getRotationVec(1.0f).normalize();
        Vec3d to = e.getBoundingBox().getCenter().subtract(p.getEyePos()).normalize();
        double dot = MathHelper.clamp(look.dotProduct(to), -1.0, 1.0);
        return Math.toDegrees(Math.acos(dot));
    }
}
