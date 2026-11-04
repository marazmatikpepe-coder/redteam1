package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

import net.minecraft.util.math.MathHelper;

/** Ускорение при беге вперёд с автопрыжками (bhop). */
public class Speed extends Module {
    private boolean fast = false;

    public Speed() { super("Speed"); }

    @Override public void cycleMode() { fast = !fast; CheatLogger.log(name, "MODE", getDetails()); }
    @Override public String getDetails() { return fast ? "speed=fast" : "speed=slow"; }

    @Override
    public void onTick() {
        var p = mc.player;
        if (p == null || !mc.options.forwardKey.isPressed()) return;
        if (p.isOnGround()) p.jump();
        double s = fast ? 0.55 : 0.32;
        double yaw = Math.toRadians(p.getYaw());
        p.setVelocity(-MathHelper.sin((float) yaw) * s, p.getVelocity().y, MathHelper.cos((float) yaw) * s);
    }
}
