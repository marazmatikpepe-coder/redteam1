package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

import net.minecraft.util.math.MathHelper;

/** Полёт без разрешения сервера. Пробел - вверх, Shift - вниз, W - вперёд. */
public class Fly extends Module {
    private boolean fast = false;

    public Fly() { super("Fly"); }

    @Override public void cycleMode() { fast = !fast; CheatLogger.log(name, "MODE", getDetails()); }
    @Override public String getDetails() { return fast ? "speed=fast" : "speed=slow"; }

    @Override
    public void onTick() {
        var p = mc().player;
        if (p == null) return;
        double s = fast ? 0.9 : 0.45;
        double vy = mc().options.jumpKey.isPressed() ? 0.45 : mc().options.sneakKey.isPressed() ? -0.45 : 0.0;
        double vx = 0, vz = 0;
        if (mc().options.forwardKey.isPressed()) {
            double yaw = Math.toRadians(p.getYaw());
            vx = -MathHelper.sin((float) yaw) * s;
            vz = MathHelper.cos((float) yaw) * s;
        }
        p.setVelocity(vx, vy, vz);
        p.fallDistance = 0;
    }
}
