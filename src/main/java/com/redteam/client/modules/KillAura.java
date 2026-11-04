package com.redteam.client.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.redteam.client.CheatLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Кнопка «режим» листает по кругу: LEGIT/все -> RAGE/все -> LEGIT/враж -> RAGE/враж.
 *
 *  LEGIT - «человечная»: 1 цель, дистанция 3, только видимые и почти перед глазами, плавный поворот
 *          с шумом, обычная перезарядка удара, случайные паузы. Крит не используется.
 *  RAGE  - «громкая»: бьёт ВСЕХ в радиусе 6 блоков (до 5 целей за тик), мгновенный поворот,
 *          без ожидания перезарядки, каждый удар с критом.
 *  Цели «все» = любые живые существа (мирные мобы, жители, игроки); «враж» = враждебные мобы и игроки.
 */
public class KillAura extends Module {
    public enum Mode { RAGE, LEGIT }

    private static final int RAGE_MAX_TARGETS = 5;

    private Mode mode = Mode.LEGIT;
    private boolean onlyHostile = false;
    private final Random rng = new Random();
    private int extraDelayTicks = 0;

    public KillAura() { super("KillAura"); }

    @Override public void cycleMode() {
        if (mode == Mode.LEGIT && !onlyHostile) mode = Mode.RAGE;
        else if (mode == Mode.RAGE && !onlyHostile) { mode = Mode.LEGIT; onlyHostile = true; }
        else if (mode == Mode.LEGIT) mode = Mode.RAGE;
        else { mode = Mode.LEGIT; onlyHostile = false; }
        CheatLogger.log(name, "MODE", getDetails());
    }

    @Override public String getDetails() {
        return mode + (onlyHostile ? "/враж" : "/все");
    }

    @Override
    public void onTick() {
        var player = mc().player;
        var world = mc().world;
        if (player == null || world == null || mc().interactionManager == null) return;
        if (mode == Mode.RAGE) rageTick(player, world); else legitTick(player, world);
    }

    private void rageTick(net.minecraft.client.network.ClientPlayerEntity player, net.minecraft.client.world.ClientWorld world) {
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity e : world.getEntities()) {
            if (!Targets.valid(player, e, onlyHostile)) continue;
            if (player.distanceTo(e) > 6.0f) continue;
            targets.add((LivingEntity) e);
        }
        if (targets.isEmpty()) return;
        targets.sort((a, b) -> Float.compare(player.distanceTo(a), player.distanceTo(b)));

        aimInstant(targets.get(0));
        int n = Math.min(RAGE_MAX_TARGETS, targets.size());
        for (int i = 0; i < n; i++) Combat.hit(targets.get(i), true);
        Combat.swing();
        CheatLogger.log(name, "ATTACK", "mode=RAGE;targets=" + n + ";crit=1");
    }

    private void legitTick(net.minecraft.client.network.ClientPlayerEntity player, net.minecraft.client.world.ClientWorld world) {
        LivingEntity target = null;
        double best = Double.MAX_VALUE;
        for (Entity e : world.getEntities()) {
            if (!Targets.valid(player, e, onlyHostile)) continue;
            double d = player.distanceTo(e);
            if (d > 3.0 || d >= best) continue;
            if (!player.canSee(e)) continue;
            if (angleTo(player, e) > 70.0) continue;
            best = d;
            target = (LivingEntity) e;
        }
        if (target == null) return;

        aimSmooth(target);

        if (extraDelayTicks > 0) { extraDelayTicks--; return; }
        if (player.getAttackCooldownProgress(0.5f) < 0.92f) return;

        Combat.hit(target, false);
        Combat.swing();
        CheatLogger.log(name, "ATTACK", "mode=LEGIT;targets=1;dist=" + String.format("%.2f", best));
        extraDelayTicks = rng.nextInt(3); // 0-2 тика случайной паузы
    }

    private float[] angles(LivingEntity t) {
        var p = mc().player;
        Vec3d to = t.getBoundingBox().getCenter().subtract(p.getEyePos());
        float yaw = (float) (MathHelper.atan2(to.z, to.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(MathHelper.atan2(to.y, Math.sqrt(to.x * to.x + to.z * to.z)) * 180.0 / Math.PI);
        return new float[] { yaw, pitch };
    }

    private void aimInstant(LivingEntity t) {
        float[] a = angles(t);
        mc().player.setYaw(a[0]);
        mc().player.setPitch(a[1]);
    }

    private void aimSmooth(LivingEntity t) {
        var p = mc().player;
        float[] a = angles(t);
        float dy = MathHelper.wrapDegrees(a[0] - p.getYaw());
        float dp = a[1] - p.getPitch();
        float step = 0.25f + rng.nextFloat() * 0.2f; // 25-45% остатка за тик
        p.setYaw(p.getYaw() + dy * step + (rng.nextFloat() - 0.5f) * 0.8f);
        p.setPitch(MathHelper.clamp(p.getPitch() + dp * step + (rng.nextFloat() - 0.5f) * 0.6f, -90f, 90f));
    }

    private static double angleTo(PlayerEntity p, Entity e) {
        Vec3d look = p.getRotationVec(1.0f).normalize();
        Vec3d to = e.getBoundingBox().getCenter().subtract(p.getEyePos()).normalize();
        double dot = MathHelper.clamp(look.dotProduct(to), -1.0, 1.0);
        return Math.toDegrees(Math.acos(dot));
    }
}
