package com.redteam.client.modules;

import net.minecraft.util.math.Vec3d;

/**
 * Включён по умолчанию. Когда воздуха под водой остаётся мало (меньше ~5 секунд),
 * автоматически всплывает к поверхности, поэтому урон от утопления не наступает.
 */
public class AntiDrown extends Module {
    public AntiDrown() { super("AntiDrown"); }

    @Override public boolean enabledByDefault() { return true; }

    @Override
    public void onTick() {
        var p = mc().player;
        if (p == null || !p.isTouchingWater()) return;
        if (p.getAir() < 100) {
            Vec3d v = p.getVelocity();
            p.setVelocity(v.x, Math.max(v.y, 0.3), v.z);
        }
    }
}
