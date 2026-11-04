package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Мгновенное убийство одной цели: ближайшее живое существо в радиусе 6 блоков получает
 * 5 ударов с критами за один тик, без ожидания перезарядки. Все цели (мирные мобы, игроки, враждебные).
 */
public class InstaKill extends Module {
    private static final int HITS = 5;

    public InstaKill() { super("InstaKill"); }

    @Override public String getDetails() { return "hits=" + HITS; }

    @Override
    public void onTick() {
        var p = mc().player;
        var world = mc().world;
        if (p == null || world == null || mc().interactionManager == null) return;

        LivingEntity target = null;
        double best = 6.0;
        for (Entity e : world.getEntities()) {
            if (!Targets.valid(p, e, false)) continue;
            double d = p.distanceTo(e);
            if (d <= best) { best = d; target = (LivingEntity) e; }
        }
        if (target == null) return;

        var to = target.getBoundingBox().getCenter().subtract(p.getEyePos());
        p.setYaw((float) (MathHelper.atan2(to.z, to.x) * 180.0 / Math.PI) - 90.0f);
        p.setPitch((float) -(MathHelper.atan2(to.y, Math.sqrt(to.x * to.x + to.z * to.z)) * 180.0 / Math.PI));

        for (int i = 0; i < HITS; i++) Combat.hit(target, true);
        Combat.swing();
        CheatLogger.log(name, "ATTACK", "burst=" + HITS + ";dist=" + String.format("%.2f", best));
    }
}
